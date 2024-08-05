package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.haruchi.domain.common.BaseEntity;
import umc.haruchi.domain.enums.ClosingStatus;
import umc.haruchi.domain.enums.DayBudgetStatus;

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
    private Long day;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer dayBudget;

    @Enumerated(EnumType.STRING)
    private ClosingStatus closingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private DayBudgetStatus dayBudgetStatus;

    @OneToMany(mappedBy = "sourceDayBudget", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PullMinusClosing> sourcePullMinusClosings = new ArrayList<>();

    @OneToMany(mappedBy = "targetDayBudget", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PullMinusClosing> targetPullMinusClosings = new ArrayList<>();

    @OneToMany(mappedBy = "sourceDayBudget", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PushPlusClosing> sourcePushPlusClosings = new ArrayList<>();

    @OneToMany(mappedBy = "targetDayBudget", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PushPlusClosing> targetPushPlusClosings = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_budget_id")
    private MonthBudget monthBudget;

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    //@Builder.Default
    private List<Income> incomeList = new ArrayList<>();

    @OneToMany(mappedBy = "dayBudget", cascade = CascadeType.ALL)
    //@Builder.Default
    private List<Expenditure> expenditureList = new ArrayList<>();

    //@PrePersist
    public void prePersist() {
        LocalDate now = LocalDate.now();
        this.day = (long) now.getDayOfMonth();
    }

    public void setIncome(long amount, int how) {
        if(how == 0)
            dayBudget -= (int)amount;
        else
            dayBudget += (int)amount;
    }

    public void setStatus(DayBudgetStatus status) {
        dayBudgetStatus = status;
    }

    public void setDayBudget(int distributedAmount) {
        dayBudget = distributedAmount;
    }

    public void setExpenditure(long amount, int how) {
        if(how == 0)
            dayBudget += (int)amount;
        else
            dayBudget -= (int)amount;
    }

    public void pushAmount(Integer distributedAmount) {
        dayBudget += distributedAmount;
    }

    public void subAmount(Integer tossAmount) {
        dayBudget -= tossAmount;
    }

    public void changeDayBudgetStatus() {
        dayBudgetStatus = DayBudgetStatus.INACTIVE;
    }

    public void setClosingStatus(ClosingStatus closingStatus) {
        this.closingStatus = closingStatus;
    }
}
