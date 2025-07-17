package KDT.onejochat.view;

import javax.swing.*;
import java.awt.*;

/**
 * 회원가입 뷰 클래스
 * 사용자 회원가입을 위한 GUI 화면을 제공하는 클래스입니다.
 */
public class SignupView extends JPanel {
    /** 이름 입력 필드 */
    private JTextField nameField;
    /** ID 입력 필드 */
    private JTextField idField;
    /** 비밀번호 입력 필드 */
    private JPasswordField passwordField;
    /** 비밀번호 확인 입력 필드 */
    private JPasswordField confirmPasswordField;
    /** ID 중복확인 버튼 */
    private JButton checkIdButton;
    /** 회원가입 완료 버튼 */
    private JButton signupButton;
    /** 취소 버튼 */
    private JButton cancelButton;
//
    /**
     * 회원가입 뷰 생성자
     * 
     * @param cardLayout 카드 레이아웃
     * @param mainPanel 메인 패널
     */
    public SignupView(CardLayout cardLayout, JPanel mainPanel) {
        // 패널 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 패널 생성 (아이콘과 제목)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        topPanel.setBackground(Color.WHITE);

        // 아이콘 라벨 생성
        JLabel iconLabel = new JLabel("🙋‍");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 제목 라벨 생성
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x0099FF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 상단 패널에 컴포넌트 추가
        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(titleLabel);

        // 중앙 입력폼 패널 생성
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        formPanel.setBackground(Color.WHITE);

        // 이름 입력 필드 생성
        JLabel nameLabel = new JLabel("이름 입력:");
        nameField = new JTextField();
        styleField(nameField);

        // ID 입력 필드와 중복확인 버튼 생성
        JLabel idLabel = new JLabel("id 입력:");
        idField = new JTextField();
        styleField(idField);
        checkIdButton = new JButton("중복확인");

        // ID 입력 패널 (필드와 버튼을 함께 배치)
        JPanel idPanel = new JPanel(new BorderLayout(5, 5));
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdButton, BorderLayout.EAST);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idPanel.setOpaque(false);

        // 비밀번호 입력 필드 생성
        JLabel pwLabel = new JLabel("password 입력:");
        passwordField = new JPasswordField();
        styleField(passwordField);

        // 비밀번호 확인 입력 필드 생성
        JLabel confirmLabel = new JLabel("password 재입력:");
        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField);

        // 폼 패널에 컴포넌트 추가
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

        // 버튼 영역 패널 생성
        signupButton = new JButton("완료");
        cancelButton = new JButton("취소");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(signupButton);

        // 메인 컨테이너 패널 생성
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createLineBorder(new Color(0x0099FF), 2));
        container.setBackground(Color.WHITE);
        container.add(Box.createVerticalStrut(10));
        container.add(topPanel);
        container.add(formPanel);
        container.add(buttonPanel);

        // 컨테이너를 메인 패널에 추가
        add(container, BorderLayout.CENTER);

        // 취소 버튼 이벤트 리스너 - 로그인 화면으로 전환
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
    }

    /**
     * 입력 필드의 스타일을 설정합니다.
     * 
     * @param field 스타일을 적용할 텍스트 필드
     */
    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    /**
     * 이름 입력값을 반환합니다.
     * 
     * @return 입력된 이름
     */
    public String getNameInput() {
        return nameField.getText();
    }

    /**
     * ID 입력값을 반환합니다.
     * 
     * @return 입력된 ID
     */
    public String getIdInput() {
        return idField.getText();
    }

    /**
     * 비밀번호 입력값을 반환합니다.
     * 
     * @return 입력된 비밀번호
     */
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    /**
     * 비밀번호 확인 입력값을 반환합니다.
     * 
     * @return 입력된 비밀번호 확인값
     */
    public String getConfirmPasswordInput() {
        return new String(confirmPasswordField.getPassword());
    }

    /**
     * ID 중복확인 버튼을 반환합니다.
     * 
     * @return ID 중복확인 버튼
     */
    public JButton getCheckIdButton() {
        return checkIdButton;
    }

    /**
     * 회원가입 완료 버튼을 반환합니다.
     * 
     * @return 회원가입 완료 버튼
     */
    public JButton getSignupButton() {
        return signupButton;
    }

    /**
     * 취소 버튼을 반환합니다.
     * 
     * @return 취소 버튼
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * 사용자 ID를 반환합니다 (공백 제거).
     * 
     * @return 공백이 제거된 사용자 ID
     */
    public String getUserId() {
        return idField.getText().trim();
    }

    /**
     * ID 중복확인 버튼을 반환합니다 (별칭).
     * 
     * @return ID 중복확인 버튼
     */
    public JButton getCheckButton() {
        return checkIdButton;
    }
}