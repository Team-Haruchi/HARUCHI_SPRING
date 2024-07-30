package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.MonthBudget;

public interface MonthBudgetRepository extends JpaRepository<MonthBudget, Long> {

    Optional<MonthBudget> findByMemberIdAndYearAndMonth(Long memberId, int year, int month);
}
