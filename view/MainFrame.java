package view;

import controller.FriendAddController;
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

        // 🔹 친구추가 다이얼로그 및 컨트롤러 연결
        FriendAddView friendAddView = new FriendAddView(this);
        new FriendAddController(friendAddView, socket);

        // 🔹 HomeView에서 친구추가 버튼 누르면 다이얼로그 띄움
        homeView.getAddFriendButton().addActionListener(e -> {
            friendAddView.setVisible(true);
        });

        // 🔹 버튼 이벤트 연결
        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 🔹 컨트롤러 연결
        new SignUpController(signupView, mainPanel, cardLayout);
        new LoginController(loginView, mainPanel, cardLayout);

        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");

        setVisible(true);
    }
}