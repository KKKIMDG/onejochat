package controller;

import view.CreateChatView;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ChatController {
    private final CreateChatView createChatView;
    private final Socket socket;
    private final String currentUserId;
    private PrintWriter writer;

    public ChatController(CreateChatView createChatView, Socket socket, String currentUserId) {
        this.socket = socket;
        this.currentUserId = currentUserId;
        this.createChatView = createChatView;
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
        // 친구 초대 버튼: 친구 추가 & 자동 스크롤
        createChatView.getInviteBtn().addActionListener(e -> {
            String selectedFriend = createChatView.getFriendList().getSelectedValue();
            DefaultListModel<String> model = createChatView.getInvitedModel();
            if (selectedFriend != null && !model.contains(selectedFriend)) {
                model.addElement(selectedFriend);

                // ====== 이 부분이 '초대한 친구' 목록으로 자동 스크롤! ======
                JList<String> invitedList = createChatView.getInvitedList();
                int lastIdx = model.getSize() - 1;
                if (lastIdx >= 0) {
                    invitedList.ensureIndexIsVisible(lastIdx);
                }
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
                    sendCreateChatRoomRequest(roomName, invitedFriends);
                    return null;
                }
            }.execute();
        });
    }

    private void sendCreateChatRoomRequest(String roomName, List<String> invitedFriends) {
        if (writer == null) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결이 없습니다."));
            return;
        }
        try {
            writer.println("CREATE_CHATROOM");
            writer.println("roomName:" + roomName);
            writer.println("owner:" + currentUserId);
            writer.println("invited:" + String.join(",", invitedFriends));
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "채팅방 생성 요청 완료"));
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버에 채팅방 생성 요청 실패"));
        }
    }

    public CreateChatView getCreateChatView() {
        return createChatView;
    }
}
