package umc.haruchi.config.login.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.haruchi.domain.Member;
import umc.haruchi.repository.MemberRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email).orElse(null);

        if (email != null) {
            return new MemberDetail(member);
        }

        throw new UsernameNotFoundException("해당 회원을 찾을 수 없습니다.");
//        log.info(">>회원 정보 찾기, {}", email);
//
//        return memberRepository.findByEmail(email)
//                    .map(this::createUserDetails)
//                    .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다."));

    }

//    private UserDetails createUserDetails(Member member) {
//        return new MemberDetail(member);
//    }
}
