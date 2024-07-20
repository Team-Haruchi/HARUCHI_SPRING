package umc.haruchi.config.auth;

import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.haruchi.domain.Member;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.apiPayload.code.status.ErrorStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(">>회원 정보 찾기, {}", email);

        try {
            return memberRepository.findByEmail(email)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new SystemException(String.format("%s %s", email, ErrorStatus.MEMBER_NOT_FOUND))
                    );
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    private UserDetails createUserDetails(Member member) {
        return new MemberDetail(member);
    }
}
