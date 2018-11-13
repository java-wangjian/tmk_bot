package com.zxxkj.freeswitch.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {
    public static void main(String args[]) {

        String msg = "Client Data";

        try {

//创建一个Socket，跟本机的8080端口连接

            Socket socket = new Socket("127.0.0.1", 8080);
            //使用Socket创建PrintWriter和BufferedReader进行读写数据

            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送数据

            pw.println(msg);

            pw.flush();
            //接收数据

            String line = is.readLine();

            System.out.println("received from server：" + line);
            //关闭资源

            pw.close();

            is.close();

            socket.close();
        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
