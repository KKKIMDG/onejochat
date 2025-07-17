package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.function.Consumer;
//
/**
 * 홈 뷰 클래스
 * 로그인 후 메인 화면을 제공하는 클래스입니다.
 * 친구 목록, 검색 기능, 채팅방 생성/목록 기능을 포함합니다.
 */
public class HomeView extends JPanel {
    /** 친구 추가 버튼 */
    private JButton addFriendBtn;
    /** 친구 목록 모델 */
    private DefaultListModel<String> friendListModel;
    /** 친구 목록 리스트 */
    private JList<String> friendList;
    /** 친구 ID 중복 방지를 위한 HashSet */
    private HashSet<String> friendSet = new HashSet<>();
    /** 채팅방 만들기 버튼 */
    private JButton createRoomBtn;
    /** 채팅방 목록 보기 버튼 */
    private JButton listRoomBtn;

    /**
     * 홈 뷰 생성자
     * 
     * @param cardLayout 카드 레이아웃
     * @param mainPanel 메인 패널
     */
    public HomeView(CardLayout cardLayout, JPanel mainPanel) {
        // 패널 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 패널 생성 (로고 및 친구추가 버튼)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // 로고 라벨 생성
        JLabel logo = new JLabel("onejo");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        // 친구추가 버튼 생성
        addFriendBtn = new JButton("친구추가 +");
        addFriendBtn.setBackground(Color.WHITE);
        addFriendBtn.setForeground(new Color(0x007BFF));
        addFriendBtn.setBorderPainted(false);
        addFriendBtn.setFocusPainted(false);
        addFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 상단 패널에 컴포넌트 추가
        topPanel.add(logo, BorderLayout.WEST);
        topPanel.add(addFriendBtn, BorderLayout.EAST);

        // 검색 패널 생성
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // 검색 아이콘 생성
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // 검색 필드 생성
        JTextField searchField = new JTextField("Search messages, people");
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 검색 패널에 컴포넌트 추가
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // 친구목록 타이틀 패널 생성
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(Color.WHITE);

        JLabel friendLabel = new JLabel("👥 친구목록");
        friendLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        friendLabel.setForeground(Color.DARK_GRAY);
        labelPanel.add(friendLabel);

        // 친구목록 리스트 생성
        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setFont(new Font("SansSerif", Font.BOLD, 25));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 하단 버튼 패널 생성
        createRoomBtn = new JButton("채팅방 만들기");
        createRoomBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        createRoomBtn.setBackground(Color.WHITE);
        createRoomBtn.setForeground(new Color(0x007BFF));
        createRoomBtn.setBorderPainted(false);

        listRoomBtn = new JButton("채팅방 목록 보기");
        listRoomBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        listRoomBtn.setBackground(Color.WHITE);
        listRoomBtn.setForeground(new Color(0x007BFF));
        listRoomBtn.setBorderPainted(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(listRoomBtn);
        bottomPanel.add(createRoomBtn);

        // 전체 상단 래퍼 패널 생성
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(Color.WHITE);
        topWrapper.add(topPanel);
        topWrapper.add(searchPanel);
        topWrapper.add(labelPanel);

        // 메인 패널에 컴포넌트 추가
        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(friendList), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 친구 추가 버튼을 반환합니다.
     * 
     * @return 친구 추가 버튼
     */
    public JButton getAddFriendButton() {
        return addFriendBtn;
    }

    /**
     * 친구 목록에 친구를 추가합니다.
     * 다양한 형식의 친구 데이터를 파싱하여 처리합니다.
     * 
     * @param friendLine 친구 정보 문자열
     */
    public void addFriendToList(String friendLine) {
        String friendId = null;

        // "FRIEND:123,mexaen" 형식 파싱
        if (friendLine.startsWith("FRIEND:")) {
            String[] parts = friendLine.substring(7).split(",");
            if (parts.length == 2) {
                // 현재 사용자가 아닌 상대방 ID를 선택
                friendId = parts[0].equals(LoginController.getCurrentUserId()) ? parts[1] : parts[0];
            }
        }
        // "mexaen" 형식 파싱
        else {
            friendId = friendLine.trim();
        }

        // 중복 방지하여 친구 목록에 추가
        if (friendId != null && !friendSet.contains(friendId)) {
            friendSet.add(friendId);
            friendListModel.addElement(friendId);
        }
    }

    /**
     * 파일에서 친구 목록을 새로고침합니다.
     * 
     * @param myId 현재 사용자 ID
     */
    public void refreshFriendListFromFile(String myId) {
        // 기존 목록 초기화
        friendListModel.clear();
        friendSet.clear();

        // 친구 목록 파일 읽기
        File file = new File("friends_" + myId + ".txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addFriendToList(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 뷰 전환 핸들러를 설정합니다.
     * 버튼 클릭 시 적절한 뷰로 전환하는 기능을 제공합니다.
     * 
     * @param handler 뷰 전환을 처리할 Consumer 함수
     */
    public void setViewChangeHandler(Consumer<String> handler) {
        createRoomBtn.addActionListener(e -> handler.accept("createChatRoomView"));
        listRoomBtn.addActionListener(e -> handler.accept("chatRoomListView"));
    }
}