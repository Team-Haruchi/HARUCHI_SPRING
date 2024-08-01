package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.IncomeHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Income;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.IncomeRepository;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;
import umc.haruchi.web.dto.DayBudgetRequestDTO;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static umc.haruchi.apiPayload.code.status.ErrorStatus.NOT_DAY_BUDGET;
import static umc.haruchi.apiPayload.code.status.ErrorStatus.NOT_SOME_DAY_BUDGET;

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

    public MonthBudget check(Long memberId){
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberHandler(ErrorStatus.NO_MEMBER_EXIST);
        }

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));
        if(monthBudget == null){
            throw new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND);
        }
        return monthBudget;
    }

    public Integer findDayBudget(Long memberId) {
        LocalDate now = LocalDate.now();
        int day = now.getDayOfMonth();

        MonthBudget monthBudget = check(memberId);

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(ErrorStatus.NOT_DAY_BUDGET));

        return dayBudget.getDayBudget();
    }

    public List<Integer> findAllBudget(Long memberId) {

        LocalDate now = LocalDate.now();
        int day = now.getDayOfMonth();
        int lastDay = now.lengthOfMonth();


        MonthBudget monthBudget = check(memberId);

        List<Integer> allBudget = new ArrayList<>();
        for(int i=day; i<=lastDay; i++){
            DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, i)
                    .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
            allBudget.add(dayBudget.getDayBudget());
        }

        return allBudget;
    }


    @Transactional
    public void deleteIncome(Long memberId, Long incomeId) {

        LocalDate now = LocalDate.now();
        int day = now.getDayOfMonth();

        MonthBudget monthBudget = check(memberId);

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(ErrorStatus.NOT_DAY_BUDGET));
        if(dayBudget.getDayBudgetStatus() == DayBudgetStatus.INACTIVE){
            throw new DayBudgetHandler(ErrorStatus.TODAY_CLOSED);
        }

        Income income = incomeRepository.findByDayBudgetAndId(dayBudget, incomeId);
        if(income == null){
            throw new IncomeHandler(ErrorStatus.INCOME_NOT_EXIST);
        }

        long amount = income.getIncomeAmount();
        dayBudget.setIncome(amount,0);
        dayBudgetRepository.save(dayBudget);

        incomeRepository.delete(income);
    }

    @Transactional
    public Income joinIncome(DayBudgetRequestDTO.createIncomeDTO request, Long memberId) {

        LocalDate now = LocalDate.now();
        int day = now.getDayOfMonth();

        MonthBudget monthBudget = check(memberId);

        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                .orElseThrow(() -> new DayBudgetHandler(NOT_DAY_BUDGET));

        Income newIncome = DayBudgetConverter.toIncome(request, dayBudget);

        long amount = newIncome.getIncomeAmount();
        dayBudget.setIncome(amount,1);
        dayBudgetRepository.save(dayBudget);

        return incomeRepository.save(newIncome);
    }
}

