package controller;

import model.User;
import service.LoginService;
import view.LoginView;
import view.SignupView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController extends LoginService {
    private LoginView loginView;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private User user;
    SignupView signupView = new SignupView(cardLayout, mainPanel); // 이걸 먼저 생성하고
    SignUpController signUpController = new SignUpController(signupView, mainPanel, cardLayout);
    public LoginController(LoginView loginView, JPanel mainPanel, CardLayout cardLayout) {
        this.loginView = loginView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.user = new User();

        loginView.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = loginView.getName();
                String id = loginView.getUserId();
                String pw = loginView.getPassword();

                user.setId(id);
                user.setPassword(pw);

                if (isLogin(user.getId(), user.getPassword())) {
                    JOptionPane.showMessageDialog(loginView, "환영합니다.");
                    // 전환
                    cardLayout.show(mainPanel, "homeView");

                } else {
                    System.out.println("❌ 로그인 실패: 아이디 또는 비밀번호 불일치");
                    JOptionPane.showMessageDialog(loginView, "로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
                }
            }
        });
    }


}