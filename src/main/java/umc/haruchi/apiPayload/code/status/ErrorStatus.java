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
    NO_MEMBER_EXIST(HttpStatus.NOT_FOUND, "MEMBER4005", "존재하지 않는 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "MEMBER4006", "비밀번호가 일치하지 않습니다."),
    WITHDRAWAL_MEMBER(HttpStatus.NOT_FOUND, "MEMBER4007", "탈퇴한 회원입니다."),

    // MonthBudget 관련 에러
    NOT_MONTH_BUDGET(HttpStatus.NOT_FOUND, "MONTHBUDGET4001", "한 달 예산이 존재하지 않습니다."),

    // DayBudget 관련 에러
    NOT_DAY_BUDGET(HttpStatus.NOT_FOUND, "DAYBUDGET4001", "하루 예산이 존재하지 않습니다."),
    NOT_SOME_DAY_BUDGET(HttpStatus.NOT_FOUND, "DAYBUDGET4002", "특정 날짜의 예산이 존재하지 않습니다."),
    TODAY_CLOSED(HttpStatus.BAD_REQUEST, "DAYBUDGET4003", "오늘 지출은 마감되었습니다."),

    // Redistribution 관련 에러
    TARGET_MUST_NULL(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4001", "타겟 날짜는 NULL 이어야 합니다."),
    TARGET_IS_NULL(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4002", "타겟 날짜가 존재하지 않습니다."),
    SOURCE_MUST_NULL(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4003", "소스 날짜는 NULL 이어야 합니다."),
    SOURCE_IS_NULL(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4004", "소스 날짜가 존재하지 않습니다."),
    INVALID_AMOUNT_RANGE(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4005", "입력된 금액이 해당 예산 범위를 초과하거나 유효하지 않습니다."),
    NO_REDISTRIBUTION_OPTION(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4006", "해당하는 재분배 옵션이 존재하지 않습니다."),
    LACK_OF_MONEY(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4007", "돈이 부족합니다."),
    OVER_OF_REMAINING_MONTH_BUDGET(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4008", "당기는 금액이 남은 한달 예산을 초과합니다."),
    FINAL_DAY(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4009", "마지막 날에는 해당 기능을 사용할 수 없습니다."),
    ZERO_AMOUNT(HttpStatus.BAD_REQUEST, "REDISTRIBUTION4010", "amount가 0일 때는 1/n을 할 수 없습니다."),

    // Income 관련 에러
    INCOME_NOT_EXIST(HttpStatus.NOT_FOUND, "INCOME4001", "해당 수입이 존재하지 않습니다."),


    // Token 관련 에러 - 수정...
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "MEMBER4020", "토큰이 비어있습니다."),
    WRONG_TYPE_SIGNATURE(HttpStatus.UNAUTHORIZED, "MEMBER4021", "잘못된 JWT 서명입니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4022", "해당 토큰은 유효한 토큰이 아닙니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "MEMBER4023", "토큰이 만료되었습니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4024", "지원되지 않는 JWT 토큰입니다."),
    EMPTY_CLAIMS_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER4025", "JWT Claims 문자열이 비어있습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER4026", "올바르지 않은 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4027", "유효한 JWT 토큰이 없습니다."),
    NO_MATCH_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "MEMBER4028", "일치하는 리프레시 토큰이 존재하지 않습니다."),
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