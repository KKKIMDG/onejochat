package controller;

import view.SignupView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SignUpController {
    private SignupView signupView;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public SignUpController(SignupView signupView, JPanel mainPanel, CardLayout cardLayout) {
        this.signupView = signupView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // ✅ 완료 버튼 이벤트
        signupView.getSignupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = signupView.getNameInput();
                String id = signupView.getIdInput();
                String pw = signupView.getPasswordInput();
                String pw2 = signupView.getConfirmPasswordInput();

                if (!pw.equals(pw2)) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다!");
                    return;
                }

                saveToFile("user_data.txt", name, id, pw);
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                cardLayout.show(mainPanel, "loginView");  // 로그인 화면으로 전환
            }
        });

        // ✅ 취소 버튼 이벤트
        signupView.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "loginView"); // 로그인 화면으로 돌아가기
            }
        });
        signupView.getCheckButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputId = signupView.getUserId();
                if (inputId.isEmpty()) {
                    JOptionPane.showMessageDialog(signupView, "ID를 입력해주세요.");
                    return;
                }

                boolean isDuplicate = isDuplicateId("user_data.txt", inputId);
                if (isDuplicate) {
                    JOptionPane.showMessageDialog(signupView, "이미 존재하는 ID입니다.");
                } else {
                    JOptionPane.showMessageDialog(signupView, "사용 가능한 ID입니다.");
                }
            }
        });
    }
    // 화원가입 완료시 파일 안에 저장.
    private void saveToFile(String filename, String name, String id, String pw) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) { // append = true
            writer.write("name: " + name + "\n");
            writer.write("ID: " + id + "\n");
            writer.write("PW: " + pw + "\n");
            writer.write("--------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isDuplicateId(String filename, String inputId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID: ")) {
                    String savedId = line.substring(4).trim(); // "ID: " 이후 문자열
                    if (savedId.equals(inputId)) {
                        return true; // 중복 ID 발견
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // 중복 없음
    }
}