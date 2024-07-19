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