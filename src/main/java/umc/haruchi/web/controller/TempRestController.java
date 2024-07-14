package umc.haruchi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.web.dto.TempResponse;
import umc.haruchi.converter.TempConverter;

@RestController
@RequestMapping("/temp")
@RequiredArgsConstructor
public class TempRestController {
    @GetMapping("/test")
    //Service에 요청하지 않고, 성공 응답이 돌아왔다고 가정함
    public ApiResponse<TempResponse.TempTestDTO> testAPI() {
        return ApiResponse.onSuccess(TempConverter.toTempTestDTO());
    }
}
