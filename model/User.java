package model;

/**
 * 사용자 모델 클래스
 * 채팅 애플리케이션의 사용자 정보를 담는 데이터 클래스입니다.//
 */
public class User {

    /** 사용자 ID */
    private String id;
    /** 사용자 비밀번호 */
    private String password;
    /** 사용자 이름 */
    private String name;

    /**
     * 사용자 ID를 반환합니다.
     * 
     * @return 사용자 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 사용자 ID를 설정합니다.
     * 
     * @param id 설정할 사용자 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 사용자 비밀번호를 반환합니다.
     * 
     * @return 사용자 비밀번호
     */
    public String getPassword() {
        return password;
    }

    /**
     * 사용자 비밀번호를 설정합니다.
     * 
     * @param password 설정할 사용자 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 사용자 이름을 반환합니다.
     * 
     * @return 사용자 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 사용자 이름을 설정합니다.
     * 
     * @param name 설정할 사용자 이름
     */
    public void setName(String name) {
        this.name = name;
    }

}
