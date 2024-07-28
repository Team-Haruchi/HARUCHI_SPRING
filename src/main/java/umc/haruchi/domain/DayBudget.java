package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.DayBudgetStatus;
//import umc.haruchi.domain.mapping.BudgetRedistribution;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "budget_redistribution_id")
//    private BudgetRedistribution budgetRedistribution;

    @OneToMany(mappedBy = "budgetRedistribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PullMinusClosing> pullMinusClosings = new ArrayList<>();

    @OneToMany(mappedBy = "budgetRedistribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PushPlusClosing> pushPlusClosings = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_budget_id")
    private MonthBudget monthBudget;

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    //@Builder.Default
    private List<Income> incomeList = new ArrayList<>();

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    //@Builder.Default
    private List<Expenditure> expenditureList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDate now = LocalDate.now();
        this.day = now.getDayOfMonth();
    }

    public void setIncome(long amount, int how) {
        if(how == 0)
            DayBudget -= (int)amount;
        else
            DayBudget += (int)amount;
    }

    public void pushAmount(Integer distributedAmount) {
        DayBudget += distributedAmount;
    }

    public void subAmount(Integer tossAmount) {
        DayBudget -= tossAmount;
    }
}
