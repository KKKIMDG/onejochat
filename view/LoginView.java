package KDT.onejochat.view;

import javax.swing.*;
import java.awt.*;

/**
 * 로그인 뷰 클래스
 * 사용자 로그인을 위한 GUI 화면을 제공하는 클래스입니다.
 */
public class LoginView extends JPanel {
    /** 사용자 ID 입력 필드 */
    private JTextField userIdField;
    /** 비밀번호 입력 필드 */
    private JPasswordField passwordField;
    /** 로그인 버튼 */
    private JButton loginButton;
    /** 회원가입 버튼 */
    private JButton joinButton;

    /**
     * 로그인 뷰 생성자
     * 
     * @param cardLayout 카드 레이아웃 (사용되지 않음)
     * @param mainPanel 메인 패널 (사용되지 않음)
     */
    public LoginView(CardLayout cardLayout, JPanel mainPanel) {
        // 패널 기본 설정
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(350, 500)); // 크기 고정

        // 메인 래퍼 패널 생성 및 설정
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(0x007BFF), 2));
        wrapper.setBackground(Color.WHITE);
        wrapper.setPreferredSize(new Dimension(300, 450));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 로고 라벨 생성 및 설정
        JLabel logo = new JLabel("🔑 로그인");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 사용자 ID 입력 필드 생성 및 설정
        userIdField = new JTextField("id 입력:");
        userIdField.setMaximumSize(new Dimension(250, 40));
        userIdField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 비밀번호 입력 필드 생성 및 설정
        passwordField = new JPasswordField("password 입력:");
        passwordField.setMaximumSize(new Dimension(250, 40));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 로그인 버튼 생성 및 설정
        loginButton = new JButton("login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(0x007BFF));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));

        // 회원가입 버튼 생성 및 설정
        joinButton = new JButton("회원가입");
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.setBackground(Color.WHITE);
        joinButton.setForeground(new Color(0x007BFF));
        joinButton.setPreferredSize(new Dimension(200, 40));
        joinButton.setMaximumSize(new Dimension(200, 40));

        // 컴포넌트들을 래퍼 패널에 추가
        wrapper.add(Box.createVerticalStrut(40));
        wrapper.add(logo);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(userIdField);
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(passwordField);
        wrapper.add(Box.createVerticalStrut(30));
        wrapper.add(loginButton);
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(joinButton);
        wrapper.add(Box.createVerticalGlue());

        // 래퍼 패널을 메인 패널에 추가
        add(wrapper, BorderLayout.CENTER);
    }

    /**
     * 사용자 ID를 반환합니다.
     * 
     * @return 입력된 사용자 ID
     */
    public String getUserId() {
        return userIdField.getText();
    }

    /**
     * 비밀번호를 반환합니다.
     * 
     * @return 입력된 비밀번호
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * 로그인 버튼을 반환합니다.
     * 
     * @return 로그인 버튼
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * 회원가입 버튼을 반환합니다.
     * 
     * @return 회원가입 버튼
     */
    public JButton getJoinButton() {
        return joinButton;
    }
}