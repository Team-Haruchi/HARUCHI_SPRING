package umc.haruchi.apiPayload.exception.handler;

import umc.haruchi.apiPayload.code.BaseErrorCode;
import umc.haruchi.apiPayload.exception.GeneralException;

public class BudgetRedistributionHandler extends GeneralException {

    public BudgetRedistributionHandler(BaseErrorCode code) {
        super(code);
    }
}
