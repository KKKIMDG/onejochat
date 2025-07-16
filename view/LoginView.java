package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton joinButton;

    public LoginView(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(350, 500)); // 크기 고정

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(0x007BFF), 2));
        wrapper.setBackground(Color.WHITE);
        wrapper.setPreferredSize(new Dimension(300, 450));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 🔷 로고
        JLabel logo = new JLabel("🔑 로그인");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 🔷 ID 필드
        usernameField = new JTextField("id 입력:");
        usernameField.setMaximumSize(new Dimension(250, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 🔷 PW 필드
        passwordField = new JPasswordField("password 입력:");
        passwordField.setMaximumSize(new Dimension(250, 40));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 🔷 로그인 버튼
        loginButton = new JButton("login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(0x007BFF));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));

        // 🔷 회원가입 버튼
        joinButton = new JButton("회원가입");
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.setBackground(Color.WHITE);
        joinButton.setForeground(new Color(0x007BFF));
        joinButton.setPreferredSize(new Dimension(200, 40));
        joinButton.setMaximumSize(new Dimension(200, 40));

        wrapper.add(Box.createVerticalStrut(40));
        wrapper.add(logo);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(usernameField);
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(passwordField);
        wrapper.add(Box.createVerticalStrut(30));
        wrapper.add(loginButton);
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(joinButton);
        wrapper.add(Box.createVerticalGlue());

        add(wrapper, BorderLayout.CENTER);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getJoinButton() {
        return joinButton;
    }
}