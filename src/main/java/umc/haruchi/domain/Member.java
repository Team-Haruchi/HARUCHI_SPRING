package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.LoginStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
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

    @Column(nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 15)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'F'")
    private LoginStatus loginStatus;

    @Column(nullable = true)
    private LocalDate inactiveDate;

    @Column(nullable = false)
    private LocalDate lastLoginDate;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long safeBox;

    @OneToOne(mappedBy = "member")
    private MemberToken memberToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MonthBudget> monthBudgetList = new ArrayList<>();
}