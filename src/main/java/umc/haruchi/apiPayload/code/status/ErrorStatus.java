package umc.haruchi.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //Member 관련 에러
    EXISTED_NAME(HttpStatus.BAD_REQUEST, "MEMBER4001", "이미 있는 닉네임입니다."),
    EXISTED_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4002", "이미 있는 이메일입니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4003", "이메일이 검증되지 않았습니다."),
    EMAIL_VERIFY_FAILED(HttpStatus.BAD_REQUEST, "MEMBER4004", "인증 번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4005", "없는 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "MEMBER4006", "비밀번호가 일치하지 않습니다."),

    // Token 관련 에러
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "MEMBER4020", "토큰이 비어있습니다."),
    WRONG_TYPE_SIGNATURE(HttpStatus.UNAUTHORIZED, "MEMBER4021", "잘못된 JWT 서명입니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4022", "해당 토큰은 유효한 토큰이 아닙니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "MEMBER4023", "토큰이 만료되었습니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4024", "지원되지 않는 JWT 토큰입니다."),
    EMPTY_CLAIMS_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4025", "JWT Claims 문자열이 비어있습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER4026", "올바르지 않은 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4027", "유효한 JWT 토큰이 없습니다."),

    ;
    //플젝 진행하며 추가하기..


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}