package view;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CreateChatView extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public CreateChatView(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔹 상단 타이틀 및 아이콘
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JLabel iconLabel = new JLabel("💬");  // 말풍선 아이콘
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));

        JLabel titleLabel = new JLabel("채팅방 만들기");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x007BFF));

        topPanel.add(iconLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(topPanel, BorderLayout.NORTH);

        // 🔹 중앙 콘텐츠 영역
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // 채팅방 이름 입력창
        JTextField roomNameField = new JTextField();
        roomNameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        roomNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        roomNameField.setBorder(BorderFactory.createTitledBorder("채팅방 이름 입력:"));
        centerPanel.add(roomNameField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 📒 초대할 친구 + 리스트
        JPanel invitePanel = new JPanel();
        invitePanel.setLayout(new BoxLayout(invitePanel, BoxLayout.Y_AXIS));
        invitePanel.setBackground(Color.WHITE);

        JLabel inviteTitle = new JLabel("📒 초대할 친구 선택");
        inviteTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inviteTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inviteTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        invitePanel.add(inviteTitle);

        // 더미 친구 리스트: 다빈, 동건
        java.util.List<String> friends = Arrays.asList("다빈", "동건");
        for (String friend : friends) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel nameLabel = new JLabel(friend);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

            JButton addBtn = new JButton("+");  // 초대 버튼
            addBtn.setPreferredSize(new Dimension(40, 40));
            addBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
            addBtn.setBackground(Color.WHITE);

            row.add(nameLabel, BorderLayout.WEST);
            row.add(addBtn, BorderLayout.EAST);
            invitePanel.add(row);
        }

        centerPanel.add(invitePanel);
        centerPanel.add(Box.createVerticalGlue());
        add(centerPanel, BorderLayout.CENTER);

        // 🔹 하단 버튼 영역
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        bottomPanel.setBackground(Color.WHITE);

        JButton secretBtn = new JButton("💬 비밀채팅");
        secretBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        secretBtn.setForeground(new Color(0x007BFF));
        secretBtn.setBackground(Color.WHITE);
        secretBtn.setFocusPainted(false);
        secretBtn.setBorderPainted(false);

        JButton normalBtn = new JButton("💬 일반채팅");
        normalBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        normalBtn.setForeground(new Color(0x007BFF));
        normalBtn.setBackground(Color.WHITE);
        normalBtn.setFocusPainted(false);
        normalBtn.setBorderPainted(false);

        bottomPanel.add(secretBtn);
        bottomPanel.add(normalBtn);

        // 뒤로가기 버튼 추가
        JButton backBtn = new JButton("뒤로가기");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setForeground(new Color(0x6C757D));
        backBtn.setBackground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "homeView"));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(bottomPanel, BorderLayout.CENTER);
        buttonPanel.add(backBtn, BorderLayout.SOUTH);

        secretBtn.addActionListener(e -> {
            // 비밀채팅 로직
            String roomName = roomNameField.getText().trim();
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "채팅방 이름을 입력하세요.");
                return;
            }
            // 비밀채팅 코드 다이얼로그 표시
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            SecretChatCodeDialog dialog = new SecretChatCodeDialog(parentFrame);
            dialog.setVisible(true);
        });

        normalBtn.addActionListener(e -> {
            // 일반채팅 로직
            String roomName = roomNameField.getText().trim();
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "채팅방 이름을 입력하세요.");
                return;
            }
            // 채팅방 생성 후 홈으로 이동
            cardLayout.show(mainPanel, "homeView");
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }
}