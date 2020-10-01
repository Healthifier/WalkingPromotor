package io.github.healthifier.walking_promoter.models;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.healthifier.walking_promoter.App;
import io.github.healthifier.walking_promoter.BuildConfig;
import io.github.healthifier.walking_promoter.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Database extends SQLiteOpenHelper {

    // <editor-fold desc="SQLiteOpenHelper">

    private static final String DB_NAME = "walkingPromoter.sqlite";
    private static final int DB_VERSION = 4;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SQUARE_STEP_IDS_TSV = "square_step_ids_tsv";
    private final Activity _activity;

    private ArrayList<Integer> cacheSquareStepIds = new ArrayList<>(Arrays.asList(17, 18, 59, 60, 61, 62, 76));
    private String cacheUserId = null;

    public static final class StepCount {
        public final String userId;
        public final String date;
        public final int count;
        public final int waringTime;

        public StepCount(String userId, Calendar cal, int count, int waringTime) {
            this(userId, DATE_FORMAT.format(cal.getTime()), count, waringTime);
        }

        public StepCount(String userId, String date, int count, int waringTime) {
            this.userId = userId;
            this.date = date;
            this.count = count;
            this.waringTime = waringTime;
        }

        @Override
        public String toString() {
            return String.format("userId=%s, date=%s, count=%d wear=%d", userId, date, count, waringTime);
        }
    }

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        _activity = null;
        setDummyIfEmpty();
    }

    public Database(Activity activity) {
        super(activity, DB_NAME, null, DB_VERSION);
        _activity = activity;
        setDummyIfEmpty();
        updateTitle();
    }

    public void updateTitle() {
        if (_activity != null) {
            String dbg = BuildConfig.DEBUG ? "[DEBUG] " : "";
            String userId = getUserId();
            if (userId.isEmpty()) {
                userId = "ユーザIDが未設定です";
            }
            String nowStr = TextHelper.iso8601(CalendarHelper.getToday().getTime());
            String syncStr = TextHelper.iso8601(getLatestSynchronizedAt().getTime());
            String verName = App.getVersionName(_activity);
            int verCode = App.getVersionCode(_activity);
            String title = String.format("%s ver.%s/%d %s (now:%s, sync:%s)", dbg, verName, verCode, userId, nowStr, syncStr);
            _activity.setTitle(title);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user_data (" +
            "key TEXT NOT NULL PRIMARY KEY," +
            "value TEXT NOT NULL" +
            ");");

        db.execSQL("CREATE TABLE goal (" +
            "date TEXT NOT NULL PRIMARY KEY," +
            "count INTEGER NOT NULL" +
            ");");

        db.execSQL("CREATE TABLE step_count (" +
            "user_id TEXT NOT NULL," +
            "date TEXT NOT NULL," +
            "count INTEGER NOT NULL," +
            "PRIMARY KEY (user_id, date));");

        db.execSQL("CREATE TABLE hosts (" +
            "url TEXT NOT NULL," +
            "synchronized_at INTEGER NOT NULL" +
            ");");
        v3_addWearingTimeToStepCount(db);
        v4_addSquareStepRecord(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3 && newVersion >= 3) {
            v3_addWearingTimeToStepCount(db);
        }
        if (oldVersion < 4 && newVersion >= 4) {
            v4_addSquareStepRecord(db);
        }
    }

    private void v3_addWearingTimeToStepCount(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE step_count ADD COLUMN wearing_time INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("UPDATE goal SET count = count / 7");
    }

    private void v4_addSquareStepRecord(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE square_step_record (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "exampleChecked INTEGER NOT NULL," +
            "correctPercentage INTEGER NOT NULL," +
            "solvedMilliseconds INTEGER NOT NULL" +
            ");");
    }

    // </editor-fold>


    public void setDummyIfEmpty() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        /*if (getUserId().isEmpty() == false) {
            return;       // TODO: uncomment out this line
        }*/

        // 以下都竹がコメントアウト
        /*
        App.logDebug("---- set dummy data ----");
        Random rand = new Random(0);
        ArrayList<String> allUserId = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            allUserId.add("User" + (char) ('A' + i));
        }
        String myUserId = allUserId.get(1);

        // set userId
        setUserId(myUserId);

        // generate goals
        Calendar startDate = CalendarHelper.startDateOfExperiment();
        for (int i = 0; i < 4 * 2; i++) {
            setGoal(CalendarHelper.addDay(startDate, i * 7), (rand.nextInt(17000) + 3000));
        }
        // generate step data
        ArrayList<StepCount> list = new ArrayList<>();
        for (String userId : allUserId) {
            for (int i = 0; i < 7 * 4 * 2; i++) {
                list.add(new StepCount(userId, CalendarHelper.addDay(startDate, i), rand.nextInt(17000) + 3000, 1000));
            }
        }
        updateStepCountRecords(list);

         */

    }

    // <editor-fold desc="user_data">

    public void setUserId(String userId) {
        cacheUserId = null;
        setUserData(KEY_USER_ID, userId);
    }

    public
    @NonNull
    String getUserId() {
        if (cacheUserId != null) {
            return cacheUserId;
        }
        cacheUserId = getUserData(KEY_USER_ID);
        if (cacheUserId == null) {
            return "";
        }
        return cacheUserId;
    }

    private void parseSquareStepIds(String squareStepIdsTsv) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (String idText : squareStepIdsTsv.split("\t")) {
            try {
                ids.add(Integer.parseInt(idText));
            } catch (NumberFormatException e) {
            }
        }
        if (!ids.isEmpty()) {
            cacheSquareStepIds = ids;
        }
    }

    public void setSquareStepIds(String squareStepIdsTsv) {
        setUserData(KEY_SQUARE_STEP_IDS_TSV, squareStepIdsTsv);
        parseSquareStepIds(squareStepIdsTsv);
    }

    public
    @NonNull
    List<Integer> getSquareStepIds() {
        String squareStepIdsTsv = getUserData(KEY_SQUARE_STEP_IDS_TSV);
        parseSquareStepIds(squareStepIdsTsv != null ? squareStepIdsTsv : "");
        return cacheSquareStepIds;
    }

    private void setUserData(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put("key", key);
        cv.put("value", value);
        replace("user_data", cv);
    }

    private String getUserData(String key) {
        String query = "SELECT value FROM user_data WHERE key = ?";
        String[] params = new String[]{key};
        return selectString(query, params);
    }

    public void setSquareStepRecord(SquareStepRecord record) {
        ContentValues cv = new ContentValues();
        cv.put("id", record.id);
        cv.put("exampleChecked", record.exampleChecked ? 1 : 0);
        cv.put("correctPercentage", record.correctPercentage);
        cv.put("solvedMilliseconds", record.solvedMilliseconds);
        replace("square_step_record", cv);
    }

    public
    @Nullable
    SquareStepRecord getSquareStepRecord(int stepId) {
        String query = "SELECT * FROM square_step_record WHERE id = ?";
        String[] params = new String[]{stepId + ""};
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, params)
        ) {
            if (cursor.moveToNext()) {
                return new SquareStepRecord(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
            }
        }
        return null;
    }

    // </editor-fold>

    // <editor-fold desc="goal">

    public void setGoal(Calendar cal, int count) {
        ContentValues cv = new ContentValues();
        cv.put("date", DATE_FORMAT.format(cal.getTime()));
        cv.put("count", count);
        replace("goal", cv);
    }

    public int getGoal(Calendar cal) {
        String query = "SELECT count FROM goal WHERE date = ?";
        String[] params = {DATE_FORMAT.format(cal.getTime())};
        return selectInt(query, params, 0);
    }

    // </editor-fold>

    // <editor-fold desc="step_count">

    public void updateStepCountRecords(List<StepCount> records) {
        String stepQuery = "SELECT count FROM step_count WHERE user_id = ? AND date = ?";

        try (SQLiteDatabase db = getWritableDatabase()) {
            db.beginTransaction();
            for (StepCount record : records) {
                try {
                    final String dateStr = DATE_FORMAT.format(DATE_FORMAT.parse(record.date));
                    int stepCount = -1;
                    try (Cursor cursor = db.rawQuery(stepQuery, new String[]{record.userId, dateStr})) {
                        if (cursor.moveToNext()) {
                            if (cursor.isNull(0) == false) {
                                stepCount = cursor.getInt(0);
                            }
                        }
                    }
                    ContentValues cv = new ContentValues();
                    cv.put("user_id", record.userId);
                    cv.put("date", dateStr);
                    cv.put("count", record.count);
                    cv.put("wearing_time", record.waringTime);
                    if (stepCount == -1) {
                        db.insert("step_count", null, cv);
                    } else if (stepCount < record.count) {
                        db.replace("step_count", null, cv);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public int getMyStepCount() {
        String query = "SELECT SUM(count) FROM step_count WHERE user_id = ?";
        String[] params = {
            getUserId()
        };
        return selectInt(query, params, 0);
    }

    public int getMyStepCount(Calendar date) {
        String query = "SELECT AVG(count) FROM step_count"
            + " WHERE user_id = ? AND date = ?";
        String[] params = {
            getUserId(),
            DATE_FORMAT.format(date.getTime()),
        };
        return selectInt(query, params, 0);
    }

    public double getMyAvgStepCount(Calendar start, Calendar inclusiveEnd) {
        String query = "SELECT AVG(count) FROM step_count"
            + " WHERE user_id = ? AND ? <= date AND date <= ? AND wearing_time >= ? AND count > 0";
        String[] params = {
            getUserId(),
            DATE_FORMAT.format(start.getTime()),
            DATE_FORMAT.format(inclusiveEnd.getTime()),
            String.valueOf(Constant.WEARING_LIMIT)
        };
        return selectDouble(query, params, 0);
    }

    public int getEveryoneStepCount(Calendar start, Calendar inclusiveEnd) {
        String query = "SELECT SUM(count) FROM step_count WHERE ? <= date AND date <= ?";
        String[] params = {
            DATE_FORMAT.format(start.getTime()),
            DATE_FORMAT.format(inclusiveEnd.getTime())
        };
        return selectInt(query, params, 0);
    }

    public
    @Nullable
    Fraction getProgress(Calendar today) {
        String query = "SELECT COUNT(DISTINCT(user_id)) FROM step_count";
        int max = selectInt(query, null, 0);
        if (max == 0) {
            return null;
        }
        String query2 = query + " WHERE date = ?";
        String[] params = {
            DATE_FORMAT.format(today.getTime())
        };
        int current = selectInt(query2, params, 0);

        return new Fraction(current, max);
    }

    // </editor-fold>

    // <editor-fold desc="hosts">

    public void updateHosts(List<String> hosts) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM hosts");

            for (String host : hosts) {
                ContentValues cv = new ContentValues();
                cv.put("url", host);
                cv.put("synchronized_at", 0);
                db.insert("hosts", null, cv);
            }
        }
    }

    public void updateSynchronizedAt(String host, Date date) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("synchronized_at", date.getTime());
            db.update("hosts", cv, "url = ?", new String[]{host});
        }
    }

    public String nextHost() {
        String query = "SELECT url FROM hosts ORDER BY synchronized_at ASC LIMIT 1";
        return selectString(query, null);
    }

    public Calendar getLatestSynchronizedAt() {
        String query = "SELECT MAX(synchronized_at) FROM hosts";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectLong(query, null, 0));
        return cal;
    }

    // </editor-fold>

    /**
     * 「歩行データ」をすべて削除します
     */
    public boolean resetCount() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM step_count");
        }

        int nStep = selectInt("SELECT COUNT(*) FROM step_count", null, 0);
        return nStep == 0;
    }

    /**
     * 「目標」をすべて削除します
     */
    public boolean resetGoal() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM goal");
        }

        int nGoal = selectInt("SELECT COUNT(*) FROM goal", null, 0);
        return nGoal == 0;
    }

    public void truncateAll() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM user_data");
            db.execSQL("DELETE FROM goal");
            db.execSQL("DELETE FROM step_count");
//            db.execSQL("DELETE FROM hosts");
            db.execSQL("VACUUM");
        }
    }

    // <editor-fold desc="private utility">

    private void replace(String tableName, ContentValues cv) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.replace(tableName, null, cv);
        }
    }

    private int selectInt(String query, String[] params, int notFound) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, params);
        ) {
            if (cursor.moveToNext()) {
                if (cursor.isNull(0) == false) {
                    return cursor.getInt(0);
                }
            }
        }
        return notFound;
    }

    private double selectDouble(String query, String[] params, double notFound) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, params);
        ) {
            if (cursor.moveToNext()) {
                if (cursor.isNull(0) == false) {
                    return cursor.getDouble(0);
                }
            }
        }
        return notFound;
    }

    private long selectLong(String query, String[] params, int notFound) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, params);
        ) {
            if (cursor.moveToNext()) {
                if (cursor.isNull(0) == false) {
                    return cursor.getLong(0);
                }
            }
        }
        return notFound;
    }

    private String selectString(String query, String[] params) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, params);
        ) {
            if (cursor.moveToNext()) {
                if (cursor.isNull(0) == false) {
                    return cursor.getString(0);
                }
            }
        }
        return null;
    }

    // </editor-fold>
}
