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
                    String idToSearch = line.substring("SEARCH_ID:".length()).trim();
                    String name = searchUser(idToSearch);

                    if (name != null) {
                        out.write("FOUND:" + name + "\n");
                    } else {
                        out.write("NOT_FOUND\n");
                    }
                    out.flush();
                } else {
                    // 기존 Echo 처리
                    out.write("서버로부터 응답: " + line + "\n");
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("클라이언트 연결 종료: " + e.getMessage());
        }
    }

    private String searchUser(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            String foundId = "", foundName = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("name:")) {
                    foundName = line.substring(6).trim();
                } else if (line.startsWith("ID:")) {
                    foundId = line.substring(4).trim();
                    if (foundId.equals(id)) {
                        return foundName;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}