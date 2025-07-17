package view;

import javax.swing.*;
import java.awt.*;

public class SecretChatCodeDialog extends JDialog {

    private String code = null;
    public String getCode() { return code; }

    public SecretChatCodeDialog(JFrame parent) {
        super(parent, "채팅방 코드 설정", true);

        // 기본 설정
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
//
        // 타이틀
        JLabel titleLabel = new JLabel("채팅방 코드 설정");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x007BFF));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 코드 입력 필드
        JTextField codeField = new JTextField();
        codeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        codeField.setPreferredSize(new Dimension(200, 35));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setBorder(BorderFactory.createTitledBorder("코드 입력:"));
        add(codeField, BorderLayout.CENTER);

        // 완료 버튼
        JButton confirmBtn = new JButton("완료");
        confirmBtn.setBackground(Color.WHITE);
        confirmBtn.setForeground(new Color(0x007BFF));
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(confirmBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // 버튼 동작 예시 (실제로는 필요한 동작 구현)
        confirmBtn.addActionListener(e -> {
            String input = codeField.getText();
            if (!input.isEmpty()) {
                code = input;
                dispose();  // 닫기
            }
        });
    }
}