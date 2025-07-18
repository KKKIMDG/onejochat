package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreateChatView extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final JTextField roomNameField;
    private final JList<String> friendList;
    private final DefaultListModel<String> friendListModel;
    private final DefaultListModel<String> invitedModel;
    private final JList<String> invitedList;
    private final JButton inviteBtn;
    private final JButton deleteBtn; // 삭제 버튼 추가
    private final JButton normalBtn;
    private final JButton secretBtn;

    public CreateChatView(CardLayout cardLayout, JPanel mainPanel, List<String> friends) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        JLabel iconLabel = new JLabel("💬");
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
        JLabel titleLabel = new JLabel("채팅방 만들기");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x007BFF));

        topPanel.add(iconLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(topPanel, BorderLayout.NORTH);

        // 중앙
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // 채팅방 이름 입력
        roomNameField = new JTextField();
        roomNameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        roomNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        roomNameField.setBorder(BorderFactory.createTitledBorder("채팅방 이름 입력:"));
        centerPanel.add(roomNameField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 친구 리스트
        friendListModel = new DefaultListModel<>();
        for (String f : friends) friendListModel.addElement(f);
        friendList = new JList<>(friendListModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JScrollPane friendPane = new JScrollPane(friendList);
        friendPane.setBorder(BorderFactory.createTitledBorder("친구 목록"));
        centerPanel.add(friendPane);

        // 초대 버튼
        inviteBtn = new JButton("➕ 초대");
        inviteBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        inviteBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel inviteBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        inviteBtnPanel.setBackground(Color.WHITE);
        inviteBtnPanel.add(inviteBtn);
        centerPanel.add(inviteBtnPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 초대한 친구 리스트
        invitedModel = new DefaultListModel<>();
        invitedList = new JList<>(invitedModel);
        invitedList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JScrollPane invitedPane = new JScrollPane(invitedList);
        invitedPane.setBorder(BorderFactory.createTitledBorder("초대한 친구"));
        centerPanel.add(invitedPane);

        // 삭제 버튼
        deleteBtn = new JButton("❌ 삭제");
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        deleteBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel deleteBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        deleteBtnPanel.setBackground(Color.WHITE);
        deleteBtnPanel.add(deleteBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(deleteBtnPanel);

        centerPanel.add(Box.createVerticalGlue());
        add(centerPanel, BorderLayout.CENTER);

        // 하단 버튼
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        bottomPanel.setBackground(Color.WHITE);
        secretBtn = new JButton("💬 비밀채팅");
        secretBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        secretBtn.setForeground(new Color(0x007BFF));
        secretBtn.setBackground(Color.WHITE);
        normalBtn = new JButton("💬 일반채팅");
        normalBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        normalBtn.setForeground(new Color(0x007BFF));
        normalBtn.setBackground(Color.WHITE);
        bottomPanel.add(secretBtn);
        bottomPanel.add(normalBtn);

        // 뒤로가기 버튼
        JButton backBtn = new JButton("뒤로가기");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        backBtn.setForeground(new Color(0x6C757D));
        backBtn.setBackground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "homeView"));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(bottomPanel, BorderLayout.CENTER);
        buttonPanel.add(backBtn, BorderLayout.SOUTH);

        add(buttonPanel, BorderLayout.SOUTH);

        // 초대 버튼 기능: 선택된 친구를 초대 목록에 추가
        inviteBtn.addActionListener(e -> {
            String selected = friendList.getSelectedValue();
            if (selected != null && !invitedModel.contains(selected)) {
                invitedModel.addElement(selected);
                friendListModel.removeElement(selected);
            }
        });

        // 삭제 버튼 기능: 초대한 친구 목록에서 제거 후 친구목록으로 복귀
        deleteBtn.addActionListener(e -> {
            String selected = invitedList.getSelectedValue();
            if (selected != null) {
                invitedModel.removeElement(selected);
                friendListModel.addElement(selected);
            }
        });
    }

    // 외부에서 버튼 접근할 수 있도록 getter 제공
    public JButton getInviteBtn() { return inviteBtn; }
    public JButton getDeleteBtn() { return deleteBtn; }
    public JList<String> getFriendList() { return friendList; }
    public DefaultListModel<String> getFriendListModel() { return friendListModel; }
    public DefaultListModel<String> getInvitedModel() { return invitedModel; }
    public JList<String> getInvitedList() { return invitedList; }
    public JTextField getRoomNameField() { return roomNameField; }
    public JButton getNormalButton() { return normalBtn; }
    public JButton getSecretButton() { return secretBtn; }
}