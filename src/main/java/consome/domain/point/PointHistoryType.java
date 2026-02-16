package consome.domain.point;

import lombok.Getter;

@Getter
public enum PointHistoryType {
    /**
     * EARN, PENALIZE 기본형임으로 사용 금지
     */
    EARN("포인트 적립"),
    PENALIZE("포인트 차감"),

    REGISTER("회원가입", EARN, 100),
    POST_WRITE("게시글 작성", EARN, 50),
    COMMENT_WRITE("댓글 작성", EARN, 10),
    POST_LIKE("게시글 추천", EARN, 5),
    COMMENT_LIKE("댓글 추천", EARN, 3),
    COMMENT_DISLIKE_CANCEL("댓글 비추천 취소", EARN, 1),
    POST_DISLIKE("게시글 비추천", PENALIZE, 3),
    COMMENT_DISLIKE("댓글 비추천", PENALIZE, 1),
    POST_DEL("게시글 삭제", PENALIZE, 50),
    COMMENT_LIKE_CANCEL("댓글 추천 취소", PENALIZE, 1),
    COMMENT_DEL("댓글 삭제", PENALIZE, 10),
    NICKNAME_CHANGE("닉네임 변경", PENALIZE, 100),
    GIFT_SEND("포인트 선물 발송", PENALIZE, 0),
    GIFT_RECEIVE("포인트 선물 수신", EARN, 0);


    private final String description;
    private final PointHistoryType baseType;
    private final int point;

    PointHistoryType(String description) {
        this.description = description;
        this.baseType = this;
        this.point = 0;
    }

    PointHistoryType(String description, PointHistoryType baseType, int point) {
        this.description = description;
        this.baseType = baseType;
        this.point = point;
    }

    public PointHistoryType getActualType() {
        return this.baseType;
    }
}
