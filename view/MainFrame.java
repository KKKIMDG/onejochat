package view;

import controller.FriendAddController;
import controller.LoginController;
import controller.SignUpController;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Socket socket;
    private String myId;

    private HomeView homeView;
    private ChatRoomView chatRoomView;

    public MainFrame(Socket socket) {
        this.socket = socket;

        setTitle("onejo");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔹 레이아웃 구성
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 🔹 View들 생성
        LoginView loginView = new LoginView(cardLayout, mainPanel);
        homeView = new HomeView(cardLayout, mainPanel);
        SignupView signupView = new SignupView(cardLayout, mainPanel);
        CreateChatView createChatView = new CreateChatView(cardLayout, mainPanel);
        ChatListView chatListView = new ChatListView(cardLayout, mainPanel);

        // 🔹 ChatRoomView는 최초 생성, 이후 내용만 바꾸는 방식
        chatRoomView = new ChatRoomView("채팅방", "사용자", () -> cardLayout.show(mainPanel, "homeView"));

        // 🔹 패널 등록
        mainPanel.add(loginView, "loginView");
        mainPanel.add(homeView, "homeView");
        mainPanel.add(signupView, "signupView");
        mainPanel.add(createChatView, "createChatRoomView");
        mainPanel.add(chatListView, "chatRoomListView");
        mainPanel.add(chatRoomView, "chatRoomView");

        // 🔹 홈 뷰 친구 추가
        homeView.getAddFriendButton().addActionListener(e -> {
            FriendAddView friendAddView = new FriendAddView(this);
            new FriendAddController(friendAddView, socket, myId, homeView);
            friendAddView.setVisible(true);
        });

        // 🔹 뷰 전환 핸들링
        homeView.setViewChangeHandler(viewName -> cardLayout.show(mainPanel, viewName));

        // 🔹 로그인 → 회원가입 전환
        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 🔹 컨트롤러 연결
        SignUpController signUpController = new SignUpController(signupView, mainPanel, cardLayout);
        LoginController loginController = new LoginController(loginView, mainPanel, cardLayout);
        loginController.setMainFrame(this);

        // 🔹 패널 적용
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");

        setVisible(true);
    }

    public void setMyId(String myId) {
        this.myId = myId;
        loadFriendsFromFile(myId);
    }

    private void loadFriendsFromFile(String userId) {
        File file = new File("friends_" + userId + ".txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                homeView.addFriendToList(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HomeView getHomeView() {
        return homeView;
    }

    // 🔹 채팅방 열기: 기존 chatRoomView를 새로운 것으로 교체
    public void openChatRoom(String roomTitle, String userName) {
        // 기존 chatRoomView를 mainPanel에서 제거
        mainPanel.remove(chatRoomView);

        // 새로운 ChatRoomView 생성
        chatRoomView = new ChatRoomView(roomTitle, userName, () -> cardLayout.show(mainPanel, "homeView"));

        // 새로운 chatRoomView를 mainPanel에 추가
        mainPanel.add(chatRoomView, "chatRoomView");

        // 레이아웃 갱신
        mainPanel.revalidate();
        mainPanel.repaint();

        // 채팅방 화면으로 전환
        cardLayout.show(mainPanel, "chatRoomView");
    }
}