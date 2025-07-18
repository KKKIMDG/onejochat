package controller;

import service.FriendService;
import service.UserService;
import view.FriendAddView;

import javax.swing.*;

public class FriendAddController {
    private final FriendAddView view;
    private final FriendService friendService;
    private final UserService userService;
    private final String myId;

    public FriendAddController(FriendAddView view, FriendService friendService, UserService userService, String myId) {
        this.view = view;
        this.friendService = friendService;
        this.userService = userService;
        this.myId = myId;

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
        if (friendService.isAlreadyFriend(myId, toId)) {
            JOptionPane.showMessageDialog(view, "⚠️ 이미 친구입니다.");
            return;
        }
        if (friendService.addFriend(myId, toId)) {
            JOptionPane.showMessageDialog(view, "🎉 친구가 추가되었습니다!");
            view.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(view, "❌ 친구 추가 실패");
        }
    }
}