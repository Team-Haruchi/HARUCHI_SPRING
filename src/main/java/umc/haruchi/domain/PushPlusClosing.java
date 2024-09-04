package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.RedistributionOption;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushPlusClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @Column(nullable = false)
    private boolean closingOption;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'DATE'")
    private RedistributionOption redistributionOption;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_day_budget_Id")
    private DayBudget sourceDayBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_day_budget_id")
    private DayBudget targetDayBudget;
}