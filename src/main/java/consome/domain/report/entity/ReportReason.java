package consome.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
    SPAM("스팸/도배"),
    ABUSE("욕설/비방"),
    ADULT("음란물"),
    ILLEGAL("불법정보"),
    OTHER("기타");

    private final String description;
}
