package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(BaseErrorCode code) {
        super(code);
    }
}
