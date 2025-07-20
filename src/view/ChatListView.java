package view;

import model.ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChatListView extends JPanel {

    public ChatListView(CardLayout cardLayout, JPanel mainPanel, List<ChatRoom> roomList, String userName) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ✅ 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("⬅️");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(50, 35));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "homeView"));
        topPanel.add(backButton, BorderLayout.WEST);

        JTextField searchField = new JTextField("채팅방 목록 검색");
        searchField.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchField.setPreferredSize(new Dimension(400, 35));
        // placeholder처럼 동작하게 포커스 이벤트 추가
        final String SEARCH_HINT = "채팅방 목록 검색";
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(SEARCH_HINT)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(SEARCH_HINT);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        topPanel.add(searchField, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ✅ 채팅방 리스트
        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(Color.WHITE);

        for (ChatRoom room : roomList) {
            chatListPanel.add(createChatRoomLabel(room.getRoomName(), userName, cardLayout, mainPanel));
            chatListPanel.add(new JSeparator());
        }

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
                // ownerId_roomName 분리
                String[] parts = roomTitle.split("_", 2);
                String roomName;
                String ownerId;
                if (parts.length > 1) {
                    ownerId = parts[0];
                    roomName = parts[1];
                } else {
                    roomName = roomTitle;
                    ownerId = roomTitle; // 일반방은 ownerId=roomName
                }
                mainFrame.openChatRoom(roomName, ownerId);
            }
        });
        return label;
    }
}