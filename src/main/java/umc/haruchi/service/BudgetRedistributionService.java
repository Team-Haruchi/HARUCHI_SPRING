package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.exception.handler.BudgetRedistributionHandler;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.*;
import umc.haruchi.repository.*;
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
    private final PullMinusClosingRepository pullMinusClosingRepository;
    private final MemberRepository memberRepository;

    LocalDate now = LocalDate.now(); // 현재 날짜 가져오기
    int year = now.getYear();
    int month = now.getMonthValue(); // 현재 월 가져오기
    int day = now.getDayOfMonth();   // 현재 일 가져오기

    //넘기기
    @Transactional
    public PushPlusClosing push(BudgetRedistributionRequestDTO.createPushDTO request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), year, month);
        DayBudget sourceBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getSourceDay());

        long totalAmount = request.getAmount();

        //target에 해당하는 daybudget 찾기
        DayBudget targetBudget = null;
        if (request.getTargetDay() != null) {
            targetBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget,request.getTargetDay());
        }

        if (request.getAmount() > sourceBudget.getDayBudget() && request.getAmount() < 0) {
            throw new BudgetRedistributionHandler(INVALID_AMOUNT_RANGE);
        }

        //고르게 넘기기
        if (request.getRedistributionOption().equals(EVENLY)) {

            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }
            //source 날짜 찾기
            long sourceDay = sourceBudget.getDay();

            //이번 달 남은 일 수 알아내기(본인 제외)
            long dayCount = monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day).count() - 1;

            // 금액 분배 계산
            long splitAmount = totalAmount / dayCount;

            //10의 자리 이하 절사
            Integer distributedAmount = (int) (roundDownToNearestHundred(splitAmount));

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);

            // 각 DayBudget에 분배
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day && !dayBudget.getDay().equals(sourceDay))
                    .forEach(dayBudget -> {
                        dayBudget.pushAmount(distributedAmount);
                    });
            // 세이프박스에 넣을 금액
            long safeBoxAmount = (long) (splitAmount % 100 * dayCount) + sourceBudget.getDayBudget() % 100;
            //source도 다시 한번 확인해서 절사
            sourceBudget.subAmount((int) (sourceBudget.getDayBudget() % 100));
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(DATE)) {
            // 특정 날짜로 넘기기

            if (targetBudget == null) {
                throw new BudgetRedistributionHandler(TARGET_IS_NULL);
            }
            // 10의 자리 이하 절사
            int tossAmount = (int) ((roundDownToNearestHundred(totalAmount))); // 넘겨줄 값

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // target에 amount 추가
            targetBudget.pushAmount(tossAmount);

            long safeBoxAmount = totalAmount - tossAmount + sourceBudget.getDayBudget() % 100;
            //source도 다시 한번 확인해서 절사
            sourceBudget.subAmount(sourceBudget.getDayBudget() % 100);
            // 세이프박스에 절사한 값 저장
            member.addSafeBox(safeBoxAmount);

        } else {
            // 세이프 박스에 넘기기
            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }
            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // 세이프박스에 저장
            member.addSafeBox(totalAmount + sourceBudget.getDayBudget() % 100);
            //source도 다시 한번 확인해서 절사
            sourceBudget.subAmount((int) (sourceBudget.getDayBudget() % 100));
        }

        PushPlusClosing pushPlusClosing = BudgetRedistributionConverter.toPushPlusClosing(request, sourceBudget, targetBudget);
        return pushPlusClosingRepository.save(pushPlusClosing); //나중에 공통으로 뺴기
    }

    //당겨쓰기
    @Transactional
    public PullMinusClosing pull(BudgetRedistributionRequestDTO.createPullDTO request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), year, month);
        DayBudget targetBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getTargetDay());
        long totalAmount = request.getAmount();
        int tossAmount = (int) (roundDownToNearestHundred(totalAmount));

        //source에 해당하는 daybudget 찾기
        DayBudget sourceBudget = null;
        if (request.getSourceDay() != null) {
            sourceBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getSourceDay());
        }

        if (request.getAmount() < 0) {
            throw new BudgetRedistributionHandler(INVALID_AMOUNT_RANGE);
        }

        //고르게 당겨쓰기
        if (request.getRedistributionOption().equals(EVENLY)) {
            if (sourceBudget != null) {
                throw new BudgetRedistributionHandler(SOURCE_MUST_NULL);
            }
            //target 날짜 찾기
            long targetDay = targetBudget.getDay();

            //이번 달 남은 일 수 알아내기
            long dayCount = monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day).count() - 1;

            // 금액 분배 계산
            long splitAmount = totalAmount / dayCount;

            //10의 자리 이하 절사
            int distributedAmount = (int) (roundDownToNearestHundred(splitAmount));

            // target amount에 더하기
            targetBudget.pushAmount((int) (distributedAmount * dayCount));

            // 각 DayBudget에 분배
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day && !dayBudget.getDay().equals(targetDay))
                    .forEach(dayBudget -> {
                        dayBudget.subAmount((int) splitAmount); //467,567 다양하게 있을 수 있음
                    });

            long safeBoxAmount = (splitAmount - distributedAmount) * dayCount +
                    monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day && !dayBudget.getDay().equals(targetDay))
                    .mapToLong(dayBudget -> dayBudget.getDayBudget() % 100) // 10 단위 이하
                    .sum();

            // sourceBudget에서 10 단위 이하 금액 절사
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= day && !dayBudget.getDay().equals(targetDay))
                    .forEach(dayBudget -> dayBudget.subAmount((int) (dayBudget.getDayBudget() % 100))); // 10 단위 이하 절사

            // 세이프박스에 넣기
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(DATE)) {
            // 특정 날 당겨쓰기

            if (sourceBudget == null) {
                throw new BudgetRedistributionHandler(SOURCE_IS_NULL);
            }

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // target에 amount 추가
            targetBudget.pushAmount(tossAmount);

            long safeBoxAmount = totalAmount - tossAmount + sourceBudget.getDayBudget() % 100;
            sourceBudget.subAmount((int) (sourceBudget.getDayBudget() % 100));

            // 세이프박스에 절사한 값 저장
            member.addSafeBox(safeBoxAmount);

        } else {
            // 세이프 박스에서 당겨쓰기
            if (sourceBudget != null) {
                throw new BudgetRedistributionHandler(SOURCE_MUST_NULL);
            }

            // 세이프박스에서 빼기
            member.subSafeBox(totalAmount);

            // target에 더하기
            targetBudget.pushAmount(tossAmount);

            member.addSafeBox(sourceBudget.getDayBudget() % 100);
            sourceBudget.subAmount((int) (sourceBudget.getDayBudget() % 100));
        }

        PullMinusClosing pullMinusClosing = BudgetRedistributionConverter.toPullMinusClosing(request, sourceBudget, targetBudget);
        return pullMinusClosingRepository.save(pullMinusClosing); //나중에 공통으로 뺴기
    }

    // 10의 자리 절사
    private long roundDownToNearestHundred(long amount) {
        return (amount / 100) * 100;
    }

}
