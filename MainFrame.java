import view.HomeView;
import view.LoginView;
import view.SignupView;

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
        loginView.getLoginButton().addActionListener(e -> cardLayout.show(mainPanel, "homeView"));

        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 🔹 기본 세팅
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}