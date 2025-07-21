package controller;

import service.FriendService;
import service.UserService;
import view.FriendAddView;
import view.MainFrame;

import javax.swing.*;

public class FriendAddController {
    private final FriendAddView view;
    private final FriendService friendService;
    private final UserService userService;
    private final String myId;
    private final MainFrame mainFrame;

    public FriendAddController(FriendAddView view, FriendService friendService, UserService userService, String myId, MainFrame mainFrame) {
        this.view = view;
        this.friendService = friendService;
        this.userService = userService;
        this.myId = myId;
        this.mainFrame = mainFrame;
        view.getSearchButton().addActionListener(e -> handleSearch());
        view.getRequestButton().addActionListener(e -> handleAddFriend());
    }

    private void handleSearch() {
        String inputId = view.getInputId();
        if (inputId.isEmpty()) {
            view.setResultText("❗ 아이디를 입력해주세요.");
            return;
        }
        String name = userService.findUserNameById(inputId);
        if (name != null) {
            view.setResultText("✅ 이름: " + name);
        } else {
            view.setResultText("❌ 사용자를 찾을 수 없습니다.");
        }
    }

    private void handleAddFriend() {
        String toId = view.getInputId();
        String resultText = view.getResultText();
        if (!resultText.startsWith("✅")) {
            JOptionPane.showMessageDialog(view, "❗ 먼저 친구를 검색하세요.");
            return;
        }
        if (toId.equals(myId)) {
            JOptionPane.showMessageDialog(view, "⚠️ 본인 아이디는 친구로 추가할 수 없습니다.");
            return;
        }
        // 서버에 친구 추가 요청
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 9001);
            java.io.PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            writer.println("REQUEST_FRIEND:FROM=" + myId + ",TO=" + toId);
            writer.flush();
            String response = reader.readLine();
            if ("FRIEND_ADDED".equals(response)) {
                JOptionPane.showMessageDialog(view, "🎉 친구가 추가되었습니다!");
                mainFrame.refreshFriendsList(); // 친구 목록 갱신
                view.setVisible(false);
            } else if ("FRIEND_FAIL".equals(response)) {
                JOptionPane.showMessageDialog(view, "❌ 친구 추가 실패");
            } else {
                JOptionPane.showMessageDialog(view, "❌ 알 수 없는 오류");
            }
            socket.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "❌ 서버 연결 실패: " + e.getMessage());
        }
    }
}