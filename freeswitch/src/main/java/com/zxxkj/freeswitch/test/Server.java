package com.zxxkj.freeswitch.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String args[]) {

        try {

//创建一个ServerSocket监听8080端口

            ServerSocket server = new ServerSocket(8080);
            Socket socket = server.accept();
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = is.readLine();
            System.out.println("received from client：" + line);
            //创建PrintWriter，用于发送数据

            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            pw.println("received data: " + line);
            //关闭资源

            pw.close();

            is.close();
            socket.close();

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}