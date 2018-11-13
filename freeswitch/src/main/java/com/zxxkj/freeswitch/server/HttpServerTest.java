package com.zxxkj.freeswitch.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerTest {
    //连接FS服务
    public void connectFSAndSubscibeFSEvent() {
        ExecutorService pool = Executors.newCachedThreadPool();
        HttpServer server;

        try {
            server = HttpServer.create(new InetSocketAddress(8080), 10);
//            server.createContext("/fsapi/", new DirectoryHandler());
            //可以通过设置一个线程组由线程来决定执行的过程
            server.setExecutor(pool);
            server.start();
            System.out.println("server started:");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new HttpServerTest().connectFSAndSubscibeFSEvent();
    }

}
