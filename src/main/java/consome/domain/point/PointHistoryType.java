package consome.domain.point;

import lombok.Getter;

@Getter
public enum PointHistoryType {
    /**
     * EARN, PENALIZE 기본형임으로 사용 금지
     */
    EARN("포인트 적립"),
    PENALIZE("포인트 차감"),

    SIGNUP("회원가입", EARN),
    POST_WRITE("게시글 작성", EARN),
    COMMENT_WRITE("댓글 작성", EARN),
    POST_LIKE("게시글 추천", EARN),
    COMMENT_LIKE("댓글 추천", EARN),
    POST_DISLIKE("게시글 비추천", PENALIZE),
    COMMENT_DISLIKE("댓글 비추천", PENALIZE);

    private final String description;
    private final PointHistoryType baseType;

    PointHistoryType(String description) {
        this.description = description;
        this.baseType = this;
    }

    PointHistoryType(String description, PointHistoryType baseType) {
        this.description = description;
        this.baseType = baseType;
    }

    public PointHistoryType getActualType() {
        return this.baseType;
    }
}
