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

    // ✅ 요청 올 때마다 파일에서 사용자 검색
    private String searchUser(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            String foundId = "", foundName = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    foundId = line.substring(3).trim();
                } else if (line.startsWith("name:")) {
                    foundName = line.substring(5).trim();
                } else if (line.startsWith("---")) {
                    // 한 명 끝났을 때 체크
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
}