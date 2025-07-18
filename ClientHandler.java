

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 클라이언트 핸들러 클래스
 * 각 클라이언트 연결을 처리하는 스레드 클래스입니다.
 * 클라이언트로부터 받은 요청을 파싱하고 적절한 응답을 보냅니다.
 */
public class ClientHandler extends Thread {
    /** 클라이언트와의 소켓 연결 */
    private Socket socket;
    // 현재 로그인 중인 사용자 목록 (중복 로그인 방지)
    private static final Set<String> loggedInUsers = Collections.synchronizedSet(new HashSet<>());
    // 현재 핸들러의 로그인 id
    private String loginId = null;

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
                } else if (request.startsWith("CREATE_CHATROOM")) {
                    handleCreateChatRoom(reader, writer);
                } else if (request.startsWith("CREATE_SECRET_CHATROOM")) {
                    handleCreateSecretChatRoom(reader, writer);
                } else if (request.startsWith("SEND_MESSAGE:")) {
                    handleSendMessage(request, writer);
                } else if (request.startsWith("GET_CHAT_HISTORY:")) {
                    handleGetChatHistory(request, writer);
                } else if (request.startsWith("GET_MY_CHATROOMS:")) {
                    handleGetMyChatRooms(request, writer);
                } else if (request.startsWith("LEAVE_CHATROOM:")) {
                    handleLeaveChatRoom(request, writer);
                } else if (request.startsWith("QUIT")) {
                    System.out.println("클라이언트 연결 종료 요청");
                    break;
                } else if (request.startsWith("INVITE_TO_CHATROOM:")) {
                    handleInviteToChatRoom(request, writer);
                } else if (request.startsWith("GET_PARTICIPANTS:")) {
                    handleGetParticipants(request, writer);
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
        } finally {
            // 연결 종료 시 로그인 목록에서 제거
            if (loginId != null) {
                loggedInUsers.remove(loginId);
                System.out.println("로그아웃: " + loginId);
            }
            try { socket.close(); } catch (Exception ignore) {}
            System.out.println("🔌 클라이언트와 연결 종료됨");
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
        // 중복 로그인 체크
        if (loggedInUsers.contains(id)) {
            writer.write("LOGIN_DUPLICATE\n");
            writer.flush();
            return;
        }
        // 로그인 검증 후 결과 전송
        if (checkLogin(id, pw)) {
            writer.write("LOGIN_SUCCESS\n");
            writer.flush();
            loggedInUsers.add(id);
            loginId = id;
        } else {
            writer.write("LOGIN_FAIL\n");
            writer.flush();
        }
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
     * 채팅방 생성 요청을 처리합니다.
     * 클라이언트로부터 채팅방 이름, 방장, 참여자 목록을 받아 파일로 저장합니다.
     *//////
    private void handleCreateChatRoom(BufferedReader reader, BufferedWriter writer) throws IOException {
        String roomName = null;
        String owner = null;
        String invited = null;
        // 채팅방 정보는 다음 3줄로 온다고 가정
        for (int i = 0; i < 3; i++) {
            String line = reader.readLine();
            if (line == null) break;
            if (line.startsWith("roomName:")) roomName = line.substring("roomName:".length());
            else if (line.startsWith("owner:")) owner = line.substring("owner:".length());
            else if (line.startsWith("invited:")) invited = line.substring("invited:".length());
        }
        if (roomName == null || owner == null || invited == null) {
            writer.write("CREATE_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        boolean created = createChatRoomFile(owner, roomName, invited);
        if (created) {
            writer.write("CREATE_CHATROOM_SUCCESS\n");
        } else {
            writer.write("CREATE_CHATROOM_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 비밀채팅방 생성 요청을 처리합니다.
     * 클라이언트로부터 채팅방 이름, 방장, 참여자, 룸코드를 받아 파일로 저장합니다.
     */
    private void handleCreateSecretChatRoom(BufferedReader reader, BufferedWriter writer) throws IOException {
        String roomName = null;
        String owner = null;
        String invited = null;
        String roomCode = null;
        // 채팅방 정보는 다음 4줄로 온다고 가정
        for (int i = 0; i < 4; i++) {
            String line = reader.readLine();
            if (line == null) break;
            if (line.startsWith("roomName:")) roomName = line.substring("roomName:".length());
            else if (line.startsWith("owner:")) owner = line.substring("owner:".length());
            else if (line.startsWith("invited:")) invited = line.substring("invited:".length());
            else if (line.startsWith("roomCode:")) roomCode = line.substring("roomCode:".length());
        }
        if (roomName == null || owner == null || invited == null || roomCode == null) {
            writer.write("CREATE_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        boolean created = createSecretChatRoomFile(owner, roomName, invited, roomCode);
        if (created) {
            writer.write("CREATE_CHATROOM_SUCCESS\n");
        } else {
            writer.write("CREATE_CHATROOM_FAIL\n");
        }
        writer.flush();
    }

    // 파일명 생성 유틸: 비밀방이면 ownerId_roomName.txt, 일반방이면 roomName.txt
    private String getChatRoomFilename(String ownerId, String roomName, boolean isSecret) {
        return isSecret ? (ownerId + "_" + roomName + ".txt") : (roomName + ".txt");
    }

    // 파일이 비밀방인지 확인 (ROOM_CODE 라인 존재 여부)
    private boolean isSecretChatRoomFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ROOM_CODE:")) return true;
                if (line.startsWith("----------------")) break;
            }
        } catch (Exception e) {}
        return false;
    }

    /**
     * 채팅방 정보를 파일로 저장합니다.
     * @param ownerId 방장 ID
     * @param roomName 채팅방 이름
     * @param invited 참여자 목록(쉼표 구분)
     * @return 생성 성공 여부
     */
    private boolean createChatRoomFile(String ownerId, String roomName, String invited) {
        String filename = getChatRoomFilename(ownerId, roomName, false);
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename))) {
            fileWriter.write("ROOM_NAME: " + roomName + System.lineSeparator());
            fileWriter.write("참여자: " + invited + System.lineSeparator());
            fileWriter.write("--------------------" + System.lineSeparator());
            // 메시지는 append로 추가됨
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 비밀채팅방 파일 생성 (ROOM_CODE 라인 포함)
     */
    private boolean createSecretChatRoomFile(String ownerId, String roomName, String invited, String roomCode) {
        String filename = ownerId + "_" + roomName + ".txt";
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename))) {
            fileWriter.write("ROOM_NAME: " + roomName + System.lineSeparator());
            fileWriter.write("OWNER: " + ownerId + System.lineSeparator());
            fileWriter.write("참여자: " + invited + System.lineSeparator());
            fileWriter.write("ROOM_CODE: " + roomCode + System.lineSeparator());
            fileWriter.write("--------------------" + System.lineSeparator());
            // 메시지는 append로 추가됨
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    /**
     * 클라이언트로부터 받은 메시지를 해당 채팅방 파일(ownerId_roomName.txt)에 저장합니다.
     * 요청 예시: SEND_MESSAGE:OWNERID=alice,ROOM=study,MSG=안녕!
     */
    private void handleSendMessage(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("SEND_MESSAGE:".length()).split(",");
        String ownerId = null, room = null, msg = null, userId = null;
        for (String part : parts) {
            if (part.startsWith("OWNERID=")) ownerId = part.substring(8);
            else if (part.startsWith("ROOM=")) room = part.substring(5);
            else if (part.startsWith("MSG=")) msg = part.substring(4);
            else if (part.startsWith("USERID=")) userId = part.substring(7);
        }
        if (room == null || msg == null || userId == null) {
            writer.write("SEND_MESSAGE_FAIL\n");
            writer.flush();
            return;
        }
        File file = new File(getChatRoomFilename(ownerId, room, false));
        if (!file.exists()) file = new File(getChatRoomFilename(ownerId, room, true));
        if (!file.exists()) {
            writer.write("SEND_MESSAGE_FAIL\n");
            writer.flush();
            return;
        }
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true))) {
            fileWriter.write(userId + ": " + msg + System.lineSeparator());
            writer.write("SEND_MESSAGE_SUCCESS\n");
        } catch (IOException e) {
            writer.write("SEND_MESSAGE_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 채팅방 입장 시 해당 채팅방 파일(ownerId_roomName.txt)의 내용을 클라이언트에 전송합니다.
     * 구분선(-----) 이후의 내용만 전송
     * 요청 예시: GET_CHAT_HISTORY:OWNERID=alice,ROOM=study
     */
    private void handleGetChatHistory(String request, BufferedWriter writer) throws IOException {
        String[] parts = request.substring("GET_CHAT_HISTORY:".length()).split(",");
        String ownerId = null, room = null;
        for (String part : parts) {
            if (part.startsWith("OWNERID=")) ownerId = part.substring(8);
            else if (part.startsWith("ROOM=")) room = part.substring(5);
        }
        if (room == null) {
            writer.write("GET_CHAT_HISTORY_FAIL\n");
            writer.flush();
            return;
        }
        File file = new File(getChatRoomFilename(ownerId, room, false));
        if (!file.exists()) file = new File(getChatRoomFilename(ownerId, room, true));
        if (!file.exists()) {
            writer.write("NO_HISTORY\n");
            writer.flush();
            return;
        }
        boolean afterDivider = false;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (afterDivider) {
                    writer.write(line + "\n");
                }
                if (line.startsWith("---")) {
                    afterDivider = true;
                }
            }
            writer.write("END_OF_HISTORY\n");
        } catch (IOException e) {
            writer.write("GET_CHAT_HISTORY_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 클라이언트가 GET_MY_CHATROOMS:ID=userid 명령을 보내면,
     * 서버가 userid가 포함된 채팅방 파일(ownerId_roomName.txt)들을 찾아 목록을 전송한다.
     * 각 채팅방은 'ownerId_roomName' 형식으로 한 줄씩 전송, 마지막에 END_OF_CHATROOMS 전송
     */
    private void handleGetMyChatRooms(String request, BufferedWriter writer) throws IOException {
        String userId = null;
        String[] parts = request.substring("GET_MY_CHATROOMS:".length()).split(",");
        for (String part : parts) {
            if (part.startsWith("ID=")) userId = part.substring(3);
        }
        if (userId == null) {
            writer.write("GET_MY_CHATROOMS_FAIL\n");
            writer.flush();
            return;
        }
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) {
            writer.write("END_OF_CHATROOMS\n");
            writer.flush();
            return;
        }
        for (File file : files) {
            String fileName = file.getName().replace(".txt", "");
            String[] nameParts = fileName.split("_", 2);
            String ownerIdFromFile = nameParts.length > 1 ? nameParts[0] : null;
            boolean include = false;
            if (ownerIdFromFile != null && ownerIdFromFile.equals(userId)) {
                include = true;
            } else {
                try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        if (line.startsWith("참여자:")) {
                            if (line.contains(userId)) {
                                include = true;
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    // 무시하고 다음 파일로
                }
            }
            if (include) {
                writer.write(fileName + "\n");
            }
        }
        writer.write("END_OF_CHATROOMS\n");
        writer.flush();
    }

    /**
     * 채팅방 나가기 요청을 처리합니다.
     * 채팅방 파일의 참여자 라인에서 userId를 삭제, 참여자 0명이면 파일 삭제
     */
    private void handleLeaveChatRoom(String request, BufferedWriter writer) throws IOException {
        String ownerId = null, room = null, userId = null;
        String[] parts = request.substring("LEAVE_CHATROOM:".length()).split(",");
        for (String part : parts) {
            if (part.startsWith("OWNERID=")) ownerId = part.substring(8);
            else if (part.startsWith("ROOM=")) room = part.substring(5);
            else if (part.startsWith("USERID=")) userId = part.substring(7);
        }
        if (room == null || userId == null) {
            writer.write("LEAVE_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        // 일반방/비밀방 파일명 모두 시도
        File file = new File(getChatRoomFilename(ownerId, room, false));
        if (!file.exists()) file = new File(getChatRoomFilename(ownerId, room, true));
        if (!file.exists()) {
            writer.write("LEAVE_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        // 파일을 읽어서 참여자 라인에서 userId를 삭제
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        boolean hasParticipants = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("참여자:")) {
                    String[] ids = line.substring(4).split(",");
                    StringBuilder newLine = new StringBuilder("참여자:");
                    int count = 0;
                    for (String id : ids) {
                        String trimmed = id.trim();
                        if (!trimmed.equals(userId) && !trimmed.isEmpty()) {
                            if (count > 0) newLine.append(",");
                            newLine.append(" ").append(trimmed);
                            count++;
                        }
                    }
                    if (count > 0) {
                        sb.append(newLine).append(System.lineSeparator());
                        hasParticipants = true;
                    }
                    found = true;
                } else {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        }
        if (!found) {
            writer.write("LEAVE_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        if (!hasParticipants) {
            // 참여자 0명이면 파일 삭제
            file.delete();
        } else {
            // 파일 덮어쓰기
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file, false))) {
                w.write(sb.toString());
            }
        }
        writer.write("LEAVE_CHATROOM_SUCCESS\n");
        writer.flush();
    }

    /**
     * 친구 초대 요청을 처리합니다.
     * 채팅방 파일의 참여자 라인에 FRIENDID를 추가(이미 있으면 실패)
     */
    private void handleInviteToChatRoom(String request, BufferedWriter writer) throws IOException {
        String ownerId = null, room = null, friendId = null;
        String[] parts = request.substring("INVITE_TO_CHATROOM:".length()).split(",");
        for (String part : parts) {
            if (part.startsWith("OWNERID=")) ownerId = part.substring(8);
            else if (part.startsWith("ROOM=")) room = part.substring(5);
            else if (part.startsWith("FRIENDID=")) friendId = part.substring(9);
        }
        if (room == null || friendId == null) {
            writer.write("INVITE_TO_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        File file = new File(getChatRoomFilename(ownerId, room, false));
        if (!file.exists()) file = new File(getChatRoomFilename(ownerId, room, true));
        if (!file.exists()) {
            writer.write("INVITE_TO_CHATROOM_FAIL\n");
            writer.flush();
            return;
        }
        // 파일을 읽어서 참여자 라인에 friendId가 이미 있으면 실패, 없으면 추가
        StringBuilder sb = new StringBuilder();
        boolean invited = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("참여자:")) {
                    if (line.contains(friendId)) {
                        sb.append(line).append(System.lineSeparator());
                        invited = false;
                    } else {
                        String newLine = line.trim().endsWith(":") ? line + " " + friendId : line + ", " + friendId;
                        sb.append(newLine).append(System.lineSeparator());
                        invited = true;
                    }
                } else {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        }
        if (invited) {
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file, false))) {
                w.write(sb.toString());
            }
            writer.write("INVITE_TO_CHATROOM_SUCCESS\n");
        } else {
            writer.write("INVITE_TO_CHATROOM_FAIL\n");
        }
        writer.flush();
    }

    /**
     * 채팅방 참여자 목록 요청을 처리합니다.
     * 채팅방 파일에서 참여자 목록을 읽어서 클라이언트에게 전송합니다.
     */
    private void handleGetParticipants(String request, BufferedWriter writer) throws IOException {
        String ownerId = null, room = null;
        String[] parts = request.substring("GET_PARTICIPANTS:".length()).split(",");
        for (String part : parts) {
            if (part.startsWith("OWNERID=")) ownerId = part.substring(8);
            else if (part.startsWith("ROOM=")) room = part.substring(5);
        }
        if (room == null) {
            writer.write("GET_PARTICIPANTS_FAIL\n");
            writer.flush();
            return;
        }

        // 일반방/비밀방 파일명 모두 시도
        File file = new File(getChatRoomFilename(ownerId, room, false));
        if (!file.exists()) file = new File(getChatRoomFilename(ownerId, room, true));
        if (!file.exists()) {
            writer.write("GET_PARTICIPANTS_FAIL\n");
            writer.flush();
            return;
        }

        // 파일에서 참여자 목록 읽기
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("참여자:")) {
                    String participants = line.substring(4).trim();
                    writer.write("PARTICIPANTS:" + participants + "\n");
                    writer.flush();
                    return;
                }
            }
        } catch (IOException e) {
            writer.write("GET_PARTICIPANTS_FAIL\n");
            writer.flush();
            return;
        }

        // 참여자 라인을 찾지 못한 경우
        writer.write("GET_PARTICIPANTS_FAIL\n");
        writer.flush();
    }
}