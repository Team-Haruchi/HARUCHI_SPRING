package umc.haruchi.converter;

import umc.haruchi.web.dto.TempResponse;

public class TempConverter {
    public static TempResponse.TempTestDTO toTempTestDTO() {
        return TempResponse.TempTestDTO.builder()
                .testString("Test")
                .build();
    }
}
