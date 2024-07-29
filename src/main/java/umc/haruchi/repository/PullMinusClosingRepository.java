package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.PullMinusClosing;
import umc.haruchi.domain.PushPlusClosing;

public interface PullMinusClosingRepository extends JpaRepository<PullMinusClosing, Long> {
}
