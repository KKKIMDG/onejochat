package view;

import javax.swing.*;
import java.awt.*;

public class FriendAddView extends JDialog {
    private JTextField idField;
    private JLabel resultLabel;
    private JButton requestButton;
    private JButton searchButton;  // 🔹 외부에서 접근할 수 있도록 필드로 승격

    public FriendAddView(JFrame parent) {
        super(parent, "친구 추가", true);
        setSize(350, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 🔹 상단 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel iconLabel = new JLabel("💬");
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
        topPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("친구 추가");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x007BFF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        add(topPanel, BorderLayout.NORTH);

        // 🔹 중앙 입력 영역
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // ID 입력 필드
        idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        idField.setPreferredSize(new Dimension(200, 35));
        idField.setMaximumSize(new Dimension(300, 35));
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);
        idField.setBorder(BorderFactory.createTitledBorder("추가할 친구의 ID 입력:"));
        centerPanel.add(idField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 🔍 검색 버튼
        searchButton = new JButton("🔍 검색");
        searchButton.setForeground(new Color(0x007BFF));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setBackground(Color.WHITE);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(searchButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 검색 결과 라벨
        resultLabel = new JLabel("검색 결과:");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(resultLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 친구 요청 버튼
        requestButton = new JButton("친구 요청");
        requestButton.setVisible(true);
        requestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestButton.setBackground(Color.WHITE);
        requestButton.setForeground(new Color(0x007BFF));
        requestButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        requestButton.setFocusPainted(false);
        requestButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x007BFF), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        centerPanel.add(requestButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    // ✅ Getter 메서드들
    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getRequestButton() {
        return requestButton;
    }

    public String getInputId() {
        return idField.getText().trim();
    }

    public void setResultText(String text) {
        resultLabel.setText(text);
    }

    public String getResultText() {
        return resultLabel.getText();
    }
}