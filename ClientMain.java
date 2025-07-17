import view.MainFrame;

import javax.swing.*;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9002); // 서버 연결
            System.out.println("서버 연결 성공");

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new MainFrame(socket); // 소켓 전달
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.");
            e.printStackTrace();
        }
    }
}