package umc.haruchi.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IncomeCategory {
    NONE("미분류"),
    ALLOWANCE("용돈"),
    SALARY("월급"),
    SIDELINE("부수입"),
    BONUS("상여"),
    INTEREST("금융소득"),
    OTHER("기타");

    private String krName;

    IncomeCategory(String krName) {
        this.krName = krName;
    }

    public String getKrName() {
        return krName;
    }

    public static IncomeCategory nameOf(String krName) {
        for (IncomeCategory category : IncomeCategory.values()) {
            if (category.getKrName().equals(krName)) {
                return category;
            }
        }
        return null;
    }
}
