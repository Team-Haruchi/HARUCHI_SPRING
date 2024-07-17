package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.ClosingOption;
import umc.haruchi.domain.enums.RedistributionOption;
import umc.haruchi.domain.mapping.BudgetRedistribution;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushPlusClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    private ClosingOption closingOption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'DATE'")
    private RedistributionOption redistributionOption;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = true)
    private LocalDate toDate;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer amount;

    @Column(nullable = true, length = 10)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_redistribution_id")
    private BudgetRedistribution budgetRedistribution;
}