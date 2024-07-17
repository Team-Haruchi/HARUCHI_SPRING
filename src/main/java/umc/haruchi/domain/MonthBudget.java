package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.haruchi.domain.common.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
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
    private List<DayBudget> dayBudgetList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDate now = LocalDate.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }


}