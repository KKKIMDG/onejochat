package controller;

import view.FriendAddView;
import view.HomeView;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class FriendAddController {
    private final FriendAddView view;
    private final Socket socket;
    private final String currentUserId;
    private final HomeView homeView;

    private BufferedReader in;
    private BufferedWriter out;

    public FriendAddController(FriendAddView view, Socket socket, String currentUserId, HomeView homeView) {
        this.view = view;
        this.socket = socket;
        this.currentUserId = currentUserId;
        this.homeView = homeView;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ 소켓 초기화 실패");
        }

        // 🔍 검색 버튼 이벤트
        view.getSearchButton().addActionListener(e -> handleSearch());

        // ➕ 친구 추가 버튼 이벤트
        view.getRequestButton().addActionListener(e -> {
            String searchedName = view.getResultText();
            String toId = view.getInputId();

            if (searchedName.startsWith("✅")) {
                try {
                    File file = new File("friends_" + currentUserId + ".txt");

                    // 중복 확인
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

                    // 친구 추가
                    if (!alreadyAdded) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                        writer.write(toId + System.lineSeparator());
                        writer.close();

                        // ✅ 목록 리프레시
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

    // 🔍 ID 검색 기능
    private void handleSearch() {
        String inputId = view.getInputId();

        if (inputId.isEmpty()) {
            view.setResultText("❗ 아이디를 입력해주세요.");
            return;
        }

        try {
            out.write("SEARCH_ID:" + inputId + "\n");
            out.flush();

            String response = in.readLine();

            if (response == null) {
                view.setResultText("❌ 서버 응답 없음");
                return;
            }

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