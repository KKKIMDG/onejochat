package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HomeView 클래스는 친구 목록을 보여주고,
 * 친구 추가, 채팅방 생성/조회 등의 기능으로 연결되는 메인 화면이다.
 */
public class HomeView extends JPanel {

    public HomeView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔷 상단 로고 및 친구 추가 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel logo = new JLabel("onejo"); // 좌측 로고
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        JButton addFriendBtn = new JButton("친구추가 +"); // 우측 친구추가 버튼
        addFriendBtn.setBackground(Color.WHITE);
        addFriendBtn.setForeground(new Color(0x007BFF));
        addFriendBtn.setBorderPainted(false);
        addFriendBtn.setFocusPainted(false);
        addFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 친구 추가 버튼 클릭 시 다이얼로그 표시
        addFriendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FriendAddView dialog = new FriendAddView((JFrame) SwingUtilities.getWindowAncestor(HomeView.this));
                dialog.setVisible(true);
            }
        });

        topPanel.add(logo, BorderLayout.WEST);
        topPanel.add(addFriendBtn, BorderLayout.EAST);

        // 🔷 검색창 영역
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JTextField searchField = new JTextField("Search messages, people");  // 검색 필드
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // 🔷 친구 목록 타이틀
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(Color.WHITE);

        JLabel friendLabel = new JLabel("👥 친구목록");
        friendLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        friendLabel.setForeground(Color.DARK_GRAY);
        labelPanel.add(friendLabel);

        // 🔷 친구 리스트 (샘플)
        DefaultListModel<String> friendListModel = new DefaultListModel<>();
        friendListModel.addElement("다빈");
        friendListModel.addElement("동건");

        JList<String> friendList = new JList<>(friendListModel);
        friendList.setFont(new Font("SansSerif", Font.BOLD, 25));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // 하나만 선택 가능

        // 🔷 채팅방 만들기 / 목록 보기 버튼
        JButton createRoomBtn = new JButton("채팅방 만들기");
        createRoomBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        createRoomBtn.setBackground(Color.WHITE);
        createRoomBtn.setForeground(new Color(0x007BFF));
        createRoomBtn.setBorderPainted(false);

        JButton listRoomBtn = new JButton("채팅방 목록 보기");
        listRoomBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        listRoomBtn.setBackground(Color.WHITE);
        listRoomBtn.setForeground(new Color(0x007BFF));
        listRoomBtn.setBorderPainted(false);

        // 채팅방 만들기 버튼 클릭 시 다이얼로그 실행
        createRoomBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateChatView dialog = new CreateChatView((JFrame) SwingUtilities.getWindowAncestor(HomeView.this));
                dialog.setVisible(true);
            }
        });

        // 채팅방 목록 보기 버튼 클릭 시 다이얼로그 실행
        listRoomBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatListView dialog = new ChatListView((JFrame) SwingUtilities.getWindowAncestor(HomeView.this));
                dialog.setVisible(true);
            }
        });

        // 하단 버튼 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(listRoomBtn);
        bottomPanel.add(createRoomBtn);

        // 🔷 상단 전체 wrapper
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(Color.WHITE);
        topWrapper.add(topPanel);
        topWrapper.add(searchPanel);
        topWrapper.add(labelPanel);

        // 🔷 전체 레이아웃 배치
        add(topWrapper, BorderLayout.NORTH);                          // 상단
        add(new JScrollPane(friendList), BorderLayout.CENTER);        // 친구 리스트
        add(bottomPanel, BorderLayout.SOUTH);                         // 하단 버튼
    }
}