package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;

import java.time.LocalDate;
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

    @Column(nullable = false, length = 5)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate lastClosing;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'LOGOUT'") // LOGIN, LOGOUT
//    private MemberStatus memberStatus;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long safeBox;

//    @Column(nullable = false, columnDefinition = "VARCHAR(5) DEFAULT 'USER'")
//    private String role; // USER, ADMIN -> 삭제할듯

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MonthBudget> monthBudgetList = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }

    public void addSafeBox(long safeBoxAmount) {
        if(safeBox == null) {
            safeBox = safeBoxAmount;
        }
        else
            safeBox += safeBoxAmount;
    }

    public void subSafeBox(long safeBoxAmount) {
        safeBox -= safeBoxAmount;
    }

    public void setLastClosing() {
        this.lastClosing = LocalDate.now();
    }
}
