package io.github.healthifier.walking_promoter.models;

import io.github.healthifier.walking_promoter.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Client {

    public static String updateData(Database db, String host) {
        /*
         * # めんどくさいので、以下のルールで
         * 戻り値が null -> 成功
         * 戻り値が "+" で始まる -> 成功
         * それ以外 -> 失敗
         */
        try {
            URL url = new URL(host + "/v2/walked.tsv");
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3 * 1000);
                try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                     BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                ) {
                    int n = readTSV(db, host, r);
                    return String.format("+全部で%d件のデータを取得しました。", n);
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static void updateSquareStepIds(Database db, String host) {
        try {
            URL url = new URL(host + "/steps.tsv");
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3 * 1000);
                try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                     BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                ) {
                    db.setSquareStepIds(r.readLine().trim());
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }

    private static int readTSV(Database db, String host, BufferedReader reader) throws IOException {
        ArrayList<Database.StepCount> list = new ArrayList<>();

        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            String[] cells = line.split("\t", 4);
            if (cells.length != 4) continue;
            String date = cells[0].trim();
            String number = cells[1].trim();
            String wearing = cells[2].trim();
            String userId = cells[3].trim();

            int num = Integer.parseInt(number);
            int wear = Integer.parseInt(wearing);
            Database.StepCount item = new Database.StepCount(userId, date, num, wear);
            list.add(item);
            App.logDebug(item);
        }
        db.updateStepCountRecords(list);
        return list.size();
    }
}
