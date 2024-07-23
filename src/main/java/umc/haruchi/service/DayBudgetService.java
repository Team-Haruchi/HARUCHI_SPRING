package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MemberHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DayBudgetService {

    @Autowired
    private MonthBudgetRepository monthBudgetRepository;

    @Autowired
    private DayBudgetRepository dayBudgetRepository;

    @Autowired
    private MemberRepository memberRepository;

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

    public List<Integer> findAllBudget(Long memberId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int lastDay = now.lengthOfMonth();

        if(memberRepository.findById(memberId).isEmpty()){
            throw new MemberHandler(ErrorStatus.NO_MEMBER_EXIST);
        }

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month);
        if(monthBudget == null){
            throw new MonthBudgetHandler(ErrorStatus.NOT_MONTH_BUDGET);
        }

        List<Integer> allBudget = new ArrayList<>();
        for(int i=day; i<=lastDay; i++){
            DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, i);
            if(dayBudget == null){
                throw new DayBudgetHandler(ErrorStatus.NOT_SOME_DAY_BUDGET);
            }
            allBudget.add(dayBudget.getDayBudget());
        }

        return allBudget;
    }
}
