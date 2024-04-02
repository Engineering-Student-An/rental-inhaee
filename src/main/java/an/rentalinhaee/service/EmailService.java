package an.rentalinhaee.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String receiver, String authCode, String type) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 메일을 받을 수신자 설정
            mimeMessageHelper.setTo(receiver);
            // 메일의 제목 설정
            mimeMessageHelper.setSubject("PULSE 대여 사업 이메일 인증");

            // 메일의 내용 설정
            mimeMessageHelper.setText(setContext(authCode, type), true);

            javaMailSender.send(mimeMessage);

//            log.info("메일 발송 성공!");
        } catch (Exception e) {
//            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }

    public String createVerifyCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0 : key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1 : key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }

        return key.toString();
    }

    public String setContext(String verifyCode, String type) {
        Context context = new Context();
        context.setVariable("verifyCode", verifyCode);
        return templateEngine.process(type, context);
    }
}
