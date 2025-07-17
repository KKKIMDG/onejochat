
import view.MainFrame;

import javax.swing.*;
import java.net.Socket;

/**
 * 클라이언트 메인 클래스
 * 채팅 애플리케이션의 클라이언트를 시작하고 서버에 연결합니다.
 */
public class ClientMain {
    //
    /**
     * 메인 메서드 - 클라이언트 애플리케이션을 시작합니다.
     * 
     * @param args 명령행 인수 (사용되지 않음)
     */
    public static void main(String[] args) {
        try {
            // 서버에 소켓 연결 (IP: 100.100.101.27, 포트: 9002)
            Socket socket = new Socket("localhost", 9001); // 서버 연결
            System.out.println("서버 연결 성공");

            // Swing GUI를 EDT(Event Dispatch Thread)에서 실행
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // 메인 프레임 생성 및 소켓 전달
                    new MainFrame(socket);
                }
            });

        } catch (Exception e) {
            // 서버 연결 실패 시 에러 메시지 표시
            JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.");
            e.printStackTrace();
        }
    }
}