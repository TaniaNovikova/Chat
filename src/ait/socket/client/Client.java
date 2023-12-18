package ait.socket.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Timer;

public class Client {
    // Надо написать консольный чат клиент.
    // В отличии от клиента, который сделали в классе,
    // чат клиент должен отправлять сообщения на сервер не дожидаясь
    // получения ответа от сервера. И печатать сообщения полученные от сервера,
    // не зависимо от того пишет что-то пользователь, или нет.
    // Т. е. отправитель и получатель, это две независимые задачи внутри клиентской аппликации.

    public static void main(String[] args) throws IOException {
        String serverHost = "127.0.0.1"; // localhost
        int port = 9000;
        try (Socket socket = new Socket(serverHost, port)) {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter socketWriter = new PrintWriter(outputStream);
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(inputStream));

            Thread sender = new Thread(new Runnable() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                // System.out.println("Enter your message or type exit for quit");
                String message = br.readLine();

                @Override
                public void run() {
                    while (!"exit".equalsIgnoreCase(message)) {
                        try {
                            message = br.readLine();
                            socketWriter.println(message);
                            socketWriter.flush();
                            message = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            sender.start();
            Thread receiver = new Thread(new Runnable() {
                String message;
                @Override
                public void run() {
                    try {
                        message = socketReader.readLine();
                        while (message!=null){
                            System.out.println("Message: "+ message+ " "+ LocalTime.now());
                            message=socketReader.readLine();
                        }
                        socketWriter.close();
                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

