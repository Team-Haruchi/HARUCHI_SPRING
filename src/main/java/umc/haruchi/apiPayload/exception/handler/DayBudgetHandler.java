package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class DayBudgetHandler extends GeneralException {

    public DayBudgetHandler(BaseErrorCode code) {
        super(code);
    }
}
