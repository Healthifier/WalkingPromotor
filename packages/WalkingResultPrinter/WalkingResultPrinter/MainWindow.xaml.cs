using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.IO;
using Excel = Microsoft.Office.Interop.Excel;
using System.ComponentModel;
using System.Diagnostics;

namespace WalkingResultPrinter
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        const int MaxDayCount = 14;
#if DEBUG
        static DateTime DefaultLastDay = new DateTime(2015, 6, 21);
#else
        static DateTime DefaultLastDay = DateTime.Today;
#endif

        public MainWindow()
        {
            InitializeComponent();
        }

        private bool ProcessFiles(bool print, DateTime lastDay, int week)
        {
            var excelApp = new Excel.Application();
#if DEBUG
            excelApp.Visible = true;
#endif
            excelApp.DisplayAlerts = false;

            string rootDirectory = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            string outputFileDirectory = Path.Combine(rootDirectory, "sheets");
            Directory.CreateDirectory(outputFileDirectory);
            string userDataFile = @"C:\Omron Healthcare\活動量計アプリケーション\UserData\被験者情報リスト.csv";
            var userData = File.ReadAllLines(userDataFile, Encoding.GetEncoding("Shift_JIS")).Select(l => l.Split(','));
            var errors = new List<string>();
            foreach (string csvDirectory in Directory.GetDirectories(Path.Combine(rootDirectory, "UserData")))
            {
                try
                {
                    string id = Path.GetFileName(csvDirectory);
                    string excelFile = Path.Combine(outputFileDirectory, id + ".xlsx");
                    File.Copy(Path.Combine(rootDirectory, "original.xlsx"), excelFile, true);
                    CreateExcelFile(excelApp, csvDirectory, excelFile, userData, id, print, lastDay, week);
                }
                catch (Exception e)
                {
                    errors.Add(e.ToString());
                }
            }
            excelApp.Quit();
            if (errors.Count > 0)
            {
                File.WriteAllLines(Path.Combine(rootDirectory, "error.txt"), errors);
            }

            return errors.Count == 0;
        }

        private void CreateExcelFile(Excel.Application excelApp, string csvDirectory, string excelFile, IEnumerable<string[]> userData, string id, bool print, DateTime lastDay, int week)
        {
            Excel.Workbook excelWorkbook = excelApp.Workbooks.Open(excelFile);

            try
            {
                CreateSummarySheet(excelWorkbook, csvDirectory, userData, id, lastDay);
                CreateMetsSheet(excelApp, excelWorkbook, csvDirectory, id, lastDay);

                if (print)
                {
                    string[] sheetNamesToPrint = {
                        string.Format("週サマリ{0}週間", week),
                        string.Format("日METs{0}週間", week),
                    };
                    foreach (string sheetName in sheetNamesToPrint)
                    {
                        Excel.Worksheet worksheetToPrint = (Excel.Worksheet)excelWorkbook.Worksheets.get_Item(sheetName);
#if DEBUG
                        worksheetToPrint.PrintOut(Preview: true);
#else
                        worksheetToPrint.PrintOut();
#endif
                    }
                }
            }
            finally
            {
                excelWorkbook.Close(true);
            }
        }

        private void CreateSummarySheet(Excel.Workbook excelWorkbook, string csvDirectory, IEnumerable<string[]> allUserData, string id, DateTime lastDay)
        {
            string csvFile = Path.Combine(csvDirectory, id + ".csv");
            var csvElements = File.ReadAllLines(csvFile, Encoding.GetEncoding("Shift_JIS")).Select(l => l.Split(','));
            Excel.Worksheet datasetWorksheet = (Excel.Worksheet)excelWorkbook.Worksheets.get_Item("元データ貼付シート①　サマリ");

            datasetWorksheet.Cells[1][1].Value2 = id;
            const int dataHeadCsvRow = 3;
            bool dataExistsForLastDay = false;
            for (int csvRow = dataHeadCsvRow; csvRow < csvElements.Count(); csvRow++)
            {
                var date = DateTime.Parse(csvElements.ElementAt(csvRow)[0]);
                if (date.Equals(lastDay))
                {
                    dataExistsForLastDay = true;
                }
                int dateDiffFromLastDay = Convert.ToInt32(lastDay.Subtract(date).TotalDays);
                if (dateDiffFromLastDay < MaxDayCount && dateDiffFromLastDay >= 0)
                {
                    int excelRow = dataHeadCsvRow + MaxDayCount - dateDiffFromLastDay;
                    for (int excelColumn = 1; excelColumn <= 26; excelColumn++)
                    {
                        Excel.Range cell = datasetWorksheet.Cells[excelColumn][excelRow];
                        cell.Value2 = csvElements.ElementAt(csvRow)[excelColumn - 1];
                    }
                }
            }

            int[] headerColumnsToSummarizeCsvIndex = { 25, 22, 23, 24 };
            int headerDataExcelRow = 20;
            for (int headerColumnIndex = 0; headerColumnIndex < headerColumnsToSummarizeCsvIndex.Count(); headerColumnIndex++)
            {
                int csvColumn = headerColumnsToSummarizeCsvIndex[headerColumnIndex];
                for (int csvRow = dataHeadCsvRow; csvRow < csvElements.Count(); csvRow++)
                {
                    string value = csvElements.ElementAt(csvRow)[csvColumn];
                    if (!string.IsNullOrEmpty(value))
                    {
                        datasetWorksheet.Cells[headerColumnIndex + 1][headerDataExcelRow].Value2 = value;
                    }
                }
            }
            datasetWorksheet.Cells[5][headerDataExcelRow].Value2 = lastDay.ToString("yyyy/MM/dd");
            string[] userData = allUserData.FirstOrDefault((string[] d) => { return d[0] == id; });
            if (userData != null)
            {
                DateTime zeroTime = new DateTime(1, 1, 1);
                var bornDate = DateTime.Parse(userData[2]);
                var ageSpan = DateTime.Today - bornDate;
                // Because we start at year 1 for the Gregorian calendar, we must subtract a year here.
                int age = (zeroTime + ageSpan).Year - 1;
                datasetWorksheet.Cells[6][headerDataExcelRow].Value2 = age;

                string sex = userData[3] == "0" ? "男性" : "女性";
                datasetWorksheet.Cells[7][headerDataExcelRow].Value2 = sex;
            }

            // We need to keep at least one row of data in the original excel file so that we don't lose the chart label info in the file.
            // This code deletes that data if it remains unchanged. Fk excel.
            if (!dataExistsForLastDay)
            {
                for (int column = 1; column <= 26; column++)
                {
                    Excel.Range cell = datasetWorksheet.Cells[column][dataHeadCsvRow + MaxDayCount];
                    cell.Value2 = "";
                }
            }
        }

        private void CreateMetsSheet(Excel.Application excelApp, Excel.Workbook excelWorkbook, string csvDirectory, string id, DateTime lastDay)
        {
            Excel.Worksheet datasetWorksheet = (Excel.Worksheet)excelWorkbook.Worksheets.get_Item("元データ貼付シート②　METs");
            for (int dateDiffFromLastDay = 0; dateDiffFromLastDay < MaxDayCount; dateDiffFromLastDay++)
            {
                var date = lastDay.AddDays(-dateDiffFromLastDay);
                var csvFile = Path.Combine(csvDirectory, string.Format("{0}10SECMETS_{1}.csv", id, date.ToString("yyyyMMdd")));
                if (!File.Exists(csvFile))
                {
                    continue;
                }

                const int rowCount = 8644, columnCount = 3;
                Excel.Workbook csvWorkbook = excelApp.Workbooks.Open(csvFile);
                try
                {
                    Excel.Worksheet csvWorksheet = (Excel.Worksheet)csvWorkbook.Worksheets[1];
                    var csvStartCell = (Excel.Range)csvWorksheet.Cells[1, 1];
                    var csvEndCell = (Excel.Range)csvWorksheet.Cells[rowCount, columnCount];
                    var csvElements = csvWorksheet.Range[csvStartCell, csvEndCell].Value2;

                    const int columnCountPerMetsDataset = 7;
                    int headColumn = columnCountPerMetsDataset * (MaxDayCount - dateDiffFromLastDay - 1) + 1;
                    var excelStartCell = (Excel.Range)datasetWorksheet.Cells[1, headColumn];
                    var excelEndCell = (Excel.Range)datasetWorksheet.Cells[rowCount, headColumn + columnCount - 1];
                    datasetWorksheet.Range[excelStartCell, excelEndCell].Value2 = csvElements;
                }
                finally
                {
                    csvWorkbook.Close(false);
                }
            }
        }

        private void ShowFileOperationCompletedMessage(bool succeeded)
        {
            if (succeeded)
            {
                MessageBox.Show(this, "正常に処理が完了しました。", Title);
            }
            else
            {
                MessageBox.Show(this, "処理が完了しました。正常に処理が行えなかったユーザーが存在します。\nコンピューターを再起動するとエラーが解決する場合があります。\n再度エラーが発生する場合は、error.txt を開発者に送信してください。", Title);
            }
        }

        private void DisableControls()
        {
            AutoGenerateCSVButton.IsEnabled = false;
            PrintButton.IsEnabled = false;
            OutputButton.IsEnabled = false;
            OneWeekRadioButton.IsEnabled = false;
            TwoWeeksRadioButton.IsEnabled = false;
        }

        private void EnableControls()
        {
            AutoGenerateCSVButton.IsEnabled = true;
            PrintButton.IsEnabled = true;
            OutputButton.IsEnabled = true;
            OneWeekRadioButton.IsEnabled = true;
            TwoWeeksRadioButton.IsEnabled = true;
        }

        private void PrintButton_Click(object sender, RoutedEventArgs e)
        {
            StartProcessingFiles(true);
        }

        private void OutputButton_Click(object sender, RoutedEventArgs e)
        {
            StartProcessingFiles(false);
        }

        private void StartProcessingFiles(bool print)
        {
            int week = OneWeekRadioButton.IsChecked == true ? 1 : 2;
            DateTime lastDay = ChartDatePicker.SelectedDate.GetValueOrDefault(DefaultLastDay);
            var worker = new BackgroundWorker();
            bool succeeded = true;
            worker.DoWork += (s, args) =>
            {
                succeeded = ProcessFiles(print, lastDay, week);
            };
            worker.RunWorkerCompleted += (s, args) =>
            {
                EnableControls();
                ShowFileOperationCompletedMessage(succeeded);
            };
            DisableControls();
            worker.RunWorkerAsync();
        }

        private void AutoGenerateCSVButton_Click(object sender, RoutedEventArgs e)
        {
            var worker = new BackgroundWorker();
            worker.DoWork += (s, args) =>
            {
                Process process = new Process();
                process.StartInfo.FileName = "AutoIt3.exe";
                process.StartInfo.Arguments = "auto.au3";
                process.Start();
                process.WaitForExit();
            };
            worker.RunWorkerCompleted += (s, args) =>
            {
                EnableControls();
                Activate();
                MessageBox.Show(this, "正常に処理が完了しました。", Title);
            };
            DisableControls();
            worker.RunWorkerAsync();
        }

        private void HelpMenuItem_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Process.Start("Readme.txt");
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Readme.txt が存在しません。", Title);
            }
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            ChartDatePicker.SelectedDate = DefaultLastDay;
        }
    }
}
