package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
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

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "이메일 인증으로 회원가입을 진행하는 API (액세스 토큰 필요 없음)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4005", description = "존재하지 않는 회원입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MONTHBUDGET4001", description = "한 달 예산이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<MemberResponseDTO.MemberJoinResultDTO> join(@Valid @RequestBody MemberRequestDTO.MemberJoinDTO request) throws Exception {
        Member member = memberService.joinMember(request);
        memberService.connectToDayBudget(member.getId());
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @PostMapping("/signup/password")
    @Operation(summary = "비밀번호 2차 확인 API", description = "회원이 입력한 비밀번호와 확인용 비밀번호를 비교하는 API (액세스 토큰 필요 없음)")
    @Parameters({
            @Parameter(name = "password", description = "회원가입을 진행할 비밀번호"),
            @Parameter(name = "checkPassword", description = "확인용 비밀번호")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4006", description = "비밀번호가 일치하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<MemberResponseDTO> verifyPassword(@RequestParam("password") String password,
                                                        @RequestParam("checkPassword") String verifyPassword) throws Exception {
        memberService.checkPassword(password, verifyPassword);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup/email")
    @Operation(summary = "이메일 인증 요청 API", description = "이메일에 인증 번호를 보내는 API (액세스 토큰 필요 없음)")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받을 메일 주소")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4002",description = "이미 있는 이메일입니다."),
    })
    public ApiResponse<MemberResponseDTO> sendEmail(@Email(message = "이메일 형식이 올바르지 않습니다.")
                                                        @RequestParam("email") String email) throws Exception {
        memberService.sendSimpleMessage(email);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup/email/verify")
    @Operation(summary = "이메일 인증 확인 API", description = "이메일 인증 번호를 확인하는 API (액세스 토큰 필요 없음)")
    @Parameters({
            @Parameter(name = "email", description = "인증을 받은 메일 주소"),
            @Parameter(name = "code", description = "받은 인증 코드")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4004",description = "인증 번호가 일치하지 않습니다.")
    })
    public ApiResponse<MemberResponseDTO> verifyEmail(@Email(message = "이메일 형식이 올바르지 않습니다.")
                                                          @RequestParam("email") String email,
                                                      @RequestParam("code") String code) throws Exception {
        String authCode = memberService.getVerificationCode(email);
        memberService.verificationEmail(code, authCode);
        return ApiResponse.onSuccess(null);
    }

    // 보안 강화 시 주석 처리 해제
//    @PostMapping("/login")
//    @Operation(summary = "로그인 API", description = "로그인을 진행하는 API (토큰 발급) (액세스 토큰 필요 없음)")
//    public ApiResponse<MemberResponseDTO.LoginJwtTokenDTO> login(@Valid @RequestBody MemberRequestDTO.MemberLoginDTO request) {
//        MemberResponseDTO.LoginJwtTokenDTO token = memberService.login(request);
//        return ApiResponse.onSuccess(token);
//    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인을 진행하는 API (토큰 방급; 액세스 토큰 필요 없음)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4005", description = "존재하지 않는 회원입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4006", description = "비밀번호가 일치하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<MemberResponseDTO.NewLoginJwtTokenDTO> login(@Valid @RequestBody MemberRequestDTO.MemberLoginDTO request) {
        MemberResponseDTO.NewLoginJwtTokenDTO token = memberService.newLogin(request);
        return ApiResponse.onSuccess(token);
    }

    @GetMapping("/test")
    public String loginTest() {
        return "login user";
    }

    // 보안 강화 시 주석 처리 해제
//    @PostMapping("/refresh")
//    @Operation(summary = "액세스 토큰과 리프레시 토큰 재발급 API", description = "리프레시 토큰으로 액세스 토큰과 리프레시 토큰을 재발급하는 API  (액세스 토큰 필요 없음)")
//    @Parameters({
//            @Parameter(name = "refreshToken", description = "리프레시 토큰")
//    })
//    public ApiResponse<MemberResponseDTO.LoginJwtTokenDTO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
//        MemberResponseDTO.LoginJwtTokenDTO tokens = memberService.reissue(refreshToken);
//        return ApiResponse.onSuccess(tokens);
//    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃을 진행하는 API (토큰 만료 및 블랙리스트화)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4022",description = "해당 토큰은 유효한 토큰이 아닙니다."),
    })
    public ApiResponse<MemberResponseDTO> logout(@RequestHeader("Authorization") String accessToken) {
        memberService.newLogout(accessToken.substring(7));
        return ApiResponse.onSuccess(null);
    }

    // 보안 강화 시 주석 처리 해제
//    @PostMapping("/logout")
//    @Operation(summary = "로그아웃 API", description = "로그아웃을 진행하는 API (토큰 만료 및 블랙리스트화)")
//    @Parameters({
//            @Parameter(name = "accessToken", description = "액세스 토큰"),
//            @Parameter(name = "refreshToken", description = "리프레시 토큰")
//    })
//    public ApiResponse<MemberResponseDTO> logout(@RequestParam("accessToken") String accessToken,
//                                                 @RequestParam("refreshToken") String refreshToken) {
//        memberService.logout(accessToken, refreshToken, "LOGOUT");
//        return ApiResponse.onSuccess(null);
//    }

    @PostMapping("/delete")
    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴를 진행하는 API (토큰 만료 및 회원 영구 삭제)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4005", description = "존재하지 않는 회원입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4022",description = "해당 토큰은 유효한 토큰이 아닙니다."),
    })
    public ApiResponse<MemberResponseDTO> deleteMember(@RequestHeader("Authorization") String accessToken,
                                                       @RequestParam String reason) {
        memberService.newWithdrawer(reason,accessToken.substring(7));
        memberService.newLogout(accessToken.substring(7));
        return ApiResponse.onSuccess(null);
    }

    // 보안 강화 시 주석 처리 해제
//    @PostMapping("/delete")
//    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴를 진행하는 API (토큰 만료 및 회원 영구 삭제)")
//    public ApiResponse<MemberResponseDTO> deleteMember(@Valid @RequestBody MemberRequestDTO.MemberWithdrawRequestDTO request) {
//        memberService.logout(request.getAccessToken(), request.getRefreshToken(), "DELETE");
//        memberService.withdrawer(request.getReason());
//        return ApiResponse.onSuccess(null);
//    }

    @GetMapping("/")
    @Operation(summary = "회원정보조회 API", description = "헤더에 있는 토큰으로 회원을 식별하고, 더보기 화면에서 회원의 정보를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<MemberResponseDTO.MemberDetailResultDTO> getMemberDetail(@AuthenticationPrincipal MemberDetail memberDetail) {
        String email = memberDetail.getMember().getEmail();
        return ApiResponse.onSuccess(memberService.getMemberDetail(email));
    }

    @GetMapping("/safebox")
    @Operation(summary = "회원 세이프박스 조회 API", description = "헤더에 있는 토큰으로 회원을 식별하고, 회원의 세이프박스 금액 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<MemberResponseDTO.MemberSafeBoxResultDTO> getMemberSafeBox(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long safeBox = memberDetail.getMember().getSafeBox();
        return ApiResponse.onSuccess(MemberConverter.toSafeBoxResultDTO(safeBox));
    }
}
