package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Expenditure;

import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {
    Expenditure findByDayBudgetAndId(DayBudget dayBudget, Long memberId);
    List<Expenditure> findByDayBudget(DayBudget dayBudget);
}
