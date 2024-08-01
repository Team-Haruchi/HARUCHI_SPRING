package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {
    Expenditure findByDayBudgetAndId(DayBudget dayBudget, Long memberId);
}
