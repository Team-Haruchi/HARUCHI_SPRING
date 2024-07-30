package umc.haruchi.converter;

import lombok.RequiredArgsConstructor;
import umc.haruchi.domain.Member;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class MemberConverter {

    public static Member toMember(MemberRequestDTO.MemberJoinDTO request) {
        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static MemberResponseDTO.MemberJoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.MemberJoinResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now()).build();
    }
}
