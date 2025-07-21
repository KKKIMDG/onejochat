package model;

/**
 * 사용자 모델 클래스
 * 채팅 애플리케이션의 사용자 정보를 담는 데이터 클래스입니다.//
 */
public class User {

    /** 사용자 ID */
    private String id;

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

}
