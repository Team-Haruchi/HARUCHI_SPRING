package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.RedistributionOption;
//import umc.haruchi.domain.mapping.BudgetRedistribution;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PullMinusClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean closingOption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'DATE'")
    private RedistributionOption redistributionOption;

//    @Column(nullable = true)
//    private LocalDate fromDate;
//
//    @Column(nullable = false)
//    private LocalDate toDate;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long amount;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "budget_redistribution_id")
//    private BudgetRedistribution budgetRedistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_day_budget_Id")
    private DayBudget sourceDayBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_day_budget_id")
    private DayBudget targetDayBudget;
}