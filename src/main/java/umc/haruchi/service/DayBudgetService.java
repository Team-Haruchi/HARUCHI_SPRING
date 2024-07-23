package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.IncomeHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Income;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.IncomeRepository;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DayBudgetService {

    @Autowired
    private MonthBudgetRepository monthBudgetRepository;

    @Autowired
    private DayBudgetRepository dayBudgetRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IncomeRepository incomeRepository;


    @Transactional
    public Integer findDayBudget(Long memberId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberHandler(ErrorStatus.NO_MEMBER_EXIST);
        }

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month);
        if(monthBudget == null){
            throw new MonthBudgetHandler(ErrorStatus.NOT_MONTH_BUDGET);
        }

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day);
        if(dayBudget == null){
            throw new DayBudgetHandler(ErrorStatus.NOT_DAY_BUDGET);
        }

        return dayBudget.getDayBudget();
    }

    @Transactional
    public void deleteIncome(Long memberId, Long incomeId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberHandler(ErrorStatus.NO_MEMBER_EXIST);
        }

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month);
        if(monthBudget == null){
            throw new MonthBudgetHandler(ErrorStatus.NOT_MONTH_BUDGET);
        }

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day);
        if(dayBudget == null){
            throw new DayBudgetHandler(ErrorStatus.NOT_DAY_BUDGET);
        }

        Income income = incomeRepository.findByDayBudgetAndId(dayBudget, incomeId);
        if(income == null){
            throw new IncomeHandler(ErrorStatus.INCOME_NOT_EXIST);
        }

        long amount = income.getIncomeAmount();

        // 하루치 예산 삭제

        // 한달 예산 삭제

        incomeRepository.delete(income);

    }
}
