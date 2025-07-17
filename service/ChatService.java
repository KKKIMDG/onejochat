package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ChatService {

    /**
     * 채팅방 생성 및 참여자 저장
     * 첫 번째 줄에 환영 메시지를 기록하고, 참여자 이름을 기록합니다.
     *
     * @param roomName 채팅방 이름
     * @param participants 참여자 이름 리스트
     * @return 저장 성공 여부
     */
    public boolean createChatRoomFile(String roomName, List<String> participants) {
        File chatFile = new File(roomName + ".txt");//

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile))) {
            // 첫 줄 환영 메시지 작성
            writer.write("환영합니다. " + roomName + " 채팅방입니다.\n");
            // 참여자 리스트 기록
            writer.write("참여자: " + String.join(", ", participants) + "\n");
            writer.write("-------------------------\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 채팅 메시지를 채팅방 파일에 추가 저장
     *
     * @param roomName 채팅방 이름
     * @param sender 메시지 보낸 사람 이름
     * @param message 메시지 내용
     * @return 저장 성공 여부
     */
    public boolean appendChatMessage(String roomName, String sender, String message) {
        File chatFile = new File(roomName + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile, true))) {
            writer.write(sender + ": " + message + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 친구 초대 리스트 관리 기능 등은 UI 컨트롤러에서 처리하고,
    // 필요한 경우 추가 서비스 메서드로 확장 가능
}