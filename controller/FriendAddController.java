package controller;

import view.FriendAddView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FriendAddController {
    private final FriendAddView view;
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public FriendAddController(FriendAddView view, Socket socket) {
        this.view = view;
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ 소켓 초기화 실패");
        }

        // 🔍 검색 버튼 액션
        view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        // 📨 친구 요청 버튼 액션 (추후 구현)
        view.getRequestButton().addActionListener(e -> {
            String searchedName = view.getResultText();
            if (searchedName.startsWith("✅")) {
                JOptionPane.showMessageDialog(view, "📨 친구 요청을 보냈습니다!");
                view.setVisible(false); // 다이얼로그 닫기
            } else {
                JOptionPane.showMessageDialog(view, "❗ 먼저 친구를 검색하세요.");
            }
        });
    }

    private void handleSearch() {
        String inputId = view.getInputId();

        if (inputId.isEmpty()) {
            view.setResultText("❗ 아이디를 입력해주세요.");
            return;
        }

        try {
            // 서버로 전송
            out.write("SEARCH_ID:" + inputId + "\n");
            out.flush();

            // 응답 받기
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