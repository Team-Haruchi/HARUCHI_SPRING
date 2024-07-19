package umc.haruchi.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.converter.MemberConverter;
import umc.haruchi.domain.Member;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.web.dto.MemberRequestDTO;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JavaMailSender mailSender;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    public static int code;

    @Transactional
    public Member joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception {

        if (memberRepository.findByName(request.getName()).isPresent()) {
            throw new MemberHandler(ErrorStatus.EXISTED_NAME);
        }

//        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new Exception("이미 존재하는 이메일입니다.");
//        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(encoder.encode(newMember.getPassword()));
        return memberRepository.save(newMember);
    }

    @Transactional
    public void checkDuplicatedEmail(String email) throws Exception {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new MemberHandler(ErrorStatus.EXISTED_EMAIL);
        }
    }

    @Transactional
    public MimeMessage createMessage(String to) throws Exception {

        checkDuplicatedEmail(to);
        MimeMessage message = mailSender.createMimeMessage();
        code = (int)(Math.random() * 90000) + 100000;

        message.setFrom(new InternetAddress("haruchi@haruchi.com", "Haruchi_Admin"));
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("Haruchi 회원가입 이메일 인증");

        String msg = "";
        msg += "<h3>" + "Haruchi 이메일 인증 번호입니다." + "</h3>";
        msg += "<h1>" + code + "</h1>";
        msg += "<h3>" + "감사합니다." + "</h3>";
        message.setText(msg, "utf-8", "html");

        return message;
    }

    @Transactional
    public void sendSimpleMessage(String to) throws Exception {

        MimeMessage message = createMessage(to);
        try {
            mailSender.send(message);
            saveVerificationCode(to, String.valueOf(code));
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 3, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    public void verificationEmail(String code, String savedCode) throws Exception {
        if (!code.equals(savedCode)) {
            throw new Exception("인증 번호가 일치하지 않습니다.");
        }
    }
}
