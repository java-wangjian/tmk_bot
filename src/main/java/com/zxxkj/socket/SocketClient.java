package com.zxxkj.socket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClient {
    public static WebSocketClient client;
    // 搭建客户端
    public static void main(String[] args)  {
        try {
            webSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void webSocket() throws URISyntaxException {
        client=new WebSocketClient(new URI("ws://47.93.255.115:18080"),new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("打开链接");
            }

            @Override
            public void onMessage(String s) {
                System.out.println("收到消息"+s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("链接已关闭");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                System.out.println("发生错误已关闭");
            }
        };
        client.connect();
    }
}
