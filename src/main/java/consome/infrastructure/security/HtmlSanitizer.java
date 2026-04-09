package consome.infrastructure.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

    private static final PolicyFactory POST_POLICY = new HtmlPolicyBuilder()
            // 기본 텍스트 서식
            .allowElements("p", "br", "strong", "em", "u", "s", "sub", "sup",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "ul", "ol", "li", "blockquote", "pre", "code", "hr",
                    "span", "div", "a", "mark")
            // 테이블
            .allowElements("table", "thead", "tbody", "tr", "th", "td")
            // 이미지
            .allowElements("img")
            .allowAttributes("src", "alt", "width", "height").onElements("img")
            .allowUrlProtocols("https", "http")
            // 링크
            .allowAttributes("href", "target", "rel").onElements("a")
            // 비디오
            .allowElements("video", "source")
            .allowAttributes("src", "controls", "type", "width", "height").onElements("video", "source")
            // iframe (유튜브, SNS embed)
            .allowElements("iframe")
            .allowAttributes("src", "width", "height", "frameborder", "allow", "allowfullscreen", "scrolling")
                    .onElements("iframe")
            // SNS embed (blockquote with data attrs)
            .allowAttributes("class", "cite", "data-instgrm-permalink", "data-instgrm-version")
                    .onElements("blockquote")
            // 일반 스타일/클래스
            .allowAttributes("class").globally()
            .allowAttributes("style").globally()
            .toFactory();

    private static final PolicyFactory COMMENT_POLICY = new HtmlPolicyBuilder()
            // 댓글은 최소 서식만 허용
            .allowElements("p", "br", "strong", "em", "a")
            .allowAttributes("href", "target", "rel").onElements("a")
            .allowUrlProtocols("https", "http")
            .toFactory();

    public String sanitizePostContent(String html) {
        if (html == null || html.isBlank()) return html;
        return POST_POLICY.sanitize(html);
    }

    public String sanitizeComment(String text) {
        if (text == null || text.isBlank()) return text;
        return COMMENT_POLICY.sanitize(text);
    }
}
