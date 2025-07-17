package controller;

import view.FriendAddView;
import view.HomeView;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
//
/**
 * 친구 추가 컨트롤러 클래스
 * 친구 추가 뷰와 서버 간의 상호작용을 관리하는 컨트롤러입니다.
 */
public class FriendAddController {
    /** 친구 추가 뷰 참조 */
    private final FriendAddView view;
    /** 서버와의 소켓 연결 */
    private final Socket socket;
    /** 현재 로그인한 사용자 ID */
    private final String currentUserId;
    /** 홈 뷰 참조 */
    private final HomeView homeView;

    /** 서버로부터 데이터를 읽기 위한 BufferedReader */
    private BufferedReader in;
    /** 서버로 데이터를 보내기 위한 BufferedWriter */
    private BufferedWriter out;

    /**
     * 친구 추가 컨트롤러 생성자
     * 
     * @param view 친구 추가 뷰
     * @param socket 서버와의 소켓 연결
     * @param currentUserId 현재 사용자 ID
     * @param homeView 홈 뷰
     */
    public FriendAddController(FriendAddView view, Socket socket, String currentUserId, HomeView homeView) {
        this.view = view;
        this.socket = socket;
        this.currentUserId = currentUserId;
        this.homeView = homeView;

        try {
            // 소켓 입출력 스트림 초기화
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ 소켓 초기화 실패");
        }

        // 검색 버튼 이벤트 리스너
        view.getSearchButton().addActionListener(e -> handleSearch());

        // 친구 추가 버튼 이벤트 리스너
        view.getRequestButton().addActionListener(e -> {
            String searchedName = view.getResultText();
            String toId = view.getInputId();

            // 검색 결과가 성공인 경우에만 친구 추가 진행
            if (searchedName.startsWith("✅")) {
                try {
                    // 사용자별 친구 목록 파일 생성
                    File file = new File("friends_" + currentUserId + ".txt");

                    // 중복 확인 - 이미 친구인지 체크
                    boolean alreadyAdded = false;
                    if (file.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().equals(toId)) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        reader.close();
                    }

                    // 친구 추가 처리
                    if (!alreadyAdded) {
                        // 친구 목록 파일에 새 친구 추가
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                        writer.write(toId + System.lineSeparator());
                        writer.close();

                        // 홈 뷰의 친구 목록 새로고침
                        if (homeView != null) {
                            homeView.refreshFriendListFromFile(currentUserId);
                        }

                        JOptionPane.showMessageDialog(view, "🎉 친구가 추가되었습니다!");
                        view.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(view, "⚠️ 이미 친구입니다.");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view, "❌ 친구 추가 중 오류 발생");
                }
            } else {
                JOptionPane.showMessageDialog(view, "❗ 먼저 친구를 검색하세요.");
            }
        });
    }

    /**
     * ID 검색 기능을 처리합니다.
     * 서버에 사용자 검색 요청을 보내고 결과를 표시합니다.
     */
    private void handleSearch() {
        String inputId = view.getInputId();

        // ID 입력 검증
        if (inputId.isEmpty()) {
            view.setResultText("❗ 아이디를 입력해주세요.");
            return;
        }

        try {
            // 서버에 ID 검색 요청 전송
            out.write("SEARCH_ID:" + inputId + "\n");
            out.flush();

            // 서버 응답 수신
            String response = in.readLine();

            // 응답이 없는 경우 처리
            if (response == null) {
                view.setResultText("❌ 서버 응답 없음");
                return;
            }

            // 검색 결과에 따른 처리
            if (response.startsWith("FOUND:")) {
                String name = response.substring(6).trim();
                view.setResultText("✅ 이름: " + name);
            } else {
                view.setResultText("❌ 사용자를 찾을 수 없습니다.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            view.setResultText("❌ 서버 통신 오류");
        }
    }
}