package umc.haruchi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.MemberStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false/*, length = 65*/)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'LOGOUT'")
    private MemberStatus memberStatus;

    @Column(nullable = true)
    private LocalDate inactiveDate;

    @Column(nullable = true)
    private LocalDateTime lastLoginDate;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long safeBox;

    @OneToOne(mappedBy = "member")
    private MemberToken memberToken;

    @Column(nullable = false, columnDefinition = "VARCHAR(5) DEFAULT 'USER'")
    private String role; // ROLE_USER, ROLE_ADMIN

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MonthBudget> monthBudgetList = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }

    public List<String> getRoleList() {
        if (!this.role.isEmpty()) {
            return Arrays.asList(this.role.split(","));
        }
        return new ArrayList<>();
    }
}
