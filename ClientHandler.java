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
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String request = reader.readLine();
            System.out.println("📩 클라이언트 요청: " + request);

            if (request == null) return;

            if (request.startsWith("LOGIN:")) handleLogin(request, writer);
            else if (request.startsWith("SIGNUP:")) handleSignup(request, writer);
            else if (request.startsWith("CHECK_ID:")) handleCheckId(request, writer);
            else if (request.startsWith("SEARCH_ID:")) handleSearchId(request, writer);
            else if (request.startsWith("FRIEND_REQUEST:")) handleFriendRequest(request, writer);
            else if (request.startsWith("GET_REQUESTS:")) handleGetRequests(request, writer);
            else {
                writer.write("UNKNOWN_COMMAND\n");
                writer.flush();
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔐 로그인 처리
    private void handleLogin(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("LOGIN:".length()).split(",");
        String id = "", pw = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
        }

        writer.write(checkLogin(id, pw) ? "LOGIN_SUCCESS\n" : "LOGIN_FAIL\n");
        writer.flush();
    }

    // 📝 회원가입 처리
    private void handleSignup(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("SIGNUP:".length()).split(",");
        String id = "", pw = "", name = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
            else if (part.startsWith("NAME=")) name = part.substring(5);
        }

        writer.write(addUserToFile(id, pw, name) ? "SIGNUP_SUCCESS\n" : "SIGNUP_FAIL\n");
        writer.flush();
    }

    // 🆔 ID 중복 확인
    private void handleCheckId(String request, BufferedWriter writer) throws IOException {
        String id = request.substring("CHECK_ID:".length()).trim();
        writer.write(isDuplicateId(id) ? "ID_DUPLICATE\n" : "ID_AVAILABLE\n");
        writer.flush();
    }

    // 🔍 사용자 검색
    private void handleSearchId(String request, BufferedWriter writer) throws IOException {
        String searchId = request.substring("SEARCH_ID:".length()).trim();
        String name = findNameById(searchId);
        writer.write(name != null ? "FOUND:" + name + "\n" : "NOT_FOUND\n");
        writer.flush();
    }

    // 📨 친구 요청 처리
    private void handleFriendRequest(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("FRIEND_REQUEST:".length()).split(",");
        String fromId = "", toId = "";
        for (String part : parts) {
            if (part.startsWith("FROM=")) fromId = part.substring(5);
            else if (part.startsWith("TO=")) toId = part.substring(3);
        }

        if (fromId.isEmpty() || toId.isEmpty()) {
            writer.write("REQUEST_FAIL\n");
        } else {
            try (BufferedWriter fw = new BufferedWriter(new FileWriter("friend_requests.txt", true))) {
                fw.write("FROM: " + fromId + "\n");
                fw.write("TO: " + toId + "\n");
                fw.write("--------------------\n");
                writer.write("REQUEST_SENT\n");
            } catch (IOException e) {
                writer.write("REQUEST_FAIL\n");
            }
        }
        writer.flush();
    }

    // 📥 친구 요청 확인
    private void handleGetRequests(String request, BufferedWriter writer) throws IOException {
        String targetId = request.substring("GET_REQUESTS:ID=".length()).trim();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("friend_requests.txt"))) {
            String line;
            String from = null, to = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("FROM:")) from = line.substring(5).trim();
                else if (line.startsWith("TO:")) to = line.substring(3).trim();
                else if (line.startsWith("---")) {
                    if (to != null && to.equals(targetId) && from != null) {
                        sb.append(from).append(",");
                    }
                    from = null;
                    to = null;
                }
            }
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);  // 마지막 콤마 제거
        writer.write("REQUEST_LIST:" + sb.toString() + "\n");
        writer.flush();
    }

    // 로그인 검사
    private boolean checkLogin(String id, String pw) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundPw = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.startsWith("ID:")) foundId = line.substring(3).trim();
                else if (line.startsWith("PW:")) foundPw = line.substring(3).trim();
                else if (line.startsWith("---")) {
                    if (id.equals(foundId) && pw.equals(foundPw)) return true;
                    foundId = null;
                    foundPw = null;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    // 회원 데이터 추가
    private boolean addUserToFile(String id, String pw, String name) {
        if (isDuplicateId(id)) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.txt", true))) {
            writer.write("ID: " + id + "\n");
            writer.write("PW: " + pw + "\n");
            writer.write("NAME: " + name + "\n");
            writer.write("--------------------\n");
            return true;
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    // ID 중복 확인
    private boolean isDuplicateId(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:") && line.substring(3).trim().equals(id)) return true;
            }
        } catch (IOException e) { return false; }
        return false;
    }

    // 이름 검색
    private String findNameById(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundName = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) foundId = line.substring(3).trim();
                else if (line.startsWith("NAME:")) foundName = line.substring(5).trim();
                else if (line.startsWith("---")) {
                    if (id.equals(foundId)) return foundName;
                    foundId = null;
                    foundName = null;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}