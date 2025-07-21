package controller;

import view.LoginView;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public LoginController(LoginView loginView, JPanel mainPanel, CardLayout cardLayout) { // 로그인 컨트롤러를 생성하면 로그인뷰, 메인패널, 카드레이아웃 객체를 참조가능
        this.loginView = loginView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // 로그인 버튼에 액션 리스너 추가
        loginView.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginController.this.login();
            }
        });
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
        String id = loginView.getUserId(); // 로그인 뷰에서 입력한 유저 아이디를 Id에 복사
        String pw = loginView.getPassword(); // 로그인 뷰에서 입력한 패스워드를 pw에 복사

        System.out.println("[CLIENT] 로그인 시도: id=" + id);
        try {
            final Socket socket = new Socket("localhost", 9001);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 서버로부터 쓰기위한 스트림 writer
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버로부터 읽어오기위한 스트림 reader

            writer.write("LOGIN:ID=" + id + ",PW=" + pw + "\n"); // 서버에 LOGIN:ID=id,PW=,pw를 보내고 줄바꿈
            writer.flush(); // 버퍼 비움

            System.out.println("[CLIENT] 서버 응답 대기...");
            String response = reader.readLine(); // 서버로부터 받은 데이터를 한줄 읽은것을 response에 복사
            System.out.println("[CLIENT] 서버 응답: " + response);
            if ("LOGIN_SUCCESS".equals(response)) { // 서버로부터 받은 데이터가 LOGIN_SUCCESS면 실행
                JOptionPane.showMessageDialog(loginView, "로그인 성공"); // 로그인 뷰 페이지에 로그인 성공 다이어로그 띄움
                currentUserId = id;
                if (mainFrame != null) {
                    mainFrame.setMyId(id);
                }
                SwingUtilities.invokeLater(() -> {
                    cardLayout.show(mainPanel, "homeView");
                });
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
                                try { reader.close(); } catch (Exception e) {}
                                try { writer.close(); } catch (Exception e) {}
                                try { socket.close(); } catch (Exception e) {}
                                break;
                            }
                        }
                    } catch (IOException e) {
                        // 무시
                    }
                }).start();
            } else if ("LOGIN_DUPLICATE".equals(response)) {
                // 중복 로그인: 바로 1회까지만 재시도 (Thread.sleep 제거)
                if (retryCount < 1) {
                    System.out.println("[CLIENT] 중복 로그인 감지, 즉시 재시도");
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
