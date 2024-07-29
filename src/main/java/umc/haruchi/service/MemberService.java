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
import umc.haruchi.domain.MemberToken;
import umc.haruchi.domain.Withdrawer;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MemberTokenRepository;
import umc.haruchi.repository.WithdrawerRepository;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

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
    private final MemberTokenRepository memberTokenRepository;
    private final WithdrawerRepository withdrawerRepository;

    public static int code;

    // 회원가입
    @Transactional
    public Member joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception {

        // 이용약관은 무조건 체크돼야 들어오므로 스킵함

        // 이메일 인증 요청에서 미리 처리하니까 삭제해도 됨
        checkDuplicatedEmail(request.getEmail());

        // 이메일 인증 여부 확인 - 프론트에서 해결해준다면 삭제해도 됨
        if (!request.isVerifiedEmail()) {
            throw new MemberHandler(ErrorStatus.NOT_VERIFIED_EMAIL);
        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
        return memberRepository.save(newMember);
    }

    // 비밀번호 확인
    @Transactional
    public void checkPassword(String password, String verifyPassword) {
        if (!password.equals(verifyPassword)) {
            throw new MemberHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }
    }

    // 이메일 중복 체크
    @Transactional
    public void checkDuplicatedEmail(String email) throws Exception {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new MemberHandler(ErrorStatus.EXISTED_EMAIL);
        }
    }

    // 인증 번호를 전송할 메세지 생성
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

    // 이메일 인증 번호 전송
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

    // 이메일 인증 번호 redis에 저장
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set("emailVerify" + email, code, 130, TimeUnit.SECONDS);
    }

    // 이메일 인증 번호 redis에서 얻기
    public String getVerificationCode(String email) {
        return (String) redisTemplate.opsForValue().get("emailVerify" + email);
    }

    // 인증 번호로 이메일 인증
    public void verificationEmail(String code, String savedCode) throws Exception {
        if (!code.equals(savedCode)) {
            throw new MemberHandler(ErrorStatus.EMAIL_VERIFY_FAILED);
        }
    }

    // 로그인 (access token 발급)
    public MemberResponseDTO.LoginJwtTokenDTO login(MemberRequestDTO.MemberLoginDTO loginDto) {
        String email = loginDto.getEmail();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));

        member.setMemberStatusLogin();

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new MemberHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        // 30일 이상 미접속 시 로그아웃 되도록 토큰 유효시간을 수정
        String accessToken = JwtUtil.createAccessJwt(member.getId(), member.getEmail(), null);
        String refreshToken = JwtUtil.createRefreshJwt(member.getId(), member.getEmail(), null);
        MemberToken token = MemberToken.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        memberTokenRepository.save(token);

        Long accessExpiredAt = JwtUtil.getExpiration(accessToken);
        Long refreshExpiredAt = JwtUtil.getExpiration(refreshToken);

        return MemberResponseDTO.LoginJwtTokenDTO.builder()
                .grantType("Bearer")
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessExpiredAt)
                .refreshTokenExpirationAt(refreshExpiredAt)
                .build();
    }

    // 회원 즉시 탈퇴 - 이유 저장
    public void withdrawer(String reason) {
        Withdrawer withdrawer = Withdrawer.builder()
                .reason(reason)
                .build();
        withdrawerRepository.save(withdrawer);
    }

    // 회원 더보기 정보(가입일, 가입 이메일, 닉네임) 조회
    public MemberResponseDTO.MemberDetailResultDTO getMemberDetail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));
        return MemberResponseDTO.MemberDetailResultDTO.builder()
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt()).build();
    }

    // 회원의 세이프박스 금액 조회
    public Long getMemberSafeBox(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));
        return member.getSafeBox();
    }
}
