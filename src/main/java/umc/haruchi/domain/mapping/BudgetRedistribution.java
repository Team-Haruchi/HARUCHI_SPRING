package umc.haruchi.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.PullMinusClosing;
import umc.haruchi.domain.PushPlusClosing;
import umc.haruchi.domain.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BudgetRedistribution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer redistribution_amount;

    @OneToMany(mappedBy = "budgetRedistribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DayBudget> dayBudgetList = new ArrayList<>();

    @OneToMany(mappedBy = "budgetRedistribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PullMinusClosing> pullMinusClosings = new ArrayList<>();

    @OneToMany(mappedBy = "budgetRedistribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PushPlusClosing> pushPlusClosings = new ArrayList<>();
}