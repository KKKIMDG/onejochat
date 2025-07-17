package view;

import controller.FriendAddController;
import controller.LoginController;
import controller.SignUpController;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * 메인 프레임 클래스
 * 채팅 애플리케이션의 메인 윈도우를 관리하는 클래스입니다.
 * 카드 레이아웃을 사용하여 여러 뷰 간의 전환을 관리합니다.
 */
public class MainFrame extends JFrame {
    /** 카드 레이아웃 - 여러 뷰 간 전환을 위한 레이아웃 매니저 */
    private CardLayout cardLayout;
    /** 메인 패널 - 모든 뷰가 포함되는 컨테이너 */
    private JPanel mainPanel;
    /** 서버와의 소켓 연결 */
    private Socket socket;
    /** 현재 로그인한 사용자의 ID */
    private String myId;

    /** 홈 뷰 참조 */
    private HomeView homeView;
    /** 채팅방 뷰 참조 */
    private ChatRoomView chatRoomView;

    /**
     * 메인 프레임 생성자
     * 
     * @param socket 서버와의 소켓 연결
     */
    public MainFrame(Socket socket) {
        this.socket = socket;

        // 프레임 기본 설정
        setTitle("onejo");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 화면 중앙에 위치

        // 카드 레이아웃 및 메인 패널 초기화
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 모든 뷰 생성
        LoginView loginView = new LoginView(cardLayout, mainPanel);
        homeView = new HomeView(cardLayout, mainPanel);
        SignupView signupView = new SignupView(cardLayout, mainPanel);
        CreateChatView createChatView = new CreateChatView(cardLayout, mainPanel);
        ChatListView chatListView = new ChatListView(cardLayout, mainPanel);

        // ChatRoomView는 최초 생성, 이후 내용만 바꾸는 방식으로 재사용
        chatRoomView = new ChatRoomView("채팅방", "사용자", () -> cardLayout.show(mainPanel, "homeView"));

        // 메인 패널에 모든 뷰 등록
        mainPanel.add(loginView, "loginView");
        mainPanel.add(homeView, "homeView");
        mainPanel.add(signupView, "signupView");
        mainPanel.add(createChatView, "createChatRoomView");
        mainPanel.add(chatListView, "chatRoomListView");
        mainPanel.add(chatRoomView, "chatRoomView");

        // 홈 뷰의 친구 추가 버튼 이벤트 처리
        homeView.getAddFriendButton().addActionListener(e -> {
            FriendAddView friendAddView = new FriendAddView(this);
            new FriendAddController(friendAddView, socket, myId, homeView);
            friendAddView.setVisible(true);
        });
//
        // 홈 뷰의 뷰 전환 핸들러 설정
        homeView.setViewChangeHandler(viewName -> cardLayout.show(mainPanel, viewName));

        // 로그인 뷰에서 회원가입 뷰로 전환하는 버튼 이벤트
        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 각 뷰에 대한 컨트롤러 생성 및 연결
        SignUpController signUpController = new SignUpController(signupView, mainPanel, cardLayout);
        LoginController loginController = new LoginController(loginView, mainPanel, cardLayout);
        loginController.setMainFrame(this);

        // 메인 패널을 프레임에 추가하고 초기 화면 설정
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");

        // 프레임을 화면에 표시
        setVisible(true);
    }

    /**
     * 현재 사용자 ID를 설정하고 친구 목록을 로드합니다.
     * 
     * @param myId 설정할 사용자 ID
     */
    public void setMyId(String myId) {
        this.myId = myId;
        loadFriendsFromFile(myId);
    }

    /**
     * 파일에서 사용자의 친구 목록을 로드합니다.
     * 
     * @param userId 친구 목록을 로드할 사용자 ID
     */
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

    /**
     * 홈 뷰를 반환합니다.
     * 
     * @return 홈 뷰 참조
     */
    public HomeView getHomeView() {
        return homeView;
    }

    /**
     * 새로운 채팅방을 열고 화면을 전환합니다.
     * 기존 chatRoomView를 새로운 것으로 교체하여 재사용합니다.
     * 
     * @param roomTitle 채팅방 제목
     * @param userName 채팅 상대방 사용자 이름
     */
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