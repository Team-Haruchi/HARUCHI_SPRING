package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.exception.handler.BudgetRedistributionHandler;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.*;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.MonthBudgetRepository;
import umc.haruchi.repository.PushPlusClosingRepository;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;

import java.time.LocalDate;

import static umc.haruchi.apiPayload.code.status.ErrorStatus.*;
import static umc.haruchi.domain.enums.RedistributionOption.DATE;
import static umc.haruchi.domain.enums.RedistributionOption.EVENLY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetRedistributionService {

    private final MonthBudgetRepository monthBudgetRepository;
    private final DayBudgetRepository dayBudgetRepository;
    private final PushPlusClosingRepository pushPlusClosingRepository;

    LocalDate now = LocalDate.now(); // 현재 날짜 가져오기
    int year = now.getYear();
    int month = now.getMonthValue(); // 현재 월 가져오기
    int day = now.getDayOfMonth();   // 현재 일 가져오기

    @Transactional
    public PushPlusClosing push(BudgetRedistributionRequestDTO.createPushDTO request, Member member) {


        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), year, month);

        //source에 해당하는 daybudget 찾기
        DayBudget sourceBudget = dayBudgetRepository.findById(request.getSourceId())
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));

        //target에 해당하는 daybudget 찾기
        DayBudget targetBudget = null;
        if (request.getTargetId() != null) {
            targetBudget = dayBudgetRepository.findById(Long.valueOf(request.getTargetId()))
                    .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        }

        if (request.getAmount() > sourceBudget.getDayBudget() && sourceBudget.getDayBudget() < 0) {
            throw new BudgetRedistributionHandler(INVALID_AMOUNT_RANGE);
        }

        //고르게 넘기기
        if (request.getRedistributionOption().equals(EVENLY)) {
            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }
            //source 날짜 찾기
            Integer fromDay = sourceBudget.getDay();

            //이번 달 남은 일 수 알아내기
            long dayCount = monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() > fromDay).count();

            // 금액 분배 계산
            long totalAmount = request.getAmount();
            long splitAmount = totalAmount / dayCount;

            //10의 자리 이하 절사
            Integer distributedAmount = (int) ((splitAmount / 100) * 100);

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);

            // 각 DayBudget에 분배
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() > fromDay)
                    .forEach(dayBudget -> {
                        dayBudget.pushAmount(distributedAmount);
                    });

            long safeBoxAmount = totalAmount - (distributedAmount * dayCount); // 세이프박스에 넣을 금액
            safeBoxAmount += (totalAmount - (totalAmount / 100) * 100);
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(DATE)) {
            // 특정 날짜로 넘기기

            if (targetBudget == null) {
                throw new BudgetRedistributionHandler(TARGET_NOT_NULL);
            }

            long totalAmount = request.getAmount();
            // 10의 자리 이하 절사
            Integer tossAmount = (int) ((totalAmount / 100) * 100); // 넘겨줄 값
            long safeBoxAmount = totalAmount - tossAmount; // 넘겨주고 남은 세이프 박스에 넣을 10의 자리 이하의 값
            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // target에 amount 추가
            targetBudget.pushAmount(tossAmount);
            // 세이프박스에 절사한 값 저장
            member.addSafeBox(safeBoxAmount);

        } else {
            // 세이프 박스에 넘기기
            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }

            long totalAmount = request.getAmount();
            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // 세이프박스에 저장
            member.addSafeBox(totalAmount);
        }
        PushPlusClosing pushPlusClosing = BudgetRedistributionConverter.toPushPlusClosing(request, sourceBudget, targetBudget);
        return pushPlusClosingRepository.save(pushPlusClosing); //나중에 공통으로 뺴기
    }
}
