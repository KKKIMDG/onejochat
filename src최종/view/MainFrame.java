package view;

import controller.FriendAddController;
import controller.LoginController;
import controller.SignUpController;
import model.ChatRoom;
import service.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public List<String> myFriends = new ArrayList<>();

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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 종료 동작 커스텀
        setLocationRelativeTo(null);  // 화면 중앙에 위치

        // x버튼(윈도우 종료) 클릭 시 로그아웃/종료 선택 다이얼로그
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 현재 보여지는 화면이 로그인 화면이면 바로 종료
                Component visible = null;
                for (Component comp : mainPanel.getComponents()) {
                    if (comp.isVisible()) {
                        visible = comp;
                        break;
                    }
                }
                if (visible != null && visible.getClass().getSimpleName().equals("LoginView")) {
                    System.exit(0);
                    return;
                }
                // 그 외에는 기존처럼 로그아웃/종료 선택
                int result = JOptionPane.showOptionDialog(
                        MainFrame.this,
                        "로그아웃 또는 종료를 선택하세요.",
                        "로그아웃/종료",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"로그아웃", "종료"},
                        "로그아웃");
                if (result == JOptionPane.YES_OPTION) { // 로그아웃
                    try {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println("LOGOUT:ID=" + myId);
                        writer.flush();
                        socket.close();
                    } catch (Exception ex) {
                        // 이미 닫혔거나 예외 무시
                    }
                    myFriends.clear();
                    myId = null;
                    if (homeView != null) homeView.refreshFriendListFromFile("");
                    cardLayout.show(mainPanel, "loginView");
                } else if (result == JOptionPane.NO_OPTION) { // 종료
                    System.exit(0);
                }
            }
        });

        // 카드 레이아웃 및 메인 패널 초기화
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 모든 뷰 생성
        LoginView loginView = new LoginView(cardLayout, mainPanel);
        homeView = new HomeView(cardLayout, mainPanel);
        SignupView signupView = new SignupView(cardLayout, mainPanel);
        // createChatView는 myFriends가 채워진 이후에 다시 초기화됨 (setMyId 참고)
        CreateChatView createChatView = new CreateChatView(cardLayout, mainPanel, myFriends);
        new controller.ChatController(createChatView, socket, myId, this, cardLayout, mainPanel);
        // 로그인 성공 시 서버에서 내 채팅방 목록 받아오기
        List<String> myRoomNames = new ArrayList<>();
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("GET_MY_CHATROOMS:ID=" + myId);
            writer.flush();
            String line;
            while ((line = reader.readLine()) != null) {
                if ("END_OF_CHATROOMS".equals(line)) break;
                myRoomNames.add(line);
            }
        } catch (Exception e) {
            // 에러 시 빈 목록
        }
        // ChatRoom 객체로 변환 (참여자 정보 없이 이름만)
        List<ChatRoom> myChatRooms = new ArrayList<>();
        for (String room : myRoomNames) {
            myChatRooms.add(new ChatRoom(room, List.of(myId))); // 임시로 참여자 myId만
        }
        ChatListView chatListView = new ChatListView(cardLayout, mainPanel, myChatRooms, myId);
        chatRoomView = new ChatRoomView("채팅방", myId, socket, myId, mainPanel, cardLayout);

        // 메인 패널에 뷰 등록
        mainPanel.add(loginView, "loginView");
        mainPanel.add(homeView, "homeView");
        mainPanel.add(signupView, "signupView");
        mainPanel.add(createChatView, "createChatRoomView");
        mainPanel.add(chatListView, "chatRoomListView");
        mainPanel.add(chatRoomView, "chatRoomView");

        // 홈 뷰 친구추가 버튼 이벤트
        homeView.getAddFriendButton().addActionListener(e -> {
            try {
                if (socket == null || socket.isClosed()) {
                    JOptionPane.showMessageDialog(this, "서버와의 연결이 끊어졌습니다. 다시 로그인 해주세요.");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "서버 소켓 상태 확인 중 오류가 발생했습니다.");
                return;
            }
            FriendAddView friendAddView = new FriendAddView(this);
            service.FriendService friendService = new service.FriendService();
            service.UserService userService = new service.UserService();
            new FriendAddController(friendAddView, friendService, userService, myId);
            friendAddView.setVisible(true);
        });

        // 홈 뷰 하단 버튼 직접 뷰 전환 연결
        homeView.getCreateRoomButton().addActionListener(e -> {
            refreshCreateChatView();
            cardLayout.show(mainPanel, "createChatRoomView");
        });
        homeView.getListRoomButton().addActionListener(e -> {
            refreshChatListView();
            cardLayout.show(mainPanel, "chatRoomListView");
        });

        // 홈 뷰 → 다른 뷰 전환 핸들러 설정
        // homeView.setViewChangeHandler(viewName -> {
        //     if (viewName.equals("createChatRoomView")) {
        //         refreshCreateChatView();
        //     }
        //     cardLayout.show(mainPanel, viewName);
        // });

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
        refreshChatListView();
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
        new controller.ChatController(updated, socket, myId, this, cardLayout, mainPanel);
        mainPanel.add(updated, "createChatRoomView");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * 채팅방 목록 뷰 갱신 (서버에서 최신 채팅방 목록 가져오기)
     */
    public void refreshChatListView() {
        List<String> myRoomNames = new ArrayList<>();
        try {
            // 기존 소켓 대신 매번 새로운 소켓으로 요청
            try (Socket tempSocket = new Socket("100.100.101.30", 9001);
                 PrintWriter writer = new PrintWriter(tempSocket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()))) {
                writer.println("GET_MY_CHATROOMS:ID=" + myId);
                writer.flush();
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("END_OF_CHATROOMS".equals(line)) break;
                    myRoomNames.add(line);
                }
            }
        } catch (Exception e) {
            // 에러 시 빈 목록
        }
        List<ChatRoom> myChatRooms = new ArrayList<>();
        for (String room : myRoomNames) {
            myChatRooms.add(new ChatRoom(room, List.of(myId)));
        }
        // 기존 chatRoomListView 완전히 제거
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChatListView) {
                mainPanel.remove(comp);
                break;
            }
        }
        ChatListView chatListView = new ChatListView(cardLayout, mainPanel, myChatRooms, myId);
        mainPanel.add(chatListView, "chatRoomListView");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * 친구 목록을 강제로 새로고침 (파일에서 다시 읽기)
     */
    public void refreshFriendsList() {
        loadFriendsFromFile(myId);
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
    public void openChatRoom(String roomName, String ownerId) {
        // 일반방이면 ownerId=roomName으로 강제
        if (ownerId == null || ownerId.isEmpty() || ownerId.equals(roomName)) {
            ownerId = roomName;
            mainPanel.remove(chatRoomView);
            chatRoomView = new ChatRoomView(roomName, ownerId, socket, myId, mainPanel, cardLayout);
            mainPanel.add(chatRoomView, "chatRoomView");
            mainPanel.revalidate();
            mainPanel.repaint();
            cardLayout.show(mainPanel, "chatRoomView");
        } else {
            // 비밀방: 코드 입력 다이얼로그
            SecretChatCodeDialog dialog = new SecretChatCodeDialog(this);
            dialog.setVisible(true);
            String code = dialog.getCode();
            if (code == null || code.isEmpty()) return;
            mainPanel.remove(chatRoomView);
            chatRoomView = new ChatRoomView(roomName, ownerId, socket, myId, mainPanel, cardLayout);
            mainPanel.add(chatRoomView, "chatRoomView");
            mainPanel.revalidate();
            mainPanel.repaint();
            cardLayout.show(mainPanel, "chatRoomView");
        }
    }
}