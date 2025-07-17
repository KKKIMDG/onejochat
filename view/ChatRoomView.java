package KDT.onejochat.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 채팅방 뷰 클래스
 * 채팅방 화면을 제공하는 클래스입니다.
 * 메시지 표시, 입력, 전송 기능을 포함합니다.
 */
public class ChatRoomView extends JPanel {
    /** 채팅 메시지 표시 영역 */
    private JTextArea chatArea;
    /** 메시지 입력 필드 */
    private JTextField inputField;
    /** 메시지 전송 버튼 */
    private JButton sendButton;
    /** 채팅방 나가기 버튼 */
    private JButton exitButton;

    /**
     * 채팅방 뷰 생성자 (기본)
     * 
     * @param roomTitle 채팅방 제목
     * @param userName 사용자 이름
     */
    public ChatRoomView(String roomTitle, String userName) {
        this(roomTitle, userName, null); // onExit은 null로 넘김
    }

    /**
     * 채팅방 뷰 생성자 (완전)
     * 
     * @param roomTitle 채팅방 제목
     * @param userName 사용자 이름
     * @param onExit 채팅방 나가기 시 실행할 Runnable
     */
    public ChatRoomView(String roomTitle, String userName, Runnable onExit) {
        setLayout(new BorderLayout());

        // 상단 패널 생성: 사용자 정보 및 나가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(userName);
        userLabel.setIcon(new ImageIcon("profile.png")); // 이미지 있을 경우 사용
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 나가기 버튼 생성 및 이벤트 설정
        exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> {
            if (onExit != null) onExit.run();
        });

        // 상단 패널에 컴포넌트 추가
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 생성: 채팅 메시지 표시 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);  // 읽기 전용으로 설정
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 패널 생성: 메시지 입력 및 전송
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("보내기");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // 기본 메시지 추가 (테스트용)
        appendMessage("동건", "바이");
        appendMessage("다빈", "하이");

        // 메시지 전송 버튼 이벤트 리스너
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = inputField.getText().trim();
                if (!msg.isEmpty()) {
                    appendMessage(userName, msg);
                    inputField.setText("");  // 입력 필드 초기화
                }
            }
        });
    }

    /**
     * 채팅 영역에 메시지를 추가합니다.
     * 
     * @param sender 메시지 발신자
     * @param message 메시지 내용
     */
    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }
}