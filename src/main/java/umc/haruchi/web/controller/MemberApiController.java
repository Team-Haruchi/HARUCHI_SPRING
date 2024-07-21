package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.code.status.SuccessStatus;
import umc.haruchi.converter.MemberConverter;
import umc.haruchi.domain.Member;
import umc.haruchi.service.MemberService;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "이메일 인증으로 회원가입을 진행하는 API")
    public ApiResponse<MemberResponseDTO.MemberJoinResultDTO> join(@Valid @RequestBody MemberRequestDTO.MemberJoinDTO request) throws Exception {
        Member member = memberService.joinMember(request);
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @PostMapping("/signup/email")
    @Operation(summary = "이메일 인증 요청 API", description = "이메일에 인증 번호를 보내는 API")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받을 메일 주소")
    })
    public ApiResponse<MemberResponseDTO> sendEmail(@Email(message = "이메일 형식이 올바르지 않습니다.") @RequestParam("email") String email) throws Exception {
        memberService.sendSimpleMessage(email);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup/email/verify")
    @Operation(summary = "이메일 인증 확인 API", description = "이메일 인증 번호를 확인하는 API")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받은 메일 주소"),
            @Parameter(name = "code", description = "받은 인증 코드")
    })
    public ApiResponse<MemberResponseDTO> verifyEmail(@Email(message = "이메일 형식이 올바르지 않습니다.") @RequestParam("email") String email,
                                                      @RequestParam("code") String code) throws Exception {
        String authCode = memberService.getVerificationCode(email);
        memberService.verificationEmail(code, authCode);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인을 진행하는 API")
    public ApiResponse<MemberResponseDTO.LoginJwtTokenDTO> login(@Valid @RequestBody MemberRequestDTO.MemberLoginDTO request) {
        MemberResponseDTO.LoginJwtTokenDTO token = memberService.login(request);
        log.info("request email = {}, password = {}", request.getEmail(), request.getPassword());
        //log.info("jwtToken accesstoken = {}, refreshtoken = {}", token.getAccessToken(), token.getRefreshToken());
        return ApiResponse.onSuccess(token);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃을 진행하는 API")
    public ApiResponse<MemberResponseDTO.MemberJoinResultDTO> logout(@Valid @RequestBody MemberRequestDTO member) {
        return null;
    }

    @PostMapping("/delete")
    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴를 진행하는 API")
    public ApiResponse<MemberResponseDTO.MemberJoinResultDTO> deleteMember(@Valid @RequestBody MemberRequestDTO member) {
        return null;
    }
}
