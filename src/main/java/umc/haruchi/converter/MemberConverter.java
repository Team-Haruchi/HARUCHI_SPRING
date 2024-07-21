package umc.haruchi.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import umc.haruchi.domain.Member;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class MemberConverter {

//    private static final BCryptPasswordEncoder passwordEncoder;

    public static Member toMember(MemberRequestDTO.MemberJoinDTO request) {
        return Member.builder()
                .monthBudget(request.getMonthBudget())
                .name(request.getName())
                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    public static MemberResponseDTO.MemberJoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.MemberJoinResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now()).build();
    }
}
