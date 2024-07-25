package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.Withdrawer;

public interface WithdrawerRepository extends JpaRepository<Withdrawer, Long> {
}
