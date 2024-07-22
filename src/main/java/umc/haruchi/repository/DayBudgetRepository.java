package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.MonthBudget;

public interface DayBudgetRepository extends JpaRepository<DayBudget, Long> {
    DayBudget findByMonthBudgetAndDay(MonthBudget monthBudget, int day);
}
