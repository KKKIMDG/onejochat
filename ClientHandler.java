import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("클라이언트로부터: " + line);

                if (line.startsWith("SEARCH_ID:")) {
                    String searchId = line.substring("SEARCH_ID:".length()).trim();
                    String result = searchUser(searchId);
                    if (result != null) {
                        out.write("FOUND:" + result + "\n");
                    } else {
                        out.write("NOT_FOUND\n");
                    }
                    out.flush();

                } else if (line.startsWith("SIGNUP:")) {
                    // ✅ 회원가입 요청 처리
                    boolean success = handleSignup(line.substring("SIGNUP:".length()).trim());
                    if (success) {
                        out.write("SIGNUP_SUCCESS\n");
                    } else {
                        out.write("SIGNUP_FAIL\n");
                    }
                    out.flush();

                } else {
                    // 기본 응답
                    out.write("서버로부터 응답: " + line + "\n");
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("클라이언트 연결 종료: " + e.getMessage());
        }
    }

    // ✅ 사용자 검색 기능
    private String searchUser(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            String foundId = "", foundName = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    foundId = line.substring(3).trim();
                } else if (line.startsWith("NAME:")) {
                    foundName = line.substring(5).trim();
                } else if (line.startsWith("---")) {
                    if (foundId.equals(id)) {
                        return foundName;
                    }
                    foundId = "";
                    foundName = "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ 회원가입 요청을 파일에 저장
    private boolean handleSignup(String data) {
        try {
            // 예: ID=kim123,PW=1234,NAME=김다빈
            String[] parts = data.split(",");
            String id = "", pw = "", name = "";

            for (String part : parts) {
                if (part.startsWith("ID=")) {
                    id = part.substring(3);
                } else if (part.startsWith("PW=")) {
                    pw = part.substring(3);
                } else if (part.startsWith("NAME=")) {
                    name = part.substring(5);
                }
            }

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                return false; // 필수 항목 누락
            }

            // 파일에 추가 모드로 기록
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.txt", true))) {
                writer.write("ID: " + id + "\n");
                writer.write("PW: " + pw + "\n");
                writer.write("NAME: " + name + "\n");
                writer.write("---\n");
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}