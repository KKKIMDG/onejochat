import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9002);
        System.out.println("서버 시작: 포트 9002");

        while (true) {
            Socket clientSocket = serverSocket.accept(); // 클라이언트 접속 대기
            System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

            Thread thread = new ClientHandler(clientSocket);
            thread.start();
        }
    }
}