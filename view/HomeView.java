package view;

import javax.swing.*;
import java.awt.*;

/**
 * HomeView 클래스는 친구 목록을 보여주고,
 * 친구 추가, 채팅방 생성/조회 등의 기능으로 연결되는 메인 화면이다.
 */
public class HomeView extends JPanel {

    private JButton addFriendBtn;

    public HomeView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔷 상단 로고 및 친구 추가 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel logo = new JLabel("onejo");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        addFriendBtn = new JButton("친구추가 +"); // 🔹 필드로 선언
        addFriendBtn.setBackground(Color.WHITE);
        addFriendBtn.setForeground(new Color(0x007BFF));
        addFriendBtn.setBorderPainted(false);
        addFriendBtn.setFocusPainted(false);
        addFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        topPanel.add(logo, BorderLayout.WEST);
        topPanel.add(addFriendBtn, BorderLayout.EAST);

        // 🔷 검색창 영역
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JTextField searchField = new JTextField("Search messages, people");
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

        DefaultListModel<String> friendListModel = new DefaultListModel<>();
        JList<String> friendList = new JList<>(friendListModel);
        friendList.setFont(new Font("SansSerif", Font.BOLD, 25));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(listRoomBtn);
        bottomPanel.add(createRoomBtn);

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(Color.WHITE);
        topWrapper.add(topPanel);
        topWrapper.add(searchPanel);
        topWrapper.add(labelPanel);

        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(friendList), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getAddFriendButton() {
        return addFriendBtn;
    }
}