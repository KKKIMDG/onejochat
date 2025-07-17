package KDT.onejochat.view;

import javax.swing.*;
import java.awt.*;

/**
 * 친구 추가 뷰 클래스
 * 친구 추가를 위한 다이얼로그 창을 제공하는 클래스입니다.
 */
public class FriendAddView extends JDialog {
    /** ID 입력 필드 */
    private JTextField idField;
    /** 검색 결과 표시 라벨 */
    private JLabel resultLabel;
    /** 친구 요청 버튼 */
    private JButton requestButton;
    /** 검색 버튼 */
    private JButton searchButton;

    /**
     * 친구 추가 뷰 생성자
     * 
     * @param parent 부모 프레임
     */
    public FriendAddView(JFrame parent) {
        super(parent, "친구 추가", true);
        // 다이얼로그 기본 설정
        setSize(350, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 상단 패널 생성 (아이콘과 제목)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // 아이콘 라벨 생성
        JLabel iconLabel = new JLabel("💬");
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
        topPanel.add(iconLabel);

        // 제목 라벨 생성
        JLabel titleLabel = new JLabel("친구 추가");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x007BFF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 입력 영역 패널 생성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // ID 입력 필드 생성
        idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        idField.setPreferredSize(new Dimension(200, 35));
        idField.setMaximumSize(new Dimension(300, 35));
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);
        idField.setBorder(BorderFactory.createTitledBorder("추가할 친구의 ID 입력:"));
        centerPanel.add(idField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 검색 버튼 생성
        searchButton = new JButton("🔍 검색");
        searchButton.setForeground(new Color(0x007BFF));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setBackground(Color.WHITE);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(searchButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 검색 결과 라벨 생성
        resultLabel = new JLabel("검색 결과:");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(resultLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 친구 요청 버튼 생성
        requestButton = new JButton("친구 요청");
        requestButton.setVisible(true);
        requestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestButton.setBackground(Color.WHITE);
        requestButton.setForeground(new Color(0x007BFF));
        requestButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        requestButton.setFocusPainted(false);
        requestButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x007BFF), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        centerPanel.add(requestButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 검색 버튼을 반환합니다.
     * 
     * @return 검색 버튼
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * 친구 요청 버튼을 반환합니다.
     * 
     * @return 친구 요청 버튼
     */
    public JButton getRequestButton() {
        return requestButton;
    }

    /**
     * 입력된 ID를 반환합니다 (공백 제거).
     * 
     * @return 입력된 ID
     */
    public String getInputId() {
        return idField.getText().trim();
    }

    /**
     * 검색 결과 텍스트를 설정합니다.
     * 
     * @param text 설정할 텍스트
     */
    public void setResultText(String text) {
        resultLabel.setText(text);
    }

    /**
     * 검색 결과 텍스트를 반환합니다.
     * 
     * @return 검색 결과 텍스트
     */
    public String getResultText() {
        return resultLabel.getText();
    }
}