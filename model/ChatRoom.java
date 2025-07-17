package model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChatRoom {
    private final String roomName;
    private final List<String> participants;

    public ChatRoom(String roomName, List<String> participants) {
        this.roomName = roomName;
        this.participants = Collections.unmodifiableList(participants); // 불변 리스트로 감싸기
    }

    public String getRoomName() {
        return roomName;
    }

    public List<String> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomName='" + roomName + '\'' +
                ", participants=" + participants +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom)) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(roomName, chatRoom.roomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName);
    }
}