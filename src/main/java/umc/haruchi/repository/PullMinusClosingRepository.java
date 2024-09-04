package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.PullMinusClosing;

import java.util.List;

public interface PullMinusClosingRepository extends JpaRepository<PullMinusClosing, Long> {
    List<PullMinusClosing> findByTargetDayBudgetAndClosingOptionIsFalse(DayBudget targetDayBudget);
    List<PullMinusClosing> findByTargetDayBudgetAndClosingOptionIsTrue(DayBudget targetDayBudget);
}
