package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class MonthBudgetHandler extends GeneralException {

    public MonthBudgetHandler(BaseErrorCode code) {
        super(code);
    }
}
