package consome.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@consome.site}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "[Consome] 이메일 인증을 완료해주세요";
        String verifyUrl = baseUrl + "/email/verify?token=" + token;
        log.info("[DEV] 이메일 인증 URL: {}", verifyUrl);
        String content = buildVerificationEmailContent(verifyUrl);

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.info("[DEV] 이메일 발송 skip - 수신: {}, 제목: {}", to, subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String buildVerificationEmailContent(String verifyUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2563eb;">Consome 이메일 인증</h2>
                    <p>안녕하세요! Consome 회원가입을 환영합니다.</p>
                    <p>아래 버튼을 클릭하여 이메일 인증을 완료해주세요.</p>
                    <div style="margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #2563eb; color: white; padding: 12px 24px;
                                  text-decoration: none; border-radius: 4px; display: inline-block;">
                            이메일 인증하기
                        </a>
                    </div>
                    <p style="color: #666; font-size: 14px;">
                        버튼이 동작하지 않는 경우, 아래 링크를 복사하여 브라우저에 붙여넣어 주세요:<br>
                        <a href="%s">%s</a>
                    </p>
                    <p style="color: #999; font-size: 12px; margin-top: 30px;">
                        이 링크는 24시간 동안 유효합니다.<br>
                        본인이 요청하지 않은 경우, 이 메일을 무시해주세요.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(verifyUrl, verifyUrl, verifyUrl);
    }
}
