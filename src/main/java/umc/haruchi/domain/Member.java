package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.MemberStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long monthBudget;

    @Column(nullable = false, length = 5)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'LOGOUT'") // LOGIN, LOGOUT
    private MemberStatus memberStatus;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long safeBox;

    @OneToOne(mappedBy = "member")
    private MemberToken memberToken;

    @Column(nullable = false, columnDefinition = "VARCHAR(5) DEFAULT 'USER'")
    private String role; // USER, ADMIN -> 삭제할듯

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MonthBudget> monthBudgetList = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }

    public void setMemberStatusLogin() {
        this.memberStatus = MemberStatus.LOGIN;
    }

    public void setMemberStatusLogout() {
        this.memberStatus = MemberStatus.LOGOUT;
    }

}
