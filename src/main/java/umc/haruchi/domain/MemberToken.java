package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.haruchi.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String accessToken;

    @Column(nullable = false, length = 255)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
