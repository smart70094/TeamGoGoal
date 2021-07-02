package com.teamgogoal.websocket;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.teamgogoal.utils.ConfigUtils;
import com.teamgogoal.utils.TggRetrofitUtils;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TggWebsocketUtils {

    private static TggWebsocket tggWebsocket = null;



    public static void createWebosocketConnection(Context context, NotificationManager notificationManager) {
        try {
            String url = String.format("wss://%s/teamgogoal/%s", ConfigUtils.SERVER_URL, TggRetrofitUtils.getId());
            tggWebsocket = new TggWebsocket(context, notificationManager, url);
            tggWebsocket.connect();
        } catch (URISyntaxException e) {
            Log.e("createWebosocketConnection", "uri syntax failure:", e);
        }
    }

    public static void send(String requestString) {
        tggWebsocket.send(requestString);
    }
}
