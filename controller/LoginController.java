package controller;

import view.LoginView;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * 로그인 컨트롤러 클래스
 * 로그인 뷰와 서버 간의 상호작용을 관리하는 컨트롤러입니다.
 */
public class LoginController {
    /** 로그인 뷰 참조 */
    private LoginView loginView;
    /** 메인 패널 참조 */
    private JPanel mainPanel;
    /** 카드 레이아웃 참조 */
    private CardLayout cardLayout;
    /** 메인 프레임 참조 */
    private MainFrame mainFrame;

    /** 현재 로그인한 사용자의 ID (정적 필드) */
    private static String currentUserId = null;

    /**
     * 로그인 컨트롤러 생성자
     * 
     * @param loginView 로그인 뷰
     * @param mainPanel 메인 패널
     * @param cardLayout 카드 레이아웃
     */
    public LoginController(LoginView loginView, JPanel mainPanel, CardLayout cardLayout) {
        this.loginView = loginView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // 로그인 버튼에 액션 리스너 추가
        loginView.getLoginButton().addActionListener(e -> login());
    }

    /**
     * 현재 로그인한 사용자의 ID를 반환합니다.
     * 
     * @return 현재 사용자 ID
     */
    public static String getCurrentUserId() {
        return currentUserId;
    }
//
    /**
     * 메인 프레임을 설정합니다.
     * 
     * @param mainFrame 설정할 메인 프레임
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * 로그인 처리를 수행합니다.
     * 서버에 로그인 요청을 보내고 결과에 따라 화면을 전환합니다.
     */
    private void login() {
        // 로그인 뷰에서 사용자 입력 받기
        String id = loginView.getUserId();
        String pw = loginView.getPassword();

        try {
            // 서버에 소켓 연결
            Socket socket = new Socket("localhost", 9002);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 로그인 요청 전송
            writer.write("LOGIN:ID=" + id + ",PW=" + pw + "\n");
            writer.flush();

            // 서버 응답 수신
            String response = reader.readLine();
            if ("LOGIN_SUCCESS".equals(response)) {
                // 로그인 성공 시 처리
                JOptionPane.showMessageDialog(loginView, "로그인 성공");
                currentUserId = id;  // 현재 사용자 ID 저장
                if (mainFrame != null) {
                    mainFrame.setMyId(id);  // 메인 프레임에도 전달
                }
                cardLayout.show(mainPanel, "homeView");  // 홈 화면으로 전환
            } else {
                // 로그인 실패 시 처리
                JOptionPane.showMessageDialog(loginView, "로그인 실패");
            }
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
