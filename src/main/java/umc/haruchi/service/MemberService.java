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
import umc.haruchi.apiPayload.exception.handler.JwtExceptionHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.config.login.jwt.JwtUtil;
import umc.haruchi.converter.MemberConverter;
import umc.haruchi.converter.MonthBudgetConverter;
import umc.haruchi.domain.Member;

import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.Withdrawer;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.repository.*;

import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MonthBudgetRepository monthBudgetRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;
    private final WithdrawerRepository withdrawerRepository;
    private final JwtUtil jwtUtil;

    public static int code;

    private final MonthBudgetService monthBudgetService;
    private final DayBudgetRepository dayBudgetRepository;

    // 회원가입
    @Transactional
    public Member joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception {

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));

        //회원가입 시 monthBudget 생성
        MonthBudget monthBudget = MonthBudgetConverter.toMonthBudget(request.getMonthBudget());
        monthBudget.setMember(newMember);

        return memberRepository.save(newMember);
    }

    //dayBudget 생성
    @Transactional
    public void connectToDayBudget(Long memberId) {
        List<DayBudget> dayBudgets = monthBudgetService.distributeDayBudgets(memberId);
        dayBudgetRepository.saveAll(dayBudgets);
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

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new MemberHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        // 30일 이상 미접속 시 로그아웃 되도록 토큰 유효시간을 수정
        String accessToken = JwtUtil.createAccessJwt(member.getId(), member.getEmail(), null);
        String refreshToken = JwtUtil.createRefreshJwt(member.getId(), member.getEmail(), null);

        Long accessExpiredAt = JwtUtil.getExpiration(accessToken);
        Long refreshExpiredAt = JwtUtil.getExpiration(refreshToken);

        redisTemplate.opsForValue().set("RT" +  email, refreshToken, refreshExpiredAt, TimeUnit.MILLISECONDS);

        return MemberResponseDTO.LoginJwtTokenDTO.builder()
                .grantType("Bearer")
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessExpiredAt)
                .refreshTokenExpirationAt(refreshExpiredAt)
                .build();
    }

    // 토큰 재발급
    public MemberResponseDTO.LoginJwtTokenDTO reissue(String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new JwtExceptionHandler(ErrorStatus.NOT_VALID_TOKEN.getMessage());
        }

        String email = jwtUtil.getEmail(refreshToken);

        Object o = redisTemplate.opsForValue().get("RT" + email);
        if (o == null) {
            throw new JwtExceptionHandler(ErrorStatus.NO_MATCH_REFRESHTOKEN.getMessage());
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));

        String newAccessToken = JwtUtil.createAccessJwt(member.getId(), member.getEmail(), null);
        String newRefreshToken = JwtUtil.createRefreshJwt(member.getId(), member.getEmail(), null);

        Long accessExpiredAt = JwtUtil.getExpiration(newAccessToken);
        Long refreshExpiredAt = JwtUtil.getExpiration(newRefreshToken);

        redisTemplate.opsForValue().set("RT" + member.getEmail(), newRefreshToken, refreshExpiredAt, TimeUnit.MILLISECONDS);

        return MemberResponseDTO.LoginJwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .accessTokenExpiresAt(accessExpiredAt)
                .refreshToken(newRefreshToken)
                .refreshTokenExpirationAt(refreshExpiredAt)
                .build();
    }

    // 로그아웃 (액세스 토큰 블랙리스트에 저장)
    public void logout(String accessToken, String refreshToken, String type) {

        try {
            jwtUtil.validateToken(accessToken);
        } catch (JwtExceptionHandler e) {
            throw new JwtExceptionHandler(ErrorStatus.NOT_VALID_TOKEN.getMessage());
        }

        String email = jwtUtil.getEmail(accessToken);

        if (redisTemplate.opsForValue().get("RT" + email) != null) {
            redisTemplate.delete("RT" + email);
        }

        Long expiration = JwtUtil.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        if (type.equals("DELETE")) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));
            memberRepository.delete(member);
        }
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
                .createdAt(member.getCreatedAt().toLocalDate()).build();
    }
}
