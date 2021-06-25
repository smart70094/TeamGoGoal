package com.teamgogoal.websocket;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tech.gusavila92.websocketclient.WebSocketClient;

public class TggWebsocket extends WebSocketClient{

    private Map reqAndResMap = new ConcurrentHashMap<String, String>();

    public TggWebsocket(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen() {
        System.out.println("opening!");
    }

    @Override
    public void onTextReceived(String message) {
        System.out.println("receiver:" + message);
    }

    @Override
    public void onBinaryReceived(byte[] data) {
        System.out.println("on binary received");
    }

    @Override
    public void onPingReceived(byte[] data) {
        System.out.println("on ping receiverd");
    }

    @Override
    public void onPongReceived(byte[] data) {
        System.out.println("on pong receiverd");
    }

    @Override
    public void onException(Exception e) {
        System.out.println("exception:" + e.toString());
    }

    @Override
    public void onCloseReceived() {
        System.out.println("close");
    }

    public static void main(String[] args) throws URISyntaxException {
        TggWebsocket tggWebsocket = new TggWebsocket("ws://885c40d0.ngrok.io/teamgogoal/123456678");
        tggWebsocket.setConnectTimeout(10000);
        tggWebsocket.setReadTimeout(60000);
        tggWebsocket.enableAutomaticReconnection(5000);
        tggWebsocket.connect();

        String requestString =
                "{\n" +
                "\"handler\":\"sendMessage\",\n" +
                "\"abc\":\"123\"\n" +
                "}";

        tggWebsocket.send(requestString);


    }
}
