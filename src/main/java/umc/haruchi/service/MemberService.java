package umc.haruchi.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.config.login.jwt.JwtUtil;
import umc.haruchi.converter.MemberConverter;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.enums.MemberStatus;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;

    public static int code;

    @Transactional
    public Member joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception {

        // 이메일 인증 요청에서 미리 처리하니까 삭제해도 됨
        checkDuplicatedEmail(request.getEmail());

        // 이메일 인증 여부 확인
        if (!request.isVerifiedEmail()) {
            throw new MemberHandler(ErrorStatus.NOT_VERIFIED_EMAIL);
        }

        // 중복돼도 괜찮아서 주석 처리
//        if (memberRepository.findByName(request.getName()).isPresent()) {
//            throw new MemberHandler(ErrorStatus.EXISTED_NAME);
//        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
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
        redisTemplate.opsForValue().set(email, code, 1, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    public void verificationEmail(String code, String savedCode) throws Exception {
        if (!code.equals(savedCode)) {
            throw new MemberHandler(ErrorStatus.EMAIL_VERIFY_FAILED);
        }
    }

    public MemberResponseDTO.LoginJwtTokenDTO login(MemberRequestDTO.MemberLoginDTO loginDto) {
        String email = loginDto.getEmail();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.setMemberStatusLogin();

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new MemberHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        String accessToken = JwtUtil.createJwt(member.getId(), member.getEmail(), null);

        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

        return MemberResponseDTO.LoginJwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresAt(expiredAt)
                .build();
    }

    // 혹시 몰라 남겨둠
//    public MemberResponseDTO.LoginJwtTokenDTO login(MemberRequestDTO.MemberLoginDTO request) {
//
//        String email = request.getEmail();
//        String password = request.getPassword();
//        Member member = memberRepository.findByEmail(email).orElse(null);
//
//        if (member == null) {
//            throw new UsernameNotFoundException("이메일이 존재하지 않습니다.");
//        }
//
//        if (!encoder.matches(password, member.getPassword())) {
//            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
//        }
//
////        // Login email/password를 기반으로 Authentication 객체 생성
////        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthenticationToken();
////
////        // 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
////        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
////
////        // 검증된 인증 정보로 JWT token 생성
////        MemberResponseDTO.LoginJwtTokenDTO token = jwtUtil.generateToken(authentication);
////
////        redisTemplate.opsForValue()
////                .set("RT:" + authentication.getName(), token.getRefreshToken(), token.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
////
////        return token;
//        return null;
//    }
}
