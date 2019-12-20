package io.github.healthifier.walking_promoter.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CalendarHelper;
import io.github.healthifier.walking_promoter.models.Client;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.Fraction;

import java.util.Calendar;
import java.util.Date;

public class SyncFragment extends Fragment {
    private enum DataType {Goal, StepCount}

    private static final int WAIT_TIME_TO_SYNC_ON_SUCCESS = 10 * 1000;
    private static final int WAIT_TIME_TO_SYNC_ON_FAILURE = 1 * 1000;
    private static final String PASSWORD = "1234";

    private final Handler handler;
    private boolean asyncStart = false;

    private Database db;
    private Button button;
    private TextView logTextView;
    private TextView msgTextView;
    private ProgressBar progressBar;

    public SyncFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);

        button = v.findViewById(R.id.button);
        db = new Database(getActivity());
        String userId = db.getUserId();
        button.setText("ユーザID: " + userId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(new Runnable() {
                    @Override
                    public void run() {
                        showUserIdDialog();
                    }
                });
            }
        });

        Button resetGoal = v.findViewById(R.id.buttonResetGoal);
        resetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(new Runnable() {
                    @Override
                    public void run() {
                        resetData(DataType.Goal);
                    }
                });
            }
        });

        Button resetCount = v.findViewById(R.id.buttonResetCount);
        resetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(new Runnable() {
                    @Override
                    public void run() {
                        resetData(DataType.StepCount);
                    }
                });
            }
        });

        logTextView = v.findViewById(R.id.textView);
        progressBar = v.findViewById(R.id.progressBar);
        msgTextView = v.findViewById(R.id.textView2);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Database db = new Database(getActivity());
        asyncStart = true;
        handler.postDelayed(getRequestRunnable(db), 100);
    }

    @Override
    public void onPause() {
        asyncStart = false;
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }


    private void showPasswordDialog(final Runnable runnable) {
        final EditText editView = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
            .setTitle("パスワードを入力してください")
            .setView(editView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String password = editView.getText().toString();
                    if (PASSWORD.equals(password)) {
                        runnable.run();
                    } else {
                        Toast.makeText(getActivity(),
                            "パスワードが間違っています",
                            Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing;
                }
            })
            .show();
    }

    private void showUserIdDialog() {
        final Database db = new Database(getActivity());
        final String userId = db.getUserId();

        final EditText editView = new EditText(getActivity());
        editView.setText(userId);
        new AlertDialog.Builder(getActivity())
            .setTitle("ユーザIDを入力してください")
            .setView(editView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String newUserId = editView.getText().toString();
                    if (newUserId != null && newUserId.length() != 0 && !newUserId.equals(userId)) {
                        updateUserId(db, newUserId);
                    }
                }
            })
            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing;
                }
            })
            .show();
    }

    private void updateUserId(Database db, String newUserId) {
        db.setUserId(newUserId);
        db.updateTitle();
        button.setText("ユーザID: " + newUserId);
        Toast.makeText(getActivity(),
            "ユーザIDを変更しました",
            Toast.LENGTH_LONG).show();
    }

    private Runnable getRequestRunnable(final Database db) {
        return new Runnable() {
            public void run() {
                final String url = db.nextHost();
                if (url == null) {
                    updateProgress("URLが設定されていません。");
                    return;
                }

                updateProgress("接続を開始します: " + url);
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String message = Client.updateData(db, url);
                        db.updateSynchronizedAt(url, new Date());
                        Client.updateSquareStepIds(db, url);
                        return message;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        boolean success;
                        if (msg == null) {
                            updateProgress("データを同期しました");
                            success = true;
                        } else if (msg.startsWith("+")) {
                            updateProgress("データを同期しました: " + msg.substring(1));
                            success = true;
                        } else {
                            updateProgress("同期に失敗しました: " + msg);
                            success = false;
                        }
                        if (asyncStart) {
                            int wait = success ? WAIT_TIME_TO_SYNC_ON_SUCCESS : WAIT_TIME_TO_SYNC_ON_FAILURE;
                            handler.postDelayed(getRequestRunnable(db), wait);
                        }
                    }
                }.execute();
            }
        };
    }

    private void resetData(DataType type) {
        boolean success;
        String name = "";
        switch (type) {
            case Goal:
                success = db.resetGoal();
                name = "目標";
                break;
            case StepCount:
                success = db.resetCount();
                name = "歩数";
                break;
            default:
                success = false;
        }
        String msg = success ? name + "をリセットしました" : "データを消去できませんでした";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void updateProgress(String str) {
        // 進捗更新
        Fraction frac = db.getProgress(CalendarHelper.getToday());
        if (frac != null) {
            msgTextView.setText(String.format("%d人中%d人のデータが最新です", frac.denominator, frac.numerator));
            progressBar.setMax(frac.denominator);
            progressBar.setProgress(frac.numerator);
        } else {
            msgTextView.setText("データがありません");
            progressBar.setMax(0);
            progressBar.setProgress(0);
        }
        // ログに追記
        Date now = Calendar.getInstance().getTime();
        logTextView.append(String.format("(%s) %s%n", now, str));
    }
}
