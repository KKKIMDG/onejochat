package view;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.function.Consumer;

import controller.LoginController;

public class HomeView extends JPanel {
    private JButton addFriendBtn;
    private DefaultListModel<String> friendListModel;
    private JList<String> friendList;
    private HashSet<String> friendSet = new HashSet<>();
    private JButton createRoomBtn;
    private JButton listRoomBtn;

    public HomeView(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 로고 및 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel logo = new JLabel("onejo");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(new Color(0x007BFF));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        addFriendBtn = new JButton("친구추가 +");
        addFriendBtn.setBackground(Color.WHITE);
        addFriendBtn.setForeground(new Color(0x007BFF));
        addFriendBtn.setBorderPainted(false);
        addFriendBtn.setFocusPainted(false);
        addFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

        topPanel.add(logo, BorderLayout.WEST);
        topPanel.add(addFriendBtn, BorderLayout.EAST);

        // 검색창
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

        // 친구목록 타이틀
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(Color.WHITE);

        JLabel friendLabel = new JLabel("👥 친구목록");
        friendLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        friendLabel.setForeground(Color.DARK_GRAY);
        labelPanel.add(friendLabel);

        // 친구목록 리스트
        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setFont(new Font("SansSerif", Font.BOLD, 25));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 하단 버튼들
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

        // 전체 조합
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

    public void addFriendToList(String friendLine) {
        String friendId = null;

        // 포맷: "FRIEND:123,mexaen"
        if (friendLine.startsWith("FRIEND:")) {
            String[] parts = friendLine.substring(7).split(",");
            if (parts.length == 2) {
                friendId = parts[0].equals(LoginController.getCurrentUserId()) ? parts[1] : parts[0];
            }
        }
        // 포맷: "mexaen"
        else {
            friendId = friendLine.trim();
        }

        if (friendId != null && !friendSet.contains(friendId)) {
            friendSet.add(friendId);
            friendListModel.addElement(friendId);
        }
    }

    public void refreshFriendListFromFile(String myId) {
        friendListModel.clear();
        friendSet.clear();

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

    // 🔹 카드 뷰 전환 핸들러 연결
    public void setViewChangeHandler(Consumer<String> handler) {
        createRoomBtn.addActionListener(e -> handler.accept("createChatRoomView"));
        listRoomBtn.addActionListener(e -> handler.accept("chatRoomListView"));
    }
}