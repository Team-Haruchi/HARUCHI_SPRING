package umc.haruchi.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.haruchi.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Income extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long incomeAmount;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long incomeCategory;

    @Column(nullable = true, length = 10)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_budget_id")
    private DayBudget dayBudget;

}
