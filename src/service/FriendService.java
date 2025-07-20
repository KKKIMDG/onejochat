package service;

import model.Friend;
import java.io.*;
import java.util.*;

public class FriendService {
    public boolean isAlreadyFriend(String myId, String friendId) {
        File file = new File("friends_" + myId + ".txt");
        if (!file.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(friendId)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addFriend(String myId, String friendId) {
        if (isAlreadyFriend(myId, friendId)) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("friends_" + myId + ".txt", true))) {
            writer.write(friendId + System.lineSeparator());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Friend> getFriends(String myId) {
        List<Friend> friends = new ArrayList<>();
        File file = new File("friends_" + myId + ".txt");
        if (!file.exists()) return friends;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                friends.add(new Friend(line.trim(), "")); // 이름 정보는 서버에서 추가 구현 필요
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return friends;
    }
} 