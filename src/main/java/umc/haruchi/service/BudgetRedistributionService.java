package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.exception.handler.BudgetRedistributionHandler;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.*;
import umc.haruchi.domain.enums.ClosingStatus;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.repository.*;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import java.time.LocalDate;
import java.util.List;

import static umc.haruchi.apiPayload.code.status.ErrorStatus.*;
import static umc.haruchi.domain.enums.RedistributionOption.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetRedistributionService {

    private final MonthBudgetRepository monthBudgetRepository;
    private final DayBudgetRepository dayBudgetRepository;
    private final PushPlusClosingRepository pushPlusClosingRepository;
    private final PullMinusClosingRepository pullMinusClosingRepository;
    private final MemberRepository memberRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenditureRepository expenditureRepository;

    //넘기기
    @Transactional
    public PushPlusClosing push(BudgetRedistributionRequestDTO.createPushDTO request, Long memberId) {
        LocalDate now = LocalDate.now(); // 현재 날짜 가져오기
        int localNowYear = now.getYear();
        int localNowMonth = now.getMonthValue(); // 현재 월 가져오기
        int localNowDay = now.getDayOfMonth();   // 현재 일 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), localNowYear, localNowMonth)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget sourceBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getSourceDay())
                .orElseThrow(() -> new DayBudgetHandler(NOT_DAY_BUDGET));
        long totalAmount = request.getAmount();
        //target에 해당하는 daybudget 찾기
        DayBudget targetBudget = null;
        if (request.getTargetDay() != null) {
            targetBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget,request.getTargetDay())
                    .orElseThrow(() -> new DayBudgetHandler(NOT_DAY_BUDGET));
        }

        if (request.getAmount() > sourceBudget.getDayBudget() && request.getAmount() < 0) {
            throw new BudgetRedistributionHandler(INVALID_AMOUNT_RANGE);
        }

        //이번 달 남은 일 수 알아내기(본인 제외)
        long dayCount = monthBudget.getDayBudgetList().stream()
                .filter(dayBudget -> dayBudget.getDay() >= localNowDay).count() - 1;

        //고르게 넘기기(233원씩 넘겨준다고하면 200원씩 분배하고 33원씩 * 일수 -> 세이프박스, 금액 차감된 source도 다시 확인 후 절사)
        if (request.getRedistributionOption().equals(EVENLY)) {

            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }
            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }
            if (sourceBudget.getDayBudget() < totalAmount){
                throw new BudgetRedistributionHandler(LACK_OF_MONEY);
            }
            //source 날짜 찾기
            long sourceDay = sourceBudget.getDay();

            // 금액 분배 계산
            long splitAmount = totalAmount / dayCount;

            //10의 자리 이하 절사
            Integer distributedAmount = (int) (roundDownToNearestHundred(splitAmount));

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);

            // 각 DayBudget에 분배
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(sourceDay))
                    .forEach(dayBudget -> {
                        dayBudget.pushAmount(distributedAmount);
                    });

            // 세이프박스에 넣을 금액(전체금액 - 분배된 총 금액, 음수에서 양수가 된 값들 절사해서 넣어줌
            long safeBoxAmount = totalAmount - distributedAmount * dayCount + sourceBudget.getDayBudget() % 100 + monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(sourceDay))
                    .mapToLong(dayBudget -> {
                        if (dayBudget.getDayBudget() > 0) {
                            return dayBudget.getDayBudget() % 100;
                        }
                        else {
                            return 0; //음수일 경우 절사 안함
                        }
                    })
                    .sum();

            // 절사한 값 반영
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(sourceDay))
                    .forEach(dayBudget -> {
                        if (dayBudget.getDayBudget() > 0) {
                            dayBudget.subAmount(dayBudget.getDayBudget() % 100);
                        }
                    });

            //source 절사 반영
            sourceBudget.subAmount(sourceBudget.getDayBudget() % 100);
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(DATE)) {

            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }

            if (targetBudget == null) {
                throw new BudgetRedistributionHandler(TARGET_IS_NULL);
            }
            if (sourceBudget.getDayBudget() < totalAmount){
                throw new BudgetRedistributionHandler(LACK_OF_MONEY);
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

        } else if (request.getRedistributionOption().equals(SAFEBOX)) {
            // 세이프 박스에 넘기기(233원을 넘겨준다고하면 233그대로 넘김, source도 다시 확인 후 절사(67원 세이프박스로))
            if (targetBudget != null) {
                throw new BudgetRedistributionHandler(TARGET_MUST_NULL);
            }
            if (sourceBudget.getDayBudget() < totalAmount){
                throw new BudgetRedistributionHandler(LACK_OF_MONEY);
            }
            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // 세이프박스에 저장
            member.addSafeBox(totalAmount + sourceBudget.getDayBudget() % 100);
            //source도 다시 한번 확인해서 절사
            sourceBudget.subAmount((int) (sourceBudget.getDayBudget() % 100));

        } else {
            throw new BudgetRedistributionHandler(NO_REDISTRIBUTION_OPTION);
        }

        PushPlusClosing pushPlusClosing = BudgetRedistributionConverter.toPush(request, sourceBudget, targetBudget);
        return pushPlusClosingRepository.save(pushPlusClosing);
    }

    //당겨쓰기
    @Transactional
    public PullMinusClosing pull(BudgetRedistributionRequestDTO.createPullDTO request, Long memberId) {
        LocalDate now = LocalDate.now(); // 현재 날짜 가져오기
        int localNowYear = now.getYear();
        int localNowMonth = now.getMonthValue(); // 현재 월 가져오기
        int localNowDay = now.getDayOfMonth();   // 현재 일 가져오기

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), localNowYear, localNowMonth)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));

        DayBudget targetBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getTargetDay())
                .orElseThrow(() -> new DayBudgetHandler(NOT_DAY_BUDGET));
        long totalAmount = request.getAmount();
        int tossAmount = (int) (roundDownToNearestHundred(totalAmount)); //233원-> 200원

        //당길 금액이 남은 한달 예산을 초과할 경우에 예외처리
        if(request.getAmount() > monthBudget.getMonthBudget() - monthBudget.getUsedAmount()) {
            throw new BudgetRedistributionHandler(OVER_OF_REMAINING_MONTH_BUDGET);
        }

        //source에 해당하는 daybudget 찾기
        DayBudget sourceBudget = null;
        if (request.getSourceDay() != null) {
            sourceBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getSourceDay())
                    .orElseThrow(() -> new DayBudgetHandler(NOT_DAY_BUDGET));
        }

        if (request.getAmount() < 0) {
            throw new BudgetRedistributionHandler(INVALID_AMOUNT_RANGE);
        }
        //이번 달 남은 일 수 알아내기
        long dayCount = monthBudget.getDayBudgetList().stream()
                .filter(dayBudget -> dayBudget.getDay() >= localNowDay).count() - 1;

        //0813 수정 : 233 * 일수 당겨와서 target에 저장, 233원씩 차감, 67원씩 세이프박스, target 절사 후 세이프박스
        if (request.getRedistributionOption().equals(EVENLY)) {

            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }

            if (sourceBudget != null) {
                throw new BudgetRedistributionHandler(SOURCE_MUST_NULL);
            }
            //target 날짜 찾기
            long targetDay = targetBudget.getDay();

            // 금액 분배 계산
            long splitAmount = totalAmount / dayCount;

            // target amount에 더하기 233 * 일수
            targetBudget.pushAmount((int) (splitAmount * dayCount));

            // 각 DayBudget에서 빼기
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(targetDay))
                    .forEach(dayBudget -> {
                        if(dayBudget.getDayBudget() < splitAmount) {
                            throw new BudgetRedistributionHandler(LACK_OF_MONEY);
                        }
                        dayBudget.subAmount((int) splitAmount); //467,567 다양하게 있을 수 있음
                    });

            //source day들에서 양수일때만 절사  -> 세이프박스
            long safeBoxAmount =
                    monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(targetDay))
                    .mapToLong(dayBudget -> {
                        if (dayBudget.getDayBudget() > 0) {
                            return dayBudget.getDayBudget() % 100;
                        }
                        return 0;
                    }) // 10 단위 이하
                    .sum();

            //target day가 음수에서 양수로 변했을 때의 값 -> 세이프박스
            if(targetBudget.getDayBudget() > 0) {
                safeBoxAmount += targetBudget.getDayBudget() % 100;
                targetBudget.subAmount(targetBudget.getDayBudget() % 100);
            }


            // 각 sourceday에서 양수일 때 10 단위 이하 금액 절사
            monthBudget.getDayBudgetList().stream()
                    .filter(dayBudget -> dayBudget.getDay() >= localNowDay && !dayBudget.getDay().equals(targetDay))
                    .forEach(dayBudget -> {
                        if (dayBudget.getDayBudget() > 0) {
                            dayBudget.subAmount(dayBudget.getDayBudget() % 100);
                        }
                    });

            // 세이프박스에 넣기
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(DATE)) {
            // 특정 날 당겨쓰기(233원 당겨온다고하면 200원을 당겨와서 target에 저장, 33원은 세이프박스, source에 67원도 절사해서 세이프박스 = 100))
            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }

            if (sourceBudget == null) {
                throw new BudgetRedistributionHandler(SOURCE_IS_NULL);
            }

            // source amount에서 뺴기
            sourceBudget.subAmount((int) totalAmount);
            // target에 amount 추가
            targetBudget.pushAmount(tossAmount);

            long safeBoxAmount = totalAmount - tossAmount + sourceBudget.getDayBudget() % 100;
            sourceBudget.subAmount(sourceBudget.getDayBudget() % 100);

            // 세이프박스에 절사한 값 저장
            member.addSafeBox(safeBoxAmount);

        } else if (request.getRedistributionOption().equals(SAFEBOX)) {
            // 세이프 박스에서 당겨쓰기(233원 당겨온다고하면 200원만 당겨와서 target에 저장, 세이프박스에서 200원 차감)
            if (sourceBudget != null) {
                throw new BudgetRedistributionHandler(SOURCE_MUST_NULL);
            }
            if (member.getSafeBox() < tossAmount){
                throw new BudgetRedistributionHandler(LACK_OF_MONEY);
            }
            // 세이프박스에서 빼기
            member.subSafeBox(tossAmount);

            // target에 더하기
            targetBudget.pushAmount(tossAmount);

        } else {
            throw new BudgetRedistributionHandler(NO_REDISTRIBUTION_OPTION);
        }

        PullMinusClosing pullMinusClosing = BudgetRedistributionConverter.toPull(request, sourceBudget, targetBudget);
        return pullMinusClosingRepository.save(pullMinusClosing);
    }

    // 10의 자리 절사
    private long roundDownToNearestHundred(long amount) {
        return (amount / 100) * 100;
    }

    //dayBudget이 양수인지 0인지 음수인지
    public boolean plusOrZeroOrMinus(BudgetRedistributionRequestDTO.createClosingDTO request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), request.getYear(), request.getMonth())
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getDay())
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        if(dayBudget.getDayBudget() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    //분배
    @Transactional
    public PushPlusClosing closingPlusOrZero(BudgetRedistributionRequestDTO.createClosingDTO request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), request.getYear(), request.getMonth())
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getDay())
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));

        //이미 마감된 날 에러처리
        if(dayBudget.getDayBudgetStatus().equals(DayBudgetStatus.INACTIVE)) {
            throw new DayBudgetHandler(TODAY_CLOSED);
        }

        //이번 달 남은 일 수 알아내기(본인 제외)
        long dayCount = monthBudget.getDayBudgetList().stream()
                .filter(budget -> budget.getDay() >= request.getDay()).count() - 1;

        long totalAmount = dayBudget.getDayBudget();

        if(dayBudget.getDayBudget() > 0) {
            dayBudget.setClosingStatus(ClosingStatus.PLUS);
        } else {
            dayBudget.setClosingStatus(ClosingStatus.ZERO);
        }

        //분배
        if (dayBudget.getDayBudget() > 0) {

            //고르게 넘기기(233원씩 넘겨준다고하면 200원씩 분배하고 33원씩 * 일수 -> 세이프박스)
            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }

            long splitAmount = totalAmount / dayCount;

            if (request.getRedistributionOption().equals(EVENLY)) {

                //10의 자리 이하 절사
                Integer distributedAmount = (int) (roundDownToNearestHundred(splitAmount));

                // 각 DayBudget에 분배
                monthBudget.getDayBudgetList().stream()
                        .filter(budget -> budget.getDay() > request.getDay())
                        .forEach(budget -> {
                            budget.pushAmount(distributedAmount);
                        });

                // 233 * 일 수를 daybudget에서 차감
                dayBudget.subAmount((int) (totalAmount * dayCount));

                //차감 되고 남은 dayBudget, 33원 * 일 수를 세이프박스로 + 딱 떨어지지 않아 남은 금액 + 음수에서 양수로 바뀐 날 절사한 값
                long safeBoxAmount = totalAmount % 100 * dayCount + dayBudget.getDayBudget() + monthBudget.getDayBudgetList().stream()
                        .filter(budget -> budget.getDay() > request.getDay())
                        .mapToLong(budget -> {
                            if (budget.getDayBudget() > 0) {
                                return budget.getDayBudget() % 100;
                            }
                            else {
                                return 0; //음수일 경우 절사 안함
                            }
                        }) // 10 단위 이하
                        .sum();;

                // 절사한 값 반영
                monthBudget.getDayBudgetList().stream()
                        .filter(budget -> budget.getDay() > request.getDay())
                        .forEach(budget -> {
                            if (budget.getDayBudget() > 0) {
                                budget.subAmount(budget.getDayBudget() % 100);
                            }
                        });
                member.addSafeBox(safeBoxAmount);

            } else if (request.getRedistributionOption().equals(SAFEBOX)) {
                // 세이프 박스에 넘기기(233원을 넘겨준다고하면 233그대로 넘김)
                // 세이프박스에 저장
                member.addSafeBox(totalAmount);
            }
        }

        dayBudget.subAmount(dayBudget.getDayBudget()); //dayBudget의 amount는 0으로
        PushPlusClosing pushPlusClosing = BudgetRedistributionConverter.toPlusClosing(request, dayBudget);
        dayBudget.changeDayBudgetStatus(); //INACTIVE로 변경

        member.setLastClosing();
        return pushPlusClosingRepository.save(pushPlusClosing);
    }

    // 차감
    @Transactional
    public PullMinusClosing closingMinus(BudgetRedistributionRequestDTO.createClosingDTO request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), request.getYear(), request.getMonth())
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, request.getDay())
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));

        //이미 마감된 날 에러처리
        if(dayBudget.getDayBudgetStatus().equals(DayBudgetStatus.INACTIVE)) {
            throw new DayBudgetHandler(TODAY_CLOSED);
        }
        //이번 달 남은 일 수 알아내기(본인 제외)
        long dayCount = monthBudget.getDayBudgetList().stream()
                .filter(budget -> budget.getDay() >= request.getDay()).count() - 1;

        long totalAmount = -1L * dayBudget.getDayBudget(); //양수로 변경

        long safeBoxAmount = 0;

        //고르게 당기기(=차감하기)(233원씩 차감한다고하면 233원씩 차감, 각 날짜별 10이하 절사)
        //당길때 남은 날들의 예산 총합 < 차감할 금액 이면 세이프박스에서 꺼내서 1/n , 그 후 차감하면 음수인 날이 생길 수 있음
        if (request.getRedistributionOption().equals(EVENLY)) {
            //dayCount가 0일때 -> 마지막 날일 때 예외처리
            if(dayCount == 0) {
                throw new BudgetRedistributionHandler(FINAL_DAY);
            }

            // 금액 분배 계산
            long splitAmount = totalAmount / dayCount;

            if(totalAmount > monthBudget.getDayBudgetList().stream()
                    .filter(budget -> budget.getDay() > request.getDay())
                    .mapToLong(budget -> budget.getDayBudget())
                    .sum()) {

                long safeBoxSplitAmount = member.getSafeBox() / dayCount; // 세이프박스에서 1/n 재분배할 금액 차감 233원씩 차감 -는 절사안하기 *

                member.subSafeBox(totalAmount - safeBoxSplitAmount * dayCount); // 세이프 박스 딱 떨어지지 않을 것을 대비

                //세이프박스 -> 재분배
                monthBudget.getDayBudgetList().stream()
                        .filter(budget -> budget.getDay() > request.getDay())
                        .forEach(budget -> budget.pushAmount((int) safeBoxSplitAmount));
            }

            // 각 DayBudget에서 차감
            monthBudget.getDayBudgetList().stream()
                    .filter(budget -> budget.getDay() > request.getDay())
                    .forEach(budget -> budget.subAmount((int) splitAmount)); //특정 일은 -가 될 수도 있음

            //절사해서 safebox
            safeBoxAmount = monthBudget.getDayBudgetList().stream()
                    .filter(budget -> budget.getDay() > request.getDay())
                    .mapToLong(budget -> {
                        if (budget.getDayBudget() > 0) {
                            return budget.getDayBudget() % 100;
                        }
                        else {
                            return 0; //음수일 경우 절사 안함
                        }
                    }) // 10 단위 이하
                    .sum();

            // 절사한 값 반영
            monthBudget.getDayBudgetList().stream()
                    .filter(budget -> budget.getDay() > request.getDay())
                    .forEach(budget -> {
                        if (budget.getDayBudget() > 0) {
                            budget.subAmount(budget.getDayBudget() % 100);
                        }
                    });

        } else if (request.getRedistributionOption().equals(SAFEBOX)) {
            if (member.getSafeBox() < totalAmount) {
                throw new BudgetRedistributionHandler(LACK_OF_MONEY);
            }
            // 세이프박스에서 차감
            member.subSafeBox(totalAmount);
        }
        dayBudget.setClosingStatus(ClosingStatus.MINUS);
        member.addSafeBox(safeBoxAmount);
        dayBudget.subAmount(dayBudget.getDayBudget()); // 양수일 때도 음수일 때도 0이 됨
        PullMinusClosing pullMinusClosing = BudgetRedistributionConverter.toMinusClosing(request, dayBudget);
        dayBudget.changeDayBudgetStatus(); //INACTIVE로 변경
        member.setLastClosing();
        return pullMinusClosingRepository.save(pullMinusClosing);
    }

    public Long calculatingAmount(int year, int month, int day, Long memberId) {

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));

        //이번 달 남은 일 수 알아내기(본인 제외)
        long dayCount = monthBudget.getDayBudgetList().stream()
                .filter(budget -> budget.getDay() >= day).count() - 1;

        //마지막 날이면 1/n 진행 x
        if(dayCount == 0) {
            throw new BudgetRedistributionHandler(FINAL_DAY);
        }

        long totalAmount = dayBudget.getDayBudget();

        if(totalAmount > 0) {
            return totalAmount / dayCount;
        }
        else if(totalAmount < 0) {
            totalAmount = -1 * totalAmount; //양수로 변경
            return totalAmount / dayCount;
        }
        else {
            //0이면 1/n 요청 들어올 수 x
            throw new BudgetRedistributionHandler(ZERO_AMOUNT);
        }
    }

    public List<Income> getIncomeList(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        return incomeRepository.findByDayBudget(dayBudget);
    }

    public List<Expenditure> getExpenditureList(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        return expenditureRepository.findByDayBudget(dayBudget);
    }

    public List<PullMinusClosing> getPullList(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        return pullMinusClosingRepository.findByTargetDayBudgetAndClosingOptionIsFalse(dayBudget);
    }

    public List<PushPlusClosing> getPushList(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        return pushPlusClosingRepository.findBySourceDayBudgetAndClosingOptionIsFalse(dayBudget);
    }

    public Integer getDayBudget(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        return dayBudget.getDayBudget();
    }

    public Long getTotalExpenditureAmount(int year, int month, int day, Long memberId) {
        List<Expenditure> expenditureList = getExpenditureList(year, month, day, memberId);
        long sum = expenditureList.stream().mapToLong(e -> e.getExpenditureAmount()).sum();
        return sum;
    }

    public Boolean closingCheck(int year, int month, int day, Long memberId) {
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(MONTH_BUDGET_NOT_FOUND));
        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
        if(!pullMinusClosingRepository.findByTargetDayBudgetAndClosingOptionIsTrue(dayBudget).isEmpty()) {
            return true;
        }
        else if(!pushPlusClosingRepository.findBySourceDayBudgetAndClosingOptionIsTrue(dayBudget).isEmpty()) {
            return true;
        }
        return false;
    }

    public LocalDate closingCheckLast(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(NO_MEMBER_EXIST)); //영속화
        LocalDate lastClosing = member.getLastClosing();
        if(lastClosing == null) {
            throw new BudgetRedistributionHandler(NOTHING_CLOSING);
        }
        return lastClosing;
    }
}
