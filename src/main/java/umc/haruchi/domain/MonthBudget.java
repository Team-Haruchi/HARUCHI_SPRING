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
public class MonthBudget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long monthBudget;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long usedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "monthBudget", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DayBudget> dayBudgetList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDate now = LocalDate.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }

    public void setMember(Member member) {
        if(this.member != null)
            member.getMonthBudgetList().remove(this);
        this.member = member;
        member.getMonthBudgetList().add(this);
    }

    public void updateMonthBudget(Long monthBudget) {
        LocalDate now = LocalDate.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
        this.monthBudget = monthBudget;
    }
}
