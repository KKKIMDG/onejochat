package KDT.onejochat.service;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 로그인 서비스 클래스
 * 서버와 통신하여 사용자 로그인을 처리하는 서비스 클래스입니다.
 */
public class LoginService {

    /** 서버 IP 주소 */
    private static final String SERVER_IP = "100.100.101.27";
    /** 서버 포트 번호 */
    private static final int SERVER_PORT = 9001;
    /** 소켓 타임아웃 설정 (밀리초) */
    private static final int SOCKET_TIMEOUT_MS = 5000;

    /**
     * 사용자 로그인을 검증합니다.
     * 
     * @param inputId 입력된 사용자 ID
     * @param inputPw 입력된 사용자 비밀번호
     * @return 로그인 성공 여부
     */
    public boolean isLogin(String inputId, String inputPw) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            // 소켓 타임아웃 설정
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);

            // 서버와의 입출력 스트림 생성
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 로그인 요청 메시지 생성 및 전송
            String request = String.format("LOGIN:ID=%s,PW=%s\n", inputId, inputPw);
            writer.write(request);
            writer.flush();

            // 서버로부터 응답 수신
            String response = reader.readLine();

            // 응답이 없는 경우 처리
            if (response == null) {
                System.err.println("서버로부터 응답이 없습니다.");
                return false;
            }

            System.out.println("서버 응답: " + response);

            // 로그인 성공 여부 반환
            return "LOGIN_SUCCESS".equals(response.trim());

        } catch (SocketTimeoutException ste) {
            // 소켓 타임아웃 예외 처리
            System.err.println("서버 응답 시간 초과");
            return false;
        } catch (IOException e) {
            // 입출력 예외 처리
            System.err.println("서버 연결 오류: " + e.getMessage());
            return false;
        }
    }
}