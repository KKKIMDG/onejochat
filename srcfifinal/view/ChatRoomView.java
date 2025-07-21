package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
    private JButton backButton;
    private JButton inviteButton;
    private Socket socket;
    private String userId;
    private String ownerId; // 방장 id
    private String roomName; // 채팅방 이름
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private volatile boolean running = true;
    /** 친구 ID → 별명 매핑 */
    private java.util.Map<String, String> nicknameMap = new java.util.HashMap<>();

    /**
     * 채팅방 뷰 생성자 (완전)
     * 
     * @param roomName 채팅방 제목
     * @param ownerId 방장 ID
     * @param socket 서버와의 소켓
     * @param userId 사용자 ID
     * @param mainPanel 메인 패널
     * @param cardLayout 카드 레이아웃
     */
    public ChatRoomView(String roomName, String ownerId, Socket socket, String userId, JPanel mainPanel, CardLayout cardLayout) {
        super(new BorderLayout());
        this.socket = socket;
        this.ownerId = ownerId;
        this.roomName = roomName;
        this.userId = userId;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // 상단 패널 생성: 사용자 정보, 뒤로가기, 나가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(userId);
        userLabel.setIcon(new ImageIcon("profile.png"));
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 뒤로가기 버튼
        backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "chatRoomListView"));
        topPanel.add(backButton, BorderLayout.WEST);

        // 나가기 버튼
        exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "정말 이 채팅방에서 나가시겠습니까?", "채팅방 나가기", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean left = leaveChatRoomOnServer();
                if (left) {
                    JOptionPane.showMessageDialog(this, "채팅방에서 나갔습니다.");
                    running = false;
                    // 채팅방 목록 즉시 새로고침 (0.3초 대기 후)
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (topFrame instanceof MainFrame) {
                        try { Thread.sleep(300); } catch (InterruptedException ex) { }
                        ((MainFrame) topFrame).refreshChatListView();
                    }
                    cardLayout.show(mainPanel, "chatRoomListView");
                } else {
                    JOptionPane.showMessageDialog(this, "채팅방 나가기 실패");
                }
            }
        });

        // 별명 파일 불러오기
        loadNicknamesFromFile();

        // 친구 초대 버튼
        inviteButton = new JButton("친구 초대");
        inviteButton.addActionListener(e -> {
            // MainFrame에서 친구 목록 가져오기
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            java.util.List<String> friendList = new java.util.ArrayList<>();
            if (topFrame instanceof MainFrame) {
                friendList = ((MainFrame) topFrame).myFriends;
            }
            // 채팅방 참여자 목록 파싱
            java.util.Set<String> participants = getCurrentParticipantsFromFile();
            // 초대 가능한 친구만 리스트업
            java.util.List<String> candidates = new java.util.ArrayList<>();
            for (String f : friendList) {
                if (!participants.contains(f)) candidates.add(f);
            }
            if (candidates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "초대 가능한 친구가 없습니다.");
                return;
            }
            // 별명 적용 리스트
            String[] displayNames = candidates.stream().map(id -> {
                String nick = nicknameMap.get(id);
                return (nick != null && !nick.isEmpty()) ? (nick + " (" + id + ")") : id;
            }).toArray(String[]::new);
            JList<String> candidateList = new JList<>(displayNames);
            candidateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(candidateList), "초대할 친구 선택", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && candidateList.getSelectedIndex() != -1) {
                String selectedDisplay = candidateList.getSelectedValue();
                // id 추출
                String friendId = candidates.get(candidateList.getSelectedIndex());
                boolean invited = inviteFriendToChatRoom(friendId);
                if (invited) {
                    JOptionPane.showMessageDialog(this, friendId + "님을 채팅방에 초대했습니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "초대 실패 (존재하지 않는 ID이거나 이미 참여 중)");
                }
            }
        });
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(inviteButton);
        rightPanel.add(exitButton);
        // 참여자 목록 버튼 추가
        JButton participantsButton = new JButton("참여자 목록");
        participantsButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof MainFrame) {
                ((MainFrame) topFrame).refreshFriendsList(); // 친구목록 최신화
            }
            java.util.Set<String> participants = getParticipantsFromServer();
            java.util.List<String> myFriends = new java.util.ArrayList<>();
            if (topFrame instanceof MainFrame) {
                myFriends = ((MainFrame) topFrame).myFriends;
            }
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            for (String p : participants) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                String nick = nicknameMap.get(p);
                JLabel nameLabel = new JLabel((nick != null && !nick.isEmpty()) ? (nick + " (" + p + ")") : p);
                row.add(nameLabel);
                if (!p.equals(userId) && !myFriends.contains(p)) {
                    JButton addBtn = new JButton("친구 추가");
                    addBtn.addActionListener(ev -> {
                        try {
                            java.io.File file = new java.io.File("friends_" + userId + ".txt");
                            boolean already = false;
                            if (file.exists()) {
                                try (BufferedReader r = new BufferedReader(new java.io.FileReader(file))) {
                                    String line;
                                    while ((line = r.readLine()) != null) {
                                        if (line.trim().equals(p)) { already = true; break; }
                                    }
                                }
                            }
                            if (!already) {
                                try (java.io.BufferedWriter w = new java.io.BufferedWriter(new java.io.FileWriter(file, true))) {
                                    w.write(p + System.lineSeparator());
                                }
                                // MainFrame의 친구 리스트도 새로고침
                                if (topFrame instanceof MainFrame) {
                                    ((MainFrame) topFrame).refreshFriendsList();
                                }
                                JOptionPane.showMessageDialog(this, p + "님을 친구로 추가했습니다!");
                                addBtn.setEnabled(false);
                            } else {
                                JOptionPane.showMessageDialog(this, "이미 친구입니다.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "친구 추가 중 오류 발생");
                        }
                    });
                    row.add(addBtn);
                } else if (!p.equals(userId)) {
                    JButton addedBtn = new JButton("이미 친구");
                    addedBtn.setEnabled(false);
                    row.add(addedBtn);
                }
                panel.add(row);
            }
            JScrollPane scroll = new JScrollPane(panel);
            scroll.setPreferredSize(new Dimension(250, 200));
            JOptionPane.showMessageDialog(this, scroll, "채팅방 참여자", JOptionPane.PLAIN_MESSAGE);
        });
        rightPanel.add(participantsButton);
        topPanel.add(rightPanel, BorderLayout.EAST);
        topPanel.add(userLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 검색 패널 추가 (상단으로 이동)
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("검색");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        // topPanel 아래에 검색 패널 추가
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

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

        // 검색 버튼 이벤트
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            java.util.List<String> lines = getChatHistoryLinesFromServer();
            StringBuilder sb = new StringBuilder();
            if (keyword.isEmpty()) {
                // 전체 내역 복원
                for (String line : lines) sb.append(line).append("\n");
            } else {
                for (String line : lines) {
                    if (line.contains(keyword)) sb.append(line).append("\n");
                }
            }
            chatArea.setText(sb.toString());
        });

        // 채팅방 입장 시 서버에서 메시지 내역 1회 불러오기 (ownerId/roomName이 유효할 때만)
        if (ownerId != null && roomName != null) {
            loadChatHistoryFromServer();
        }

        // 새로고침 버튼 완전히 제거

        // 메시지 전송 버튼 이벤트 리스너
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessageFromInput();
            }
        });
        // 엔터키로 메시지 전송
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessageFromInput();
            }
        });

        // 실시간 채팅 갱신 스레드 복구 (ownerId/roomName이 유효할 때만 요청)
        Thread pollingThread = new Thread(() -> {
            int prevLineCount = 0;
            boolean firstValid = true;
            while (running) {
                try {
                    if (ownerId == null || roomName == null) {
                        Thread.sleep(200);
                        continue;
                    }
                    if (firstValid) {
                        firstValid = false;
                        // 이미 최초 1회 loadChatHistoryFromServer() 호출했으므로 skip
                        Thread.sleep(200);
                        continue;
                    }
                    java.util.List<String> lines = getChatHistoryLinesFromServer();
                    if (lines.size() > prevLineCount) {
                        for (int i = prevLineCount; i < lines.size(); i++) {
                            chatArea.append(lines.get(i) + "\n");
                        }
                        prevLineCount = lines.size();
                    }
                    Thread.sleep(200); // 0.2초마다 갱신
                } catch (Exception e) {
                    // 무시하고 계속
                }
            }
        });
        pollingThread.start();
        backButton.addActionListener(e -> {
            running = false;
            pollingThread.interrupt();
            cardLayout.show(mainPanel, "chatRoomListView");
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

    /**
     * 서버에 GET_CHAT_HISTORY 요청을 보내고, 결과를 chatArea에 표시
     */
    private void loadChatHistoryFromServer() {
        java.util.List<String> lines = getChatHistoryLinesFromServer();
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        chatArea.setText(sb.toString());
    }

    /**
     * 서버에 SEND_MESSAGE 요청을 보내고, 성공 여부 반환
     */
    private boolean sendMessageToServer(String msg) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("SEND_MESSAGE:OWNERID=" + ownerId + ",ROOM=" + roomName + ",MSG=" + msg + ",USERID=" + userId);
            writer.flush();
            String response = reader.readLine();
            return "SEND_MESSAGE_SUCCESS".equals(response);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 서버에 LEAVE_CHATROOM 명령을 보내고 성공 여부 반환
     */
    private boolean leaveChatRoomOnServer() {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("LEAVE_CHATROOM:OWNERID=" + ownerId + ",ROOM=" + roomName + ",USERID=" + userId);
            writer.flush();
            String response = reader.readLine();
            return "LEAVE_CHATROOM_SUCCESS".equals(response);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 서버에 INVITE_TO_CHATROOM 명령을 보내고 성공 여부 반환
     */
    private boolean inviteFriendToChatRoom(String friendId) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("INVITE_TO_CHATROOM:OWNERID=" + ownerId + ",ROOM=" + roomName + ",FRIENDID=" + friendId);
            writer.flush();
            String response = reader.readLine();
            return "INVITE_TO_CHATROOM_SUCCESS".equals(response);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 서버에서 채팅방 참여자 목록을 가져와서 반환
     */
    private java.util.Set<String> getParticipantsFromServer() {
        java.util.Set<String> set = new java.util.HashSet<>();
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("GET_PARTICIPANTS:OWNERID=" + ownerId + ",ROOM=" + roomName);
            writer.flush();
            String response = reader.readLine();
            if (response != null && response.startsWith("PARTICIPANTS:")) {
                String participants = response.substring(13); // "PARTICIPANTS:" 제거
                String[] ids = participants.split(",");
                for (String id : ids) {
                    String trimmed = id.trim();
                    if (!trimmed.isEmpty()) set.add(trimmed);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            // 서버에서 가져오기 실패 시 로컬 파일에서 시도
            return getCurrentParticipantsFromFile();
        }
        return set;
    }

    /**
     * 현재 채팅방 파일에서 참여자 목록을 파싱하여 반환
     */
    private java.util.Set<String> getCurrentParticipantsFromFile() {
        java.util.Set<String> set = new java.util.HashSet<>();
        try {
            // 일반방: roomName.txt, 비밀방: ownerId_roomName.txt
            String filename;
            if (ownerId == null || ownerId.isEmpty() || ownerId.equals(roomName)) {
                filename = roomName + ".txt";
            } else {
                filename = ownerId + "_" + roomName + ".txt";
            }
            java.io.File file = new java.io.File(filename);
            if (!file.exists()) return set;
            try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("참여자:")) {
                        String[] ids = line.substring(4).split(",");
                        for (String id : ids) {
                            String trimmed = id.trim();
                            if (!trimmed.isEmpty()) set.add(trimmed);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return set;
    }

    // 메시지 전송 공통 처리 함수
    private void sendMessageFromInput() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            boolean sent = sendMessageToServer(msg);
            if (sent) {
                inputField.setText("");
                loadChatHistoryFromServer(); // 메시지 전송 성공 시 채팅 내역 즉시 새로고침
            } else {
                JOptionPane.showMessageDialog(ChatRoomView.this, "메시지 전송 실패");
            }
        }
    }

    // 서버에서 전체 채팅 내역을 받아오는 함수 (리스트 반환)
    private java.util.List<String> getChatHistoryLinesFromServer() {
        java.util.List<String> lines = new java.util.ArrayList<>();
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("GET_CHAT_HISTORY:OWNERID=" + ownerId + ",ROOM=" + roomName);
            writer.flush();
            String line;
            while ((line = reader.readLine()) != null) {
                if ("END_OF_HISTORY".equals(line) || "NO_HISTORY".equals(line)) break;
                lines.add(line);
            }
        } catch (Exception e) {
            // 무시
        }
        return lines;
    }

    /** 별명 파일 불러오기 */
    private void loadNicknamesFromFile() {
        String myId = userId;
        if (myId == null) return;
        nicknameMap.clear();
        java.io.File file = new java.io.File("friend_nicknames_" + myId + ".txt");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String id = line.substring(0, idx);
                    String nick = line.substring(idx + 1);
                    nicknameMap.put(id, nick);
                }
            }
        } catch (Exception e) { /* 무시 */ }
    }
}