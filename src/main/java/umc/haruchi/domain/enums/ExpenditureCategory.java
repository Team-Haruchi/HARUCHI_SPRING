package umc.haruchi.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExpenditureCategory {
    NONE("미분류"),
    FOOD("식비"),
    CAFE("카페"),
    TRANSPORT("교통"),
    HOBBY("취미"),
    FASHION("패션"),
    EDUCATION("교육"),
    EVENT("경조사"),
    SUBSCRIPTION("구독"),
    OTHER("기타")
    ;

    private String krName;

    ExpenditureCategory(String krName) {
        this.krName = krName;
    }

    public String getKrName() {
        return krName;
    }

    public static ExpenditureCategory nameOf(String krName) {
        for (ExpenditureCategory category : ExpenditureCategory.values()) {
            if (category.getKrName().equals(krName)) {
                return category;
            }
        }
        return null;
    }
}
