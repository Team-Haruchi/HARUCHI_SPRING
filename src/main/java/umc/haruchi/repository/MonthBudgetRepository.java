package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.MonthBudget;

import java.util.Optional;

public interface MonthBudgetRepository extends JpaRepository<MonthBudget, Long> {

    MonthBudget findByMemberIdAndYearAndMonth(Long memberId, int year, int month);
}
