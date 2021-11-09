import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static boolean stop = false;

    public static void main(String[] args) {
//        Написать клиент - серверное приложение с возможностью бана пользователя со стороны сервера и
//        подсчетом количества сообщений от конкретного ip.
        try {
            clientSocket = new Socket("localhost", 4000);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            new ReadMsg().start();
            new WriteMsg().start();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private static class ReadMsg extends Thread {

        @Override
        public void run() {

            String str;

            try {
                while (!stop) {
                    str = in.readLine();
                    if (str.equals("stop")) {
                        stop = true;
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {

            }
        }
    }

    public static class WriteMsg extends Thread {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (!stop) {
                String line;
                try {
                    line = scanner.nextLine();
                    out.write(line + "\n");
                    out.flush();
                    if (line.equals("stop")) {
                        stop = true;
                        break;
                    }
                } catch (IOException e) {

                }
            }
        }
    }
}