package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
