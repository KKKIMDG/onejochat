package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatListView extends JPanel {

    public ChatListView(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ✅ 상단 패널 (뒤로가기 + 검색창)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // 🔙 뒤로가기 버튼
        JButton backButton = new JButton("⬅️"); // 또는 "←"
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(50, 35)); // 고정 너비 그대로 둬도 됨
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "homeView"));
        topPanel.add(backButton, BorderLayout.WEST);

        // 🔍 검색창
        JTextField searchField = new JTextField("채팅방 목록 검색");
        searchField.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchField.setPreferredSize(new Dimension(400, 35));
        topPanel.add(searchField, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 📜 채팅방 리스트 패널
        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(Color.WHITE);

        // 🧑 샘플 채팅방 1 - 동건
        chatListPanel.add(createChatRoomLabel("동건", "다빈", cardLayout, mainPanel));
        chatListPanel.add(new JSeparator());

        // 📚 샘플 채팅방 2 - 공부 채팅방
        chatListPanel.add(createChatRoomLabel("공부 채팅방", "다빈", cardLayout, mainPanel));
        chatListPanel.add(new JSeparator());

        // 🔽 스크롤 추가
        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createChatRoomLabel(String roomTitle, String userName, CardLayout cardLayout, JPanel mainPanel) {
        JLabel label = new JLabel(roomTitle);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(mainPanel);
                mainFrame.openChatRoom(roomTitle, userName);
            }
        });

        return label;
    }
}