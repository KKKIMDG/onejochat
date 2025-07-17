package controller;

import view.SignupView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class SignUpController {
    private SignupView signupView;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public SignUpController(SignupView signupView, JPanel mainPanel, CardLayout cardLayout) {
        this.signupView = signupView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // ✅ 완료 버튼 이벤트
        signupView.getSignupButton().addActionListener(e -> {
            String name = signupView.getNameInput();
            String id = signupView.getIdInput();
            String pw = signupView.getPasswordInput();
            String pw2 = signupView.getConfirmPasswordInput();

            if (!pw.equals(pw2)) {
                JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다!");
                return;
            }

            boolean success = sendSignupRequestToServer(id, pw, name);
            if (success) {
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                cardLayout.show(mainPanel, "loginView");  // 로그인 화면으로 전환
            } else {
                JOptionPane.showMessageDialog(null, "회원가입 실패! 서버에 문제가 있습니다.");
            }
        });

        // ✅ 취소 버튼 이벤트
        signupView.getCancelButton().addActionListener(e -> {
            cardLayout.show(mainPanel, "loginView"); // 로그인 화면으로 돌아가기
        });

        // ✅ 중복확인 버튼 이벤트
        signupView.getCheckButton().addActionListener(e -> {
            String inputId = signupView.getUserId();
            if (inputId.isEmpty()) {
                JOptionPane.showMessageDialog(signupView, "ID를 입력해주세요.");
                return;
            }

            boolean isDuplicate = sendCheckIdRequestToServer(inputId);
            if (isDuplicate) {
                JOptionPane.showMessageDialog(signupView, "이미 존재하는 ID입니다.");
            } else {
                JOptionPane.showMessageDialog(signupView, "사용 가능한 ID입니다.");
            }
        });
    }

    // 서버에 회원가입 요청 보내기
    private boolean sendSignupRequestToServer(String id, String pw, String name) {
        try (Socket socket = new Socket("localhost", 9001);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String signupData = String.format("SIGNUP:ID=%s,PW=%s,NAME=%s\n", id, pw, name);
            writer.write(signupData);
            writer.flush();

            String response = reader.readLine();
            return "SIGNUP_SUCCESS".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 서버에 ID 중복 체크 요청 보내기
    private boolean sendCheckIdRequestToServer(String id) {
        try (Socket socket = new Socket("localhost", 9002);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String checkData = "CHECK_ID:" + id + "\n";
            writer.write(checkData);
            writer.flush();

            String response = reader.readLine();
            return "ID_DUPLICATE".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}