package controller;

import view.SignupView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
//
/**
 * 회원가입 컨트롤러 클래스
 * 회원가입 뷰와 서버 간의 상호작용을 관리하는 컨트롤러입니다.
 */
public class SignUpController {
    /** 회원가입 뷰 참조 */
    private SignupView signupView;
    /** 메인 패널 참조 */
    private JPanel mainPanel;
    /** 카드 레이아웃 참조 */
    private CardLayout cardLayout;

    /**
     * 회원가입 컨트롤러 생성자
     * 
     * @param signupView 회원가입 뷰
     * @param mainPanel 메인 패널
     * @param cardLayout 카드 레이아웃
     */
    public SignUpController(SignupView signupView, JPanel mainPanel, CardLayout cardLayout) {
        this.signupView = signupView;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // 회원가입 완료 버튼 이벤트 리스너
        signupView.getSignupButton().addActionListener(e -> {
            // 사용자 입력 데이터 가져오기
            String name = signupView.getNameInput();
            String id = signupView.getIdInput();
            String pw = signupView.getPasswordInput();
            String pw2 = signupView.getConfirmPasswordInput();

            // 비밀번호 확인 검증
            if (!pw.equals(pw2)) {
                JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다!");
                return;
            }

            // 서버에 회원가입 요청 전송
            boolean success = sendSignupRequestToServer(id, pw, name);
            if (success) {
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                cardLayout.show(mainPanel, "loginView");  // 로그인 화면으로 전환
            } else {
                JOptionPane.showMessageDialog(null, "회원가입 실패! 서버에 문제가 있습니다.");
            }
        });

        // 취소 버튼 이벤트 리스너
        signupView.getCancelButton().addActionListener(e -> {
            cardLayout.show(mainPanel, "loginView"); // 로그인 화면으로 돌아가기
        });

        // ID 중복확인 버튼 이벤트 리스너
        signupView.getCheckButton().addActionListener(e -> {
            String inputId = signupView.getUserId();
            // ID 입력 검증
            if (inputId.isEmpty()) {
                JOptionPane.showMessageDialog(signupView, "ID를 입력해주세요.");
                return;
            }

            // 서버에 ID 중복 확인 요청
            boolean isDuplicate = sendCheckIdRequestToServer(inputId);
            if (isDuplicate) {
                JOptionPane.showMessageDialog(signupView, "이미 존재하는 ID입니다.");
            } else {
                JOptionPane.showMessageDialog(signupView, "사용 가능한 ID입니다.");
            }
        });
    }

    /**
     * 서버에 회원가입 요청을 보냅니다.
     * 
     * @param id 사용자 ID
     * @param pw 사용자 비밀번호
     * @param name 사용자 이름
     * @return 회원가입 성공 여부
     */
    private boolean sendSignupRequestToServer(String id, String pw, String name) {
        try (Socket socket = new Socket("localhost", 9001);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // 회원가입 요청 메시지 생성 및 전송
            String signupData = String.format("SIGNUP:ID=%s,PW=%s,NAME=%s\n", id, pw, name);
            writer.write(signupData);
            writer.flush();

            // 서버 응답 수신 및 처리
            String response = reader.readLine();
            return "SIGNUP_SUCCESS".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 서버에 ID 중복 확인 요청을 보냅니다.
     * 
     * @param id 확인할 사용자 ID
     * @return ID 중복 여부 (true: 중복, false: 사용 가능)
     */
    private boolean sendCheckIdRequestToServer(String id) {
        try (Socket socket = new Socket("localhost", 9002);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // ID 중복 확인 요청 메시지 생성 및 전송
            String checkData = "CHECK_ID:" + id + "\n";
            writer.write(checkData);
            writer.flush();

            // 서버 응답 수신 및 처리
            String response = reader.readLine();
            return "ID_DUPLICATE".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}