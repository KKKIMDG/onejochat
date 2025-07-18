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
        loginWithRetry(0);
    }

    // 최대 3회까지 재시도
    private void loginWithRetry(int retryCount) {
        String id = loginView.getUserId();
        String pw = loginView.getPassword();

        try {
            Socket socket = new Socket("100.100.101.30", 9001);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write("LOGIN:ID=" + id + ",PW=" + pw + "\n");
            writer.flush();

            String response = reader.readLine();
            if ("LOGIN_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(loginView, "로그인 성공");
                currentUserId = id;
                if (mainFrame != null) {
                    mainFrame.setMyId(id);
                }
                cardLayout.show(mainPanel, "homeView");
                new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = reader.readLine()) != null) {
                            if ("FORCE_LOGOUT".equals(msg)) {
                                JOptionPane.showMessageDialog(null, "다른 곳에서 로그인되어 강제 로그아웃되었습니다.");
                                SwingUtilities.invokeLater(() -> {
                                    cardLayout.show(mainPanel, "loginView");
                                    currentUserId = null;
                                });
                                break;
                            }
                        }
                    } catch (IOException e) {
                        // 무시
                    }
                }).start();
            } else if ("LOGIN_DUPLICATE".equals(response)) {
                // 중복 로그인: 0.5초 후 최대 3회까지 재시도
                if (retryCount < 3) {
                    try { Thread.sleep(500); } catch (InterruptedException e) { }
                    loginWithRetry(retryCount + 1);
                } else {
                    JOptionPane.showMessageDialog(loginView, "중복 로그인 처리 중입니다. 잠시 후 다시 시도해 주세요.");
                }
            } else {
                JOptionPane.showMessageDialog(loginView, "로그인 실패");
            }
            clearPasswordField();
            // socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            clearPasswordField();
        }
    }

    // ID 저장/불러오기/삭제 관련 메서드
    private void clearPasswordField() {
        if (loginView != null) {
            loginView.setPassword("");
        }
    }
}
