package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Income;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

    Income findByDayBudgetAndId(DayBudget dayBudget, Long memberId);
}
