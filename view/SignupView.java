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
    private JButton cancelButton;

    public SignupView(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔹 상단
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        topPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("🙋‍");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x0099FF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(titleLabel);

        // 🔹 중앙 입력폼
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        formPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("이름 입력:");
        nameField = new JTextField();
        styleField(nameField);

        JLabel idLabel = new JLabel("id 입력:");
        idField = new JTextField();
        styleField(idField);

        checkIdButton = new JButton("중복확인");

        JPanel idPanel = new JPanel(new BorderLayout(5, 5));
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdButton, BorderLayout.EAST);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idPanel.setOpaque(false);

        JLabel pwLabel = new JLabel("password 입력:");
        passwordField = new JPasswordField();
        styleField(passwordField);

        JLabel confirmLabel = new JLabel("password 재입력:");
        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField);

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

        // 🔹 버튼 영역
        signupButton = new JButton("완료");
        cancelButton = new JButton("취소");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(signupButton);

        // 🔹 컨테이너
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createLineBorder(new Color(0x0099FF), 2));
        container.setBackground(Color.WHITE);
        container.add(Box.createVerticalStrut(10));
        container.add(topPanel);
        container.add(formPanel);
        container.add(buttonPanel);

        add(container, BorderLayout.CENTER);

        // 🔄 취소 버튼 누르면 로그인 화면으로 전환
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
    }


    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    // 🔹 Getter
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

    public JButton getCancelButton() {
        return cancelButton;
    }

    public String getUserId() {
        return idField.getText().trim();
    }

    public JButton getCheckButton() {
        return checkIdButton;
    }
}