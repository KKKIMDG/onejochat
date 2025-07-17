package service;

import model.ChatRoom;

import java.io.*;
import java.util.*;

public class ChatService {

    private static final String CHAT_FOLDER = "chat";

    public ChatService() {
        File dir = new File(CHAT_FOLDER);
        if (!dir.exists()) dir.mkdir(); // chatrooms 폴더 없으면 생성
    }

    public boolean createChatRoomFile(String roomName, List<String> participants) {
        File folder = new File("chat"); // ✅ 채팅파일 저장 폴더
        if (!folder.exists()) folder.mkdirs(); // 폴더 없으면 생성

        File chatFile = new File(folder, roomName + ".txt"); // 폴더 안에 파일 저장

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile))) {
            writer.write("환영합니다. " + roomName + " 채팅방입니다.\n");
            writer.write("참여자: " + String.join(", ", participants) + "\n");
            writer.write("-------------------------\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean appendChatMessage(String roomName, String sender, String message) {
        File chatFile = new File(CHAT_FOLDER, roomName + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile, true))) {
            writer.write(sender + ": " + message + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ChatRoom> getAllChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();

        File folder = new File("chat"); // ✅ 폴더 경로 동일하게 유지
        if (!folder.exists()) return chatRooms;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return chatRooms;

        for (File file : files) {
            String roomName = file.getName().replace(".txt", "");
            List<String> participants = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("참여자:")) {
                        String[] parts = line.substring(5).split(",\\s*");
                        participants = Arrays.asList(parts);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            chatRooms.add(new ChatRoom(roomName, participants));
        }

        return chatRooms;
    }
}