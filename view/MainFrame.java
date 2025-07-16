package view;

import controller.LoginController;
import controller.SignUpController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
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
        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 🔹 회원가입 컨트롤러 연결
        new SignUpController(signupView, mainPanel, cardLayout);
        // 🔹 로그인 컨트롤러 연결 추가
        new LoginController(loginView, mainPanel, cardLayout);
        // 🔹 메인 프레임 세팅
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");  // 최초 화면은 로그인
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}