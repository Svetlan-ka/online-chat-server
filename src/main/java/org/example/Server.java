package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Server extends Thread {
    public static LinkedList<Server> serverList = new LinkedList<>();
    private final BufferedReader in;
    private final PrintWriter out;
    private final Socket socket;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        start();
    }


    public static void main(String[] args) throws IOException {
        File settings = new File("settings.txt");
        final int PORT = getPort(settings);

        System.out.println("Сервер запущен!");
        logServer("Сервер был запущен.");

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept(); //ждем подключений
                try {
                    serverList.add(new Server(socket)); //добавляем новое соединение в список
                    System.out.println("Подключился новый клиент: " + socket);
                    logServer("Подключился новый клиент: " + socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine(); //первое сообщение это ник клиента
            out.write(message);
            out.flush();

            while (true) {
                message = in.readLine();
                try {
                    if (message.equals("/exit")) {
                        this.closeSocket();
                        break;
                    }
                } catch (NullPointerException ex) {
                    this.closeSocket();
                }
                if (message != null) {
                    System.out.println(message);
                    logServer(message);
                }
                for (Server vr : serverList) {
                    vr.send(message);
                }
            }
        } catch (IOException ex) {
            this.closeSocket();
        }
    }

    private void send(String message) { //отправка сообщений клиенту
        try {
            out.write(message);
            out.flush();
        } catch (Exception ignored) {
        }
    }

    private void closeSocket() { //отключение клиента
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();

                for (Server vr : serverList) {
                    if (vr.equals(this)) vr.interrupt();
                    serverList.remove(this);
                }

                System.out.println("Клиент " + socket + " отключился");
                logServer("Клиент " + socket + " отключился.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void logServer(String log) throws IOException {
        FileWriter logs = new FileWriter("logs.txt", true);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //выводим дату и время до секунд
        String dateAndTime = format.format(new Date());

        logs.append(dateAndTime)
                .append(" ")
                .append(log)
                .append("\n")
                .flush();
    }

    public static int getPort(File file) {
        int port = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String settings = reader.readLine();
            String[] parts = settings.split(" ");
            port = Integer.parseInt(parts[1]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }
}
