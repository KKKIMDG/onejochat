package service;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class LoginService {

    private static final String SERVER_IP = "100.100.101.27"; // 실제 서버 IP
    private static final int SERVER_PORT = 9001;             // 서버 포트 번호
    private static final int SOCKET_TIMEOUT_MS = 5000;       // 5초 타임아웃 설정

    public boolean isLogin(String inputId, String inputPw) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String request = String.format("LOGIN:ID=%s,PW=%s\n", inputId, inputPw);
            writer.write(request);
            writer.flush();

            String response = reader.readLine();

            if (response == null) {
                System.err.println("서버로부터 응답이 없습니다.");
                return false;
            }

            System.out.println("서버 응답: " + response);

            return "LOGIN_SUCCESS".equals(response.trim());

        } catch (SocketTimeoutException ste) {
            System.err.println("서버 응답 시간 초과");
            return false;
        } catch (IOException e) {
            System.err.println("서버 연결 오류: " + e.getMessage());
            return false;
        }
    }
}