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
            String request;

            while ((request = reader.readLine()) != null) {
                System.out.println("📩 클라이언트 요청: " + request);

                if (request.startsWith("LOGIN:")) {
                    handleLogin(request, writer);
                } else if (request.startsWith("SIGNUP:")) {
                    handleSignup(request, writer);
                } else if (request.startsWith("CHECK_ID:")) {
                    handleCheckId(request, writer);
                } else if (request.startsWith("SEARCH_ID:")) {
                    handleSearchId(request, writer);
                } else if (request.startsWith("REQUEST_FRIEND:")) {
                    handleFriendRequest(request, writer);
                } else if (request.startsWith("QUIT")) {
                    System.out.println("클라이언트 연결 종료 요청");
                    break;
                } else {
                    writer.write("UNKNOWN_COMMAND\n");
                    writer.flush();
                }
            }

            socket.close();
            System.out.println("🔌 클라이언트와 연결 종료됨");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("LOGIN:".length()).split(",");
        String id = "", pw = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
        }

        if (checkLogin(id, pw)) {
            writer.write("LOGIN_SUCCESS\n");
        } else {
            writer.write("LOGIN_FAIL\n");
        }
        writer.flush();
    }

    private void handleSignup(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("SIGNUP:".length()).split(",");
        String id = "", pw = "", name = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
            else if (part.startsWith("NAME=")) name = part.substring(5);
        }

        if (addUserToFile(id, pw, name)) {
            writer.write("SIGNUP_SUCCESS\n");
        } else {
            writer.write("SIGNUP_FAIL\n");
        }
        writer.flush();
    }

    private void handleCheckId(String request, BufferedWriter writer) throws IOException {
        String id = request.substring("CHECK_ID:".length()).trim();
        if (isDuplicateId(id)) {
            writer.write("ID_DUPLICATE\n");
        } else {
            writer.write("ID_AVAILABLE\n");
        }
        writer.flush();
    }

    private void handleSearchId(String request, BufferedWriter writer) throws IOException {
        String searchId = request.substring("SEARCH_ID:".length()).trim();
        String name = findNameById(searchId);
        if (name != null) {
            writer.write("FOUND:" + name + "\n");
        } else {
            writer.write("NOT_FOUND\n");
        }
        writer.flush();
    }

    private void handleFriendRequest(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("REQUEST_FRIEND:".length()).split(",");
        String fromId = "", toId = "";
        for (String part : parts) {
            if (part.startsWith("FROM=")) fromId = part.substring(5);
            else if (part.startsWith("TO=")) toId = part.substring(3);
        }

        if (addFriendToFile(fromId, toId)) {
            writer.write("FRIEND_ADDED\n");
        } else {
            writer.write("FRIEND_FAIL\n");
        }
        writer.flush();
    }

    private boolean checkLogin(String id, String pw) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundPw = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.startsWith("ID:")) foundId = line.substring(3).trim();
                else if (line.startsWith("PW:")) foundPw = line.substring(3).trim();
                else if (line.startsWith("---")) {
                    if (foundId != null && foundPw != null && foundId.equals(id) && foundPw.equals(pw)) return true;
                    foundId = null;
                    foundPw = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean addUserToFile(String id, String pw, String name) {
        if (isDuplicateId(id)) return false;
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("user_data.txt", true))) {
            fileWriter.write("ID: " + id + System.lineSeparator());
            fileWriter.write("PW: " + pw + System.lineSeparator());
            fileWriter.write("NAME: " + name + System.lineSeparator());
            fileWriter.write("--------------------" + System.lineSeparator());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDuplicateId(String id) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    String foundId = line.substring(3).trim();
                    if (foundId.equals(id)) return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private String findNameById(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            String foundId = null;
            String foundName = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    foundId = line.substring(3).trim();
                } else if (line.startsWith("NAME:")) {
                    foundName = line.substring(5).trim();
                } else if (line.startsWith("---")) {
                    if (foundId != null && foundId.equals(id)) return foundName;
                    foundId = null;
                    foundName = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean addFriendToFile(String fromId, String toId) {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter("friends.txt", true))) {
            fw.write("FRIEND:" + fromId + "," + toId + System.lineSeparator());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}