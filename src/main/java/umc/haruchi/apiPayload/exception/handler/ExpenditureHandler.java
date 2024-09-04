package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class ExpenditureHandler extends GeneralException {

    public ExpenditureHandler(BaseErrorCode code) {
        super(code);
    }
}
