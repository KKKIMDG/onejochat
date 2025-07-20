package service;

import java.io.*;

public class UserService {
    // 사용자 이름(ID)로 검색 (user_data.txt에서)
    public String findUserNameById(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundName = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) foundId = line.substring(3).trim();
                else if (line.startsWith("NAME:")) foundName = line.substring(5).trim();
                else if (line.startsWith("---")) {
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

    // 로그인 검증
    public boolean checkLogin(String id, String pw) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line, foundId = null, foundPw = null;
            while ((line = reader.readLine()) != null) {
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
} 