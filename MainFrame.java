// package 선언이 없습니다. 즉, 이 파일은 '기본(default) 패키지' 또는 '루트(root) 패키지'에 위치합니다.
// 만약 특정 패키지(예: 'com.onejo.app')에 포함시키고 싶다면, package com.onejo.app; 를 추가해야 합니다.
import view.HomeView; // view 패키지 안에 있는 HomeView를 가져옵니다.
import view.LoginView; // view 패키지 안에 있는 LoginView를 가져옵니다.
import view.SignupView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("onejo");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        LoginView loginView = new LoginView();
        mainPanel.add(loginView, "loginView");

        HomeView homeView = new HomeView();
        mainPanel.add(homeView, "homeView");

        // 로그인 버튼 클릭 시 HomeView로 전환
        loginView.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "homeView");
            }
        });

        // 회원가입 버튼 눌렀을 때
        loginView.getJoinButton().addActionListener(e -> {
            setContentPane(new SignupView());
            revalidate();
            repaint();
        });

        add(mainPanel);
        cardLayout.show(mainPanel, "loginView"); // 최초 화면을 로그인으로
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
