import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    public static final int PORT = 4000;
    public static final int LIMIT_CONNECTIONS = 3;
    public static LinkedList<ServerClient> serverClients = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("server start");
        try {
            while (true) {
                Socket socket = server.accept();
                System.out.println("connected");
                boolean check = true;
                try {
                    for (ServerClient serverClient : serverClients) {//перебераем клиентов
                        if (socket.getInetAddress().equals(serverClient.getInetAddress())){//нашли клиента
                            if (serverClient.getCountConnections() < LIMIT_CONNECTIONS) {//проверяем лимит подключений клиента
                                if (serverClient.isInterrupted()) {//проверка на прерывание предедущего потока
                                    serverClient.reStart(socket);
                                    System.out.println("authorization");
                                    break;
                                } else {
                                    System.out.println("Клиент с таким Inet Address уже подключен");
                                }
                            } else {
                                System.out.println("user "+socket.getInetAddress()+ "was be banned");
                                socket.close();
                            }
                            check = false;
                        }
                    }
                    if (check){
                        ServerClient serverClient = new ServerClient(socket);
                        serverClients.add(serverClient);
                        System.out.println("authorization");
                        serverClient.start();
                    }
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}