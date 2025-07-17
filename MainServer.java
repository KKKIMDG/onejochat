package KDT.onejochat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 메인 서버 클래스
 * 채팅 애플리케이션의 서버를 시작하고 클라이언트 연결을 관리합니다.
 */
public class MainServer {

    /**
     * 메인 메서드 - 서버를 시작하고 클라이언트 연결을 수락합니다.
     * 
     * @param args 명령행 인수 (사용되지 않음)
     * @throws IOException 소켓 생성 또는 클라이언트 연결 처리 중 발생할 수 있는 예외
     */
    public static void main(String[] args) throws IOException {
        // 포트 9002에서 서버 소켓 생성
        ServerSocket serverSocket = new ServerSocket(9002);
        System.out.println("서버 시작: 포트 9002");

        // 무한 루프로 클라이언트 연결을 계속 수락
        while (true) {
            // 클라이언트 접속 대기 (블로킹 호출)
            Socket clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

            // 각 클라이언트에 대해 새로운 스레드 생성하여 병렬 처리
            Thread thread = new ClientHandler(clientSocket);
            thread.start();
        }
    }
}