package service;

import model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginService {

    User user = new User();

    // 🔐 2. 텍스트 파일과 비교
    public boolean isLogin(String inputId, String inputPw) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            String savedId = "";
            String savedPw = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    savedId = line.substring(4).trim();

                    line = reader.readLine(); // 다음 줄은 PW 라고 가정
                    if (line != null && line.startsWith("PW:")) {
                        savedPw = line.substring(4).trim();

                        // 바로 비교
                        if (inputId.equals(savedId) && inputPw.equals(savedPw)) {
                            return true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // 못 찾았으면 false
    }
}