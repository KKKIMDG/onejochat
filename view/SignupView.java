package view;

import javax.swing.*;
import java.awt.*;

public class SignupView extends JPanel {
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton checkIdButton;
    private JButton signupButton;

    public SignupView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔹 상단 패널 (아이콘 + 타이틀)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        topPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("🙋");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x0099FF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(titleLabel);

        // 🔹 입력 필드 영역
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        formPanel.setBackground(Color.WHITE);

        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel nameLabel = new JLabel("이름 입력");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        checkIdButton = new JButton("중복확인");

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BorderLayout(5, 5));
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdButton, BorderLayout.EAST);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idPanel.setOpaque(false);

        JLabel idLabel = new JLabel("id 입력");
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setEchoChar('•');

        JLabel pwLabel = new JLabel("비밀번호 입력");
        pwLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        confirmPasswordField.setEchoChar('•');

        JLabel confirmLabel = new JLabel("비밀번호 확인");
        confirmLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(idLabel);
        formPanel.add(idPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(pwLabel);
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(confirmLabel);
        formPanel.add(confirmPasswordField);

        // 🔹 완료 버튼
        signupButton = new JButton("완료");
        signupButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupButton.setPreferredSize(new Dimension(120, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(signupButton);

        // 🔹 전체 구성 감싸는 패널
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createLineBorder(new Color(0x0099FF), 2));
        container.setBackground(Color.WHITE);
        container.add(Box.createVerticalStrut(10));
        container.add(topPanel);
        container.add(formPanel);
        container.add(buttonPanel);

        add(container, BorderLayout.CENTER);
    }

    // 🔹 Getter 메서드
    public String getNameInput() {
        return nameField.getText();
    }

    public String getIdInput() {
        return idField.getText();
    }

    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPasswordInput() {
        return new String(confirmPasswordField.getPassword());
    }

    public JButton getCheckIdButton() {
        return checkIdButton;
    }

    public JButton getSignupButton() {
        return signupButton;
    }
}