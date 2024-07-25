package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class IncomeHandler extends GeneralException {
    public IncomeHandler(BaseErrorCode code) {
        super(code);
    }
}
