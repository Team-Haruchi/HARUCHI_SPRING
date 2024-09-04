package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.MonthBudget;

import java.util.List;
import java.util.Optional;

public interface DayBudgetRepository extends JpaRepository<DayBudget, Long> {
    Optional<DayBudget> findByMonthBudgetAndDay(MonthBudget monthBudget, int day);
    List<DayBudget> findByMonthBudget(MonthBudget monthBudget);
}
