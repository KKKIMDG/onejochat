package controller;

import view.LoginView;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LoginController {
    private LoginView loginView;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private MainFrame mainFrame;

    private static String currentUserId = null;  // 🔥 정적 필드 추가

    public LoginController(LoginView loginView, JPanel mainPanel, CardLayout cardLayout) {
        this.loginView = loginView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        loginView.getLoginButton().addActionListener(e -> login());
    }

    public static String getCurrentUserId() {  // 🔥 정적 메서드 추가
        return currentUserId;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    private void login() {
        String id = loginView.getUserId();
        String pw = loginView.getPassword();

        try {
            Socket socket = new Socket("localhost", 9002);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write("LOGIN:ID=" + id + ",PW=" + pw + "\n");
            writer.flush();

            String response = reader.readLine();
            if ("LOGIN_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(loginView, "로그인 성공");
                currentUserId = id;  // 🔥 정적 필드에 저장
                if (mainFrame != null) {
                    mainFrame.setMyId(id);  // 메인 프레임에도 전달
                }
                cardLayout.show(mainPanel, "homeView");
            } else {
                JOptionPane.showMessageDialog(loginView, "로그인 실패");
            }
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
