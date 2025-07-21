package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private JButton deleteFriendBtn;
    private JButton editFriendBtn;
    private boolean isEditMode = false;
    /** 전체 친구 목록(검색용) */
    private List<String> allFriends = new ArrayList<>();
    /** 친구 ID → 별명 매핑 */
    private Map<String, String> nicknameMap = new java.util.HashMap<>();
    // private JButton logoutBtn; // 로그아웃 버튼 추가

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

        // 로그아웃 버튼 생성 (제거)
        // logoutBtn = new JButton("로그아웃");
        // logoutBtn.setBackground(Color.WHITE);
        // logoutBtn.setForeground(Color.GRAY);
        // logoutBtn.setBorderPainted(false);
        // logoutBtn.setFocusPainted(false);
        // logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // 친구 삭제 버튼 생성 (순서 이동)
        deleteFriendBtn = new JButton("삭제");
        deleteFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        deleteFriendBtn.setBackground(Color.WHITE);
        deleteFriendBtn.setForeground(Color.RED);
        deleteFriendBtn.setEnabled(false);
        deleteFriendBtn.addActionListener(e -> {
            String selected = friendList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, selected + "님을 친구 목록에서 삭제할까요?", "친구 삭제", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String myId = LoginController.getCurrentUserId();
                    File file = new File("friends_" + myId + ".txt");
                    List<String> lines = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().equals(selected)) lines.add(line);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                        for (String l : lines) writer.write(l + System.lineSeparator());
                    } catch (Exception ex) { ex.printStackTrace(); }
                    refreshFriendListFromFile(myId);
                    JOptionPane.showMessageDialog(this, selected + "님이 삭제되었습니다.");
                }
            }
        });

        // 친구 편집 버튼 생성
        editFriendBtn = new JButton("친구 편집");
        editFriendBtn.setBackground(Color.WHITE);
        editFriendBtn.setForeground(new Color(0x007BFF));
        editFriendBtn.setBorderPainted(false);
        editFriendBtn.setFocusPainted(false);
        editFriendBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        editFriendBtn.addActionListener(e -> {
            isEditMode = !isEditMode;
            deleteFriendBtn.setVisible(isEditMode && friendList.getSelectedValue() != null);
            if (isEditMode) {
                editFriendBtn.setText("편집 완료");
            } else {
                editFriendBtn.setText("친구 편집");
            }
        });

        // 상단 패널에 컴포넌트 추가
        topPanel.add(logo, BorderLayout.WEST);
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBtnPanel.setBackground(Color.WHITE);
        rightBtnPanel.add(addFriendBtn);
        // rightBtnPanel.add(logoutBtn); // 로그아웃 버튼 제거
        rightBtnPanel.add(deleteFriendBtn);
        topPanel.add(rightBtnPanel, BorderLayout.EAST);

        // 검색 패널 생성
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // 검색 아이콘 생성
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // 검색 필드 생성
        JTextField searchField = new JTextField();
        final String SEARCH_HINT = "Search messages, people";
        searchField.setText(SEARCH_HINT);
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // placeholder처럼 동작하게 포커스 이벤트 추가
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(SEARCH_HINT)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(SEARCH_HINT);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // 검색 필드 DocumentListener 추가
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filterFriends() {
                String keyword = searchField.getText().trim().toLowerCase();
                friendListModel.clear();
                if (keyword.isEmpty() || keyword.equals(SEARCH_HINT.toLowerCase())) {
                    for (String f : allFriends) friendListModel.addElement(f);
                } else {
                    for (String f : allFriends) {
                        if (f.toLowerCase().contains(keyword)) {
                            friendListModel.addElement(f);
                        }
                    }
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { filterFriends(); }
            @Override public void removeUpdate(DocumentEvent e) { filterFriends(); }
            @Override public void changedUpdate(DocumentEvent e) { filterFriends(); }
        });

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
        friendList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String id = value.toString();
                String nickname = nicknameMap.get(id);
                if (nickname != null && !nickname.isEmpty()) {
                    label.setText(nickname + " (" + id + ")");
                } else {
                    label.setText(id);
                }
                label.setFont(new Font("SansSerif", Font.BOLD, 25));
                return label;
            }
        });
        // 친구 선택 시 삭제 버튼 활성화
        friendList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && friendList.getSelectedValue() != null) {
                deleteFriendBtn.setEnabled(true);
            } else {
                deleteFriendBtn.setEnabled(false);
            }
        });

        // 친구 이름 변경(별명) 기능: 우클릭 메뉴
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("이름 변경");
        popupMenu.add(renameItem);
        friendList.setComponentPopupMenu(popupMenu);
        friendList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    int row = friendList.locationToIndex(e.getPoint());
                    friendList.setSelectedIndex(row);
                }
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    int row = friendList.locationToIndex(e.getPoint());
                    friendList.setSelectedIndex(row);
                }
            }
        });
        renameItem.addActionListener(ev -> {
            String selected = friendList.getSelectedValue();
            if (selected != null) {
                String current = nicknameMap.getOrDefault(selected, "");
                String newName = JOptionPane.showInputDialog(this, "새 이름(별명)을 입력하세요:", current);
                if (newName != null) {
                    if (newName.trim().isEmpty()) {
                        nicknameMap.remove(selected);
                    } else {
                        nicknameMap.put(selected, newName.trim());
                    }
                    saveNicknamesToFile();
                    friendList.repaint();
                }
            }
        });

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
        // 삭제 버튼을 친구목록 우측에 배치하는 패널 제거

        // 별명 파일 불러오기
        loadNicknamesFromFile();
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
            allFriends.add(friendId); // 전체 목록에도 추가
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
        allFriends.clear(); // 전체 목록도 초기화

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
     * 친구 목록을 모두 초기화합니다.
     */
    public void clearFriendList() {
        friendListModel.clear();
        friendSet.clear();
        allFriends.clear();
    }

    public JButton getCreateRoomButton() { return createRoomBtn; }
    public JButton getListRoomButton() { return listRoomBtn; }
    // public JButton getLogoutButton() { return logoutBtn; } // getter도 삭제

    /** 별명 파일 저장 */
    private void saveNicknamesToFile() {
        String myId = LoginController.getCurrentUserId();
        if (myId == null) return;
        try (BufferedWriter w = new BufferedWriter(new FileWriter("friend_nicknames_" + myId + ".txt"))) {
            for (var entry : nicknameMap.entrySet()) {
                w.write(entry.getKey() + "=" + entry.getValue() + System.lineSeparator());
            }
        } catch (Exception e) { /* 무시 */ }
    }
    /** 별명 파일 불러오기 */
    private void loadNicknamesFromFile() {
        String myId = LoginController.getCurrentUserId();
        if (myId == null) return;
        nicknameMap.clear();
        File file = new File("friend_nicknames_" + myId + ".txt");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String id = line.substring(0, idx);
                    String nick = line.substring(idx + 1);
                    nicknameMap.put(id, nick);
                }
            }
        } catch (Exception e) { /* 무시 */ }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadNicknamesFromFile();
        friendList.repaint();
    }
}