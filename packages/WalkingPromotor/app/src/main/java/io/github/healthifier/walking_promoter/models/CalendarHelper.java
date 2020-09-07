package io.github.healthifier.walking_promoter.models;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarHelper {

    //private static final Calendar EXPERIMENT_START = create(2017, 9, 21);
    private static final Calendar EXPERIMENT_START = create(2020, 8, 6);

    public static Calendar getToday() {
        return Calendar.getInstance();
    }

    public static Calendar startDateOfExperiment() {
        return EXPERIMENT_START;
    }

    public static Calendar startDateOfLastWeek() {
        // 最大8日前、最小2日前で、曜日がEXPERIMENT_STARTと等しい。
        Calendar today = getToday();
        int dwOrigin = EXPERIMENT_START.get(Calendar.DAY_OF_WEEK);
        int dwToday = today.get(Calendar.DAY_OF_WEEK);
        int offset = (dwToday - dwOrigin + 7) % 7;
        if (offset < 2) { // 最小[N]日前
            offset += 7;
        }
        Calendar ret = addDay(today, -offset);
        assert ret.get(Calendar.DAY_OF_WEEK) == dwOrigin;
        return ret;
    }

    public static Calendar startDateOfNextWeek() {
        return addDay(startDateOfLastWeek(), 7);
    }

    public static Calendar create(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        return c;
    }

    public static Calendar addDay(Calendar cal, int day) {
        Calendar c = (Calendar) cal.clone();
        c.add(Calendar.DATE, day);
        return c;
    }

    public static ArrayList<Calendar> createDailyList(Calendar start, Calendar inclusiveEnd) {
        return createList(start, inclusiveEnd, 1);
    }

    public static ArrayList<Calendar> createWeeklyList(Calendar start, Calendar inclusiveEnd) {
        return createList(start, inclusiveEnd, 7);
    }

    private static ArrayList<Calendar> createList(Calendar start, Calendar inclusiveEnd, int gap) {
        ArrayList<Calendar> ret = new ArrayList<>();
        while (start.compareTo(inclusiveEnd) <= 0) {
            ret.add(start);
            start = addDay(start, gap);
        }
        return ret;
    }
}
