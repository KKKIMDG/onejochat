package controller;

import view.FriendAddView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FriendAddController {
    private final FriendAddView view;

    public FriendAddController(FriendAddView view, Socket socket) {
        this.view = view;

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
                JOptionPane.showMessageDialog(view, "📨 집규 요청을 보내왈습니다!");
                view.setVisible(false); // 다이여러그 닫기
            } else {
                JOptionPane.showMessageDialog(view, "❗ 먼저 집규를 검색하세요.");
            }
        });
    }

    private void handleSearch() {
        String inputId = view.getInputId();

        if (inputId.isEmpty()) {
            view.setResultText("❗ 아이디를 입력해주세요.");
            return;
        }

        try (
                Socket socket = new Socket("localhost", 9001);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
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
