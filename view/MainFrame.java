package view;

import controller.FriendAddController;
import controller.LoginController;
import controller.SignUpController;
import model.ChatRoom;
import service.ChatService;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    /** 친구 목록 저장 리스트 */
    private List<String> myFriends = new ArrayList<>();

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
        // createChatView는 myFriends가 채워진 이후에 다시 초기화됨 (setMyId 참고)
        CreateChatView createChatView = new CreateChatView(cardLayout, mainPanel, myFriends);
        List<ChatRoom> chatRooms = new ChatService().getAllChatRooms(); // 채팅방 목록 불러오기
        ChatListView chatListView = new ChatListView(cardLayout, mainPanel, chatRooms, myId); // 현재 로그인 사용자 ID 전달
        chatRoomView = new ChatRoomView("채팅방", "사용자", () -> cardLayout.show(mainPanel, "homeView"));

        // 메인 패널에 뷰 등록
        mainPanel.add(loginView, "loginView");
        mainPanel.add(homeView, "homeView");
        mainPanel.add(signupView, "signupView");
        mainPanel.add(createChatView, "createChatRoomView");
        mainPanel.add(chatListView, "chatRoomListView");
        mainPanel.add(chatRoomView, "chatRoomView");

        // 홈 뷰 친구추가 버튼 이벤트
        homeView.getAddFriendButton().addActionListener(e -> {
            FriendAddView friendAddView = new FriendAddView(this);
            new FriendAddController(friendAddView, socket, myId, homeView);
            friendAddView.setVisible(true);
        });

        // 홈 뷰 → 다른 뷰 전환 핸들러 설정
        homeView.setViewChangeHandler(viewName -> {
            if (viewName.equals("createChatRoomView")) {
                refreshCreateChatView();
            }
            cardLayout.show(mainPanel, viewName);
        });

        // 로그인 → 회원가입 이동
        loginView.getJoinButton().addActionListener(e -> cardLayout.show(mainPanel, "signupView"));

        // 로그인, 회원가입 컨트롤러 연결
        SignUpController signUpController = new SignUpController(signupView, mainPanel, cardLayout);
        LoginController loginController = new LoginController(loginView, mainPanel, cardLayout);
        loginController.setMainFrame(this);

        // 메인 패널 연결 및 시작 화면 설정
        add(mainPanel);
        cardLayout.show(mainPanel, "loginView");

        // 프레임 보이기
        setVisible(true);
    }

    /**
     * 현재 사용자 ID 설정 및 친구 목록 로드
     *
     * @param myId 사용자 ID
     */
    public void setMyId(String myId) {
        this.myId = myId;
        loadFriendsFromFile(myId);
    }

    /**
     * 친구 목록 파일에서 불러오기
     *
     * @param userId 사용자 ID
     */
    private void loadFriendsFromFile(String userId) {
        File file = new File("friends_" + userId + ".txt");
        myFriends.clear();
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                myFriends.add(line); // 친구 리스트에 추가
                homeView.addFriendToList(line); // 홈 뷰에도 추가
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 채팅방 만들기 뷰 갱신 (친구 목록 최신화 반영)
     */
    private void refreshCreateChatView() {
        // 기존 CreateChatView 제거
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof CreateChatView) {
                mainPanel.remove(comp);
                break;
            }
        }

        // 새로운 CreateChatView 생성 및 등록
        CreateChatView updated = new CreateChatView(cardLayout, mainPanel, myFriends);
        mainPanel.add(updated, "createChatRoomView");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * 홈 뷰 반환
     */
    public HomeView getHomeView() {
        return homeView;
    }

    /**
     * 채팅방 열기 및 뷰 전환
     */
    public void openChatRoom(String roomTitle, String userName) {
        mainPanel.remove(chatRoomView);
        chatRoomView = new ChatRoomView(roomTitle, userName, () -> cardLayout.show(mainPanel, "homeView"));
        mainPanel.add(chatRoomView, "chatRoomView");
        mainPanel.revalidate();
        mainPanel.repaint();
        cardLayout.show(mainPanel, "chatRoomView");
    }
}