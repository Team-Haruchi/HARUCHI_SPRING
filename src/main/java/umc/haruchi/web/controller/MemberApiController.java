package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.jwt.JwtTokenService;
import umc.haruchi.converter.MemberConverter;
import umc.haruchi.domain.Member;
import umc.haruchi.service.MemberService;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "member", description = "회원 관련 API")
public class MemberApiController {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "이메일 인증으로 회원가입을 진행하는 API")
    public ApiResponse<MemberResponseDTO.MemberJoinResultDTO> join(@Valid @RequestBody MemberRequestDTO.MemberJoinDTO request) throws Exception {
        Member member = memberService.joinMember(request);
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @PostMapping("/signup/password")
    @Operation(summary = "비밀번호 2차 확인 API", description = "회원이 입력한 비밀번호와 확인용 비밀번호를 비교하는 API")
    @Parameters({
            @Parameter(name = "password", description = "회원가입을 진행할 비밀번호"),
            @Parameter(name = "checkPassword", description = "확인용 비밀번호")
    })
    public ApiResponse<MemberResponseDTO> verifyPassword(@RequestParam("password") String password,
                                                        @RequestParam("checkPassword") String verifyPassword) throws Exception {
        memberService.checkPassword(password, verifyPassword);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup/email")
    @Operation(summary = "이메일 인증 요청 API", description = "이메일에 인증 번호를 보내는 API")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받을 메일 주소")
    })
    public ApiResponse<MemberResponseDTO> sendEmail(@Email(message = "이메일 형식이 올바르지 않습니다.")
                                                        @RequestParam("email") String email) throws Exception {
        memberService.sendSimpleMessage(email);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup/email/verify")
    @Operation(summary = "이메일 인증 확인 API", description = "이메일 인증 번호를 확인하는 API")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받은 메일 주소"),
            @Parameter(name = "code", description = "받은 인증 코드")
    })
    public ApiResponse<MemberResponseDTO> verifyEmail(@Email(message = "이메일 형식이 올바르지 않습니다.")
                                                          @RequestParam("email") String email,
                                                      @RequestParam("code") String code) throws Exception {
        String authCode = memberService.getVerificationCode(email);
        memberService.verificationEmail(code, authCode);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인을 진행하는 API")
    public ApiResponse<MemberResponseDTO.LoginJwtTokenDTO> login(@Valid @RequestBody MemberRequestDTO.MemberLoginDTO request) {
        MemberResponseDTO.LoginJwtTokenDTO token = memberService.login(request);
        return ApiResponse.onSuccess(token);
    }

    @GetMapping("/test")
    public String loginTest() {
        return "login user";
    }

    @PostMapping("/refresh") // 오류 발생 -> 헤더 인식 불가능
    @Operation(summary = "액세스 토큰과 리프레시 토큰 재발급 API", description = "리프레시 토큰으로 액세스 토큰과 리프레시 토큰을 재발급하는 API")
    public ApiResponse<MemberResponseDTO.LoginJwtTokenDTO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        MemberResponseDTO.LoginJwtTokenDTO tokens = jwtTokenService.refresh(refreshToken);
        return ApiResponse.onSuccess(tokens);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃을 진행하는 API")
    public ApiResponse<MemberResponseDTO> logout(@RequestParam("token") String token) {
        jwtTokenService.expire(token, "LOGOUT");
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/delete")
    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴를 진행하는 API")
    public ApiResponse<MemberResponseDTO> deleteMember(@RequestParam("token") String token) {
        jwtTokenService.expire(token, "INACTIVE");
        return ApiResponse.onSuccess(null);
    }
}
