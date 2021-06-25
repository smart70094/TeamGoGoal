package com.teamgogoal.websocket;

import android.util.Log;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TggWebsocketUtils {

    private static TggWebsocket tggWebsocket = null;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void createWebosocketConnection(String url) {
        TggWebsocket tggWebsocket = null;
        try {
            tggWebsocket = new TggWebsocket("ws://885c40d0.ngrok.io/teamgogoal/123456678");
            tggWebsocket.connect();
        } catch (URISyntaxException e) {
            Log.e("createWebosocketConnection", "uri syntax failure:", e);
        }
    }

    private static void send(String requestString) {
        tggWebsocket.send(requestString);
    }
}
