package controller;

import service.ChatService;
import model.ChatRoom;
import view.ChatListView;
import view.CreateChatView;
import view.ChatRoomView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ChatController {
    private final CreateChatView createChatView;
    private final Socket socket;
    private final String currentUserId;
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final ChatService chatService = new ChatService();
    private PrintWriter writer;

    public ChatController(CreateChatView createChatView, Socket socket, String currentUserId,
                          JFrame frame, CardLayout cardLayout, JPanel mainPanel) {
        this.socket = socket;
        this.currentUserId = currentUserId;
        this.createChatView = createChatView;
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        try {
            this.writer = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 연결 실패 (채팅방)");
        }

        setupEventListeners();
    }

    private void setupEventListeners() {
        // 친구 초대 버튼
        createChatView.getInviteBtn().addActionListener(e -> {
            String selectedFriend = createChatView.getFriendList().getSelectedValue();
            DefaultListModel<String> model = createChatView.getInvitedModel();
            if (selectedFriend != null && !model.contains(selectedFriend)) {
                model.addElement(selectedFriend);

                JList<String> invitedList = createChatView.getInvitedList();
                int lastIdx = model.getSize() - 1;
                if (lastIdx >= 0) {
                    invitedList.ensureIndexIsVisible(lastIdx);
                }
            }
        });

        // 초대 취소(삭제) 버튼
        createChatView.getDeleteBtn().addActionListener(e -> {
            JList<String> invitedList = createChatView.getInvitedList();
            String selected = invitedList.getSelectedValue();
            if (selected != null) {
                createChatView.getInvitedModel().removeElement(selected);
            }
        });

        // 일반 채팅방 생성 버튼
        createChatView.getNormalButton().addActionListener(e -> {
            String roomName = createChatView.getRoomNameField().getText().trim();
            DefaultListModel<String> model = createChatView.getInvitedModel();

            if (roomName.isEmpty() || model.isEmpty()) {
                JOptionPane.showMessageDialog(null, "방 이름과 초대할 친구를 입력해주세요.");
                return;
            }

            List<String> invitedFriends = new ArrayList<>();
            for (int i = 0; i < model.getSize(); i++) {
                invitedFriends.add(model.getElementAt(i));
            }
            if (!invitedFriends.contains(currentUserId)) {
                invitedFriends.add(currentUserId);
            }

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    boolean created = sendCreateChatRoomRequest(roomName, invitedFriends);
                    if (created) {
                        // 생성 성공 시 ChatRoomView로 이동
                        SwingUtilities.invokeLater(() -> {
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                            if (topFrame instanceof view.MainFrame) {
                                ((view.MainFrame) topFrame).refreshChatListView();
                            }
                            ChatRoomView chatRoomView = new ChatRoomView(roomName, currentUserId, socket, currentUserId, mainPanel, cardLayout);
                            mainPanel.add(chatRoomView, "chatRoomView");
                            cardLayout.show(mainPanel, "chatRoomView");
                            mainPanel.revalidate();
                            mainPanel.repaint();
                        });
                    }
                    return null;
                }
            }.execute();
        });

        // 비밀채팅방 생성 버튼
        createChatView.getSecretButton().addActionListener(e -> {
            String roomName = createChatView.getRoomNameField().getText().trim();
            DefaultListModel<String> model = createChatView.getInvitedModel();

            if (roomName.isEmpty() || model.isEmpty()) {
                JOptionPane.showMessageDialog(null, "방 이름과 초대할 친구를 입력해주세요.");
                return;
            }

            // 룸 코드 입력 다이얼로그
            String roomCode = JOptionPane.showInputDialog(null, "비밀채팅방 입장 코드를 입력하세요:", "룸 코드 설정", JOptionPane.PLAIN_MESSAGE);
            if (roomCode == null || roomCode.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "룸 코드를 입력해야 합니다.");
                return;
            }

            List<String> invitedFriends = new ArrayList<>();
            for (int i = 0; i < model.getSize(); i++) {
                invitedFriends.add(model.getElementAt(i));
            }
            if (!invitedFriends.contains(currentUserId)) {
                invitedFriends.add(currentUserId);
            }

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    boolean created = sendCreateSecretChatRoomRequest(roomName, invitedFriends, roomCode);
                    if (created) {
                        // 생성 성공 시 ChatRoomView로 이동
                        SwingUtilities.invokeLater(() -> {
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                            if (topFrame instanceof view.MainFrame) {
                                ((view.MainFrame) topFrame).refreshChatListView();
                            }
                            ChatRoomView chatRoomView = new ChatRoomView(roomName, currentUserId, socket, currentUserId, mainPanel, cardLayout);
                            mainPanel.add(chatRoomView, "chatRoomView");
                            cardLayout.show(mainPanel, "chatRoomView");
                            mainPanel.revalidate();
                            mainPanel.repaint();
                        });
                    }
                    return null;
                }
            }.execute();
        });
    }

    private boolean sendCreateChatRoomRequest(String roomName, List<String> invitedFriends) {
        if (writer == null) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결이 없습니다."));
            return false;
        }
        try {
            writer.println("CREATE_CHATROOM");
            writer.println("roomName:" + roomName);
            writer.println("owner:" + currentUserId);
            writer.println("invited:" + String.join(",", invitedFriends));
            writer.flush();

            // 서버 응답 대기
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            if ("CREATE_CHATROOM_SUCCESS".equals(response)) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "채팅방 생성 완료 및 파일 저장 성공"));
                return true;
            } else {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "채팅방 생성 요청은 성공했지만 파일 저장 실패"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버에 채팅방 생성 요청 실패"));
            return false;
        }
    }

    private boolean sendCreateSecretChatRoomRequest(String roomName, List<String> invitedFriends, String roomCode) {
        if (writer == null) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결이 없습니다."));
            return false;
        }
        try {
            writer.println("CREATE_SECRET_CHATROOM");
            writer.println("roomName:" + roomName);
            writer.println("owner:" + currentUserId);
            writer.println("invited:" + String.join(",", invitedFriends));
            writer.println("roomCode:" + roomCode);
            writer.flush();

            // 서버 응답 대기
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            if ("CREATE_CHATROOM_SUCCESS".equals(response)) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "비밀채팅방 생성 완료 및 파일 저장 성공"));
                return true;
            } else {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "비밀채팅방 생성 요청은 성공했지만 파일 저장 실패"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버에 비밀채팅방 생성 요청 실패"));
            return false;
        }
    }

    public CreateChatView getCreateChatView() {
        return createChatView;
    }
}
