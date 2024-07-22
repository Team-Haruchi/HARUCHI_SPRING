package umc.haruchi.config.login.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.haruchi.domain.Member;
import umc.haruchi.repository.MemberRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("loadUserByUsername 함수 실행");

        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
        return MemberDetail.createMemberDetail(member.get());
    }
}
