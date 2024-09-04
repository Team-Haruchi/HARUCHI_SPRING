package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.PushPlusClosing;

import java.util.List;

public interface PushPlusClosingRepository extends JpaRepository<PushPlusClosing, Long> {
    List<PushPlusClosing> findBySourceDayBudgetAndClosingOptionIsFalse(DayBudget dayBudget);
    List<PushPlusClosing> findBySourceDayBudgetAndClosingOptionIsTrue(DayBudget dayBudget);
}
