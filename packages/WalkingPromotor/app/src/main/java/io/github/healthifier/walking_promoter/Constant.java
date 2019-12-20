package io.github.healthifier.walking_promoter;

import java.util.ArrayList;

public class Constant {

    private static final boolean LOCAL_SERVER = false;
    public static final String TAG = "WalkingPromotor";
    public static final int WEARING_LIMIT = 8 * 60;

    public static ArrayList<String> serverHosts;

    static {
        if (LOCAL_SERVER) {
            ArrayList<String> urls = new ArrayList<>();
            urls.add("http://10.0.2.2:8000");
            serverHosts = urls;
        } else {
            ArrayList<String> urls = new ArrayList<>();
            urls.add("http://192.168.0.101:8000");
            urls.add("http://192.168.0.102:8000");
            urls.add("http://192.168.0.103:8000");
            urls.add("http://192.168.0.104:8000");
            serverHosts = urls;
        }
    }
}
