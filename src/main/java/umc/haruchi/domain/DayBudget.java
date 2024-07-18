package umc.haruchi.domain;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.domain.mapping.BudgetRedistribution;

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
public class DayBudget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer day;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer DayBudget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private DayBudgetStatus dayBudgetStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_redistribution_id")
    private BudgetRedistribution budgetRedistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_budget_id")
    private MonthBudget monthBudget;

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    private List<Income> incomeList = new ArrayList<>();

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    private List<Expenditure> expenditureList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDate now = LocalDate.now();
        this.day = now.getDayOfMonth();
    }
}
