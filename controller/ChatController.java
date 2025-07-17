package controller;

import view.CreateChatView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ChatController {
    private final CreateChatView createChatView;
    private final Socket socket;
    private final String currentUserId; //

    public ChatController(CreateChatView createChatView, Socket socket, String currentUserId) {
        this.socket = socket;
        this.currentUserId = currentUserId;
        this.createChatView = createChatView;

        // 버튼 이벤트 등록
        setupEventListeners();
    }

    private void setupEventListeners() {
        // 친구 초대 버튼 이벤트
        createChatView.getInviteBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFriend = createChatView.getFriendList().getSelectedValue();
                DefaultListModel<String> model = createChatView.getInvitedModel();

                if (selectedFriend != null && !model.contains(selectedFriend)) {
                    model.addElement(selectedFriend);
                }
            }
        });

        // 일반 채팅방 생성 버튼 이벤트
        createChatView.getNormalButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = createChatView.getRoomNameField().getText().trim();
                DefaultListModel<String> model = createChatView.getInvitedModel();

                if (roomName.isEmpty() || model.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "방 이름과 초대할 친구를 입력해주세요.");
                    return;
                }

                // DefaultListModel -> List<String> 변환
                List<String> invitedFriends = new java.util.ArrayList<>();
                for (int i = 0; i < model.getSize(); i++) {
                    invitedFriends.add(model.getElementAt(i));
                }

                try {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);

                    writer.println("CREATE_CHATROOM");
                    writer.println("roomName:" + roomName);
                    writer.println("owner:" + currentUserId);
                    writer.println("invited:" + String.join(",", invitedFriends));

                    JOptionPane.showMessageDialog(null, "채팅방 생성 요청 완료");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "서버에 채팅방 생성 요청 실패");
                }
            }
        });
    }

    public CreateChatView getCreateChatView() {
        return createChatView;
    }
}