package view;

import javax.swing.*;
import java.awt.*;

public class ShowChatView extends JDialog {

    public ShowChatView(JFrame parent) {
        super(parent, "채팅방 목록", true);

        // 전체 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 검색창
        JTextField searchField = new JTextField("채팅방 목록 검색");
        searchField.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchField.setPreferredSize(new Dimension(400, 35));
        panel.add(searchField, BorderLayout.NORTH);

        // 채팅방 리스트 패널
        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(Color.WHITE);

        // 샘플 채팅방 1 - 동건
        JLabel label1 = new JLabel("동건");
        label1.setFont(new Font("SansSerif", Font.BOLD, 18));
        label1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        label1.setHorizontalAlignment(SwingConstants.LEFT);
        label1.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatListPanel.add(label1);
        chatListPanel.add(new JSeparator());

        // 샘플 채팅방 2 - 공부 채팅방
        JLabel label2 = new JLabel("공부 채팅방");
        label2.setFont(new Font("SansSerif", Font.BOLD, 18));
        label2.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        label2.setHorizontalAlignment(SwingConstants.LEFT);
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatListPanel.add(label2);
        chatListPanel.add(new JSeparator());

        // 스크롤 추가
        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 다이얼로그 설정
        setContentPane(panel);
        setSize(450, 600);
        setLocationRelativeTo(parent);
    }
}