

import java.io.*;
import java.net.Socket;

/**
 * 클라이언트 핸들러 클래스
 * 각 클라이언트 연결을 처리하는 스레드 클래스입니다.
 * 클라이언트로부터 받은 요청을 파싱하고 적절한 응답을 보냅니다.
 */
public class ClientHandler extends Thread {
    /** 클라이언트와의 소켓 연결 */
    private Socket socket;
//
    /**
     * 클라이언트 핸들러 생성자
     * 
     * @param socket 클라이언트와의 소켓 연결
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * 스레드 실행 메서드
     * 클라이언트로부터 요청을 받아 처리하고 응답을 보냅니다.
     */
    @Override
    public void run() {
        try (
                // 클라이언트로부터 데이터를 읽기 위한 BufferedReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // 클라이언트에게 데이터를 보내기 위한 BufferedWriter
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String request;

            // 클라이언트로부터 요청을 계속 받아 처리
            while ((request = reader.readLine()) != null) {
                System.out.println("📩 클라이언트 요청: " + request);

                // 요청 타입에 따라 적절한 핸들러 메서드 호출
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
                    // 알 수 없는 명령어에 대한 응답
                    writer.write("UNKNOWN_COMMAND\n");
                    writer.flush();
                }
            }

            // 소켓 연결 종료
            socket.close();
            System.out.println("🔌 클라이언트와 연결 종료됨");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그인 요청을 처리합니다.
     * 
     * @param request 클라이언트로부터 받은 로그인 요청
     * @param writer 클라이언트에게 응답을 보내기 위한 BufferedWriter
     * @throws IOException 입출력 예외
     */
    private void handleLogin(String request, BufferedWriter writer) throws IOException {
        // 요청 문자열에서 ID와 PW 추출
        String[] parts = request.substring("LOGIN:".length()).split(",");
        String id = "", pw = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
        }

        // 로그인 검증 후 결과 전송
        if (checkLogin(id, pw)) {
            writer.write("LOGIN_SUCCESS\n");
        } else {
            writer.write("LOGIN_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 회원가입 요청을 처리합니다.
     * 
     * @param request 클라이언트로부터 받은 회원가입 요청
     * @param writer 클라이언트에게 응답을 보내기 위한 BufferedWriter
     * @throws IOException 입출력 예외
     */
    private void handleSignup(String request, BufferedWriter writer) throws IOException {
        // 요청 문자열에서 ID, PW, NAME 추출
        String[] parts = request.substring("SIGNUP:".length()).split(",");
        String id = "", pw = "", name = "";
        for (String part : parts) {
            if (part.startsWith("ID=")) id = part.substring(3);
            else if (part.startsWith("PW=")) pw = part.substring(3);
            else if (part.startsWith("NAME=")) name = part.substring(5);
        }

        // 회원가입 처리 후 결과 전송
        if (addUserToFile(id, pw, name)) {
            writer.write("SIGNUP_SUCCESS\n");
        } else {
            writer.write("SIGNUP_FAIL\n");
        }
        writer.flush();
    }

    /**
     * ID 중복 확인 요청을 처리합니다.
     * 
     * @param request 클라이언트로부터 받은 ID 확인 요청
     * @param writer 클라이언트에게 응답을 보내기 위한 BufferedWriter
     * @throws IOException 입출력 예외
     */
    private void handleCheckId(String request, BufferedWriter writer) throws IOException {
        String id = request.substring("CHECK_ID:".length()).trim();
        if (isDuplicateId(id)) {
            writer.write("ID_DUPLICATE\n");
        } else {
            writer.write("ID_AVAILABLE\n");
        }
        writer.flush();
    }

    /**
     * ID로 사용자 검색 요청을 처리합니다.
     * 
     * @param request 클라이언트로부터 받은 ID 검색 요청
     * @param writer 클라이언트에게 응답을 보내기 위한 BufferedWriter
     * @throws IOException 입출력 예외
     */
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

    /**
     * 친구 요청을 처리합니다.
     * 
     * @param request 클라이언트로부터 받은 친구 요청
     * @param writer 클라이언트에게 응답을 보내기 위한 BufferedWriter
     * @throws IOException 입출력 예외
     */
    private void handleFriendRequest(String request, BufferedWriter writer) throws IOException {
        // 요청 문자열에서 FROM과 TO ID 추출
        String[] parts = request.substring("REQUEST_FRIEND:".length()).split(",");
        String fromId = "", toId = "";
        for (String part : parts) {
            if (part.startsWith("FROM=")) fromId = part.substring(5);
            else if (part.startsWith("TO=")) toId = part.substring(3);
        }

        // 친구 추가 처리 후 결과 전송
        if (addFriendToFile(fromId, toId)) {
            writer.write("FRIEND_ADDED\n");
        } else {
            writer.write("FRIEND_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 로그인 정보를 검증합니다.
     * 
     * @param id 사용자 ID
     * @param pw 사용자 비밀번호
     * @return 로그인 성공 여부
     */
    private boolean checkLogin(String id, String pw) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundPw = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.startsWith("ID:")) foundId = line.substring(3).trim();
                else if (line.startsWith("PW:")) foundPw = line.substring(3).trim();
                else if (line.startsWith("---")) {
                    // 구분선을 만나면 한 사용자의 정보가 끝난 것으로 간주
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

    /**
     * 새로운 사용자를 파일에 추가합니다.
     * 
     * @param id 사용자 ID
     * @param pw 사용자 비밀번호
     * @param name 사용자 이름
     * @return 추가 성공 여부
     */
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

    /**
     * ID 중복 여부를 확인합니다.
     * 
     * @param id 확인할 사용자 ID
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
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

    /**
     * ID로 사용자 이름을 찾습니다.
     * 
     * @param id 찾을 사용자 ID
     * @return 사용자 이름 (찾지 못한 경우 null)
     */
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
                    // 구분선을 만나면 한 사용자의 정보가 끝난 것으로 간주
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

    /**
     * 친구 관계를 파일에 추가합니다.
     * 
     * @param fromId 친구 요청을 보낸 사용자 ID
     * @param toId 친구 요청을 받은 사용자 ID
     * @return 추가 성공 여부
     */
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