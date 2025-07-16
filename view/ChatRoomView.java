package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatRoomView extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton exitButton;

    public ChatRoomView(String roomTitle, String userName) {
        setTitle(roomTitle);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단: 사용자 정보 및 나가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(userName);
        userLabel.setIcon(new ImageIcon("profile.png")); // 프로필 사진 경로가 있으면 설정
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> dispose());

        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 채팅 내용 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 하단: 입력창 + 보내기 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("보내기");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // 더미 메시지 출력
        appendMessage("동건", "바이");
        appendMessage("다빈", "하이");

        // 보내기 버튼 동작
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = inputField.getText().trim();
                if (!msg.isEmpty()) {
                    appendMessage(userName, msg);
                    inputField.setText("");
                }
            }
        });

        setVisible(true);
    }

    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }
}