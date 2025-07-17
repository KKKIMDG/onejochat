package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatRoomView extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton exitButton;

    public ChatRoomView(String roomTitle, String userName) {
        this(roomTitle, userName, null); // onExit은 null로 넘김
    }

    public ChatRoomView(String roomTitle, String userName, Runnable onExit) {
        setLayout(new BorderLayout());

        // 상단: 사용자 정보 및 나가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(userName);
        userLabel.setIcon(new ImageIcon("profile.png")); // 이미지 있을 경우 사용
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> {
            if (onExit != null) onExit.run();
        });

        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 채팅창
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 하단: 입력창 + 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("보내기");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // 기본 메시지
        appendMessage("동건", "바이");
        appendMessage("다빈", "하이");

        // 이벤트
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = inputField.getText().trim();
                if (!msg.isEmpty()) {
                    appendMessage(userName, msg);
                    inputField.setText("");
                }
            }
        });
    }

    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }
}