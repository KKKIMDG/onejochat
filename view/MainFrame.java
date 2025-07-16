package view;

import controller.LoginController;
import controller.SignUpController;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Socket socket;

    public MainFrame(Socket socket) {
        this.socket = socket;

        setTitle("onejo");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔹 CardLayout 초기화
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 🔹 View 생성 및 등록
        LoginView loginView = new LoginView(cardLayout, mainPanel);
        HomeView homeView = new HomeView();
        SignupView signupView = new SignupView(cardLayout, mainPanel);

        mainPanel.add(loginView, "loginView");
        mainPanel.add(homeView, "homeView");
        mainPanel.add(signupView, "signupView");

        // 🔹 버튼 이벤트 연결
        loginView.getJoinButton().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cardLayout.show(mainPanel, "signupView");
            }
        });

        // 🔹 컨트롤러 연결 (socket 넘기려면 생성자도 수정 필요)
        new SignUpController(signupView, mainPanel, cardLayout /*, socket */);
        new LoginController(loginView, mainPanel, cardLayout /*, socket */);

        // 🔹 초기화
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");
        setVisible(true);
    }
}