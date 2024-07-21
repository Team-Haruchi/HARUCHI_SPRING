package umc.haruchi.config.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public static CorsConfigurationSource apiConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        ArrayList<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("*");

        ArrayList<String> allowedHttpMethods = new ArrayList<>();
        allowedHttpMethods.add("*");

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(allowedOriginPatterns);
        configuration.setAllowedMethods(allowedHttpMethods);
        //configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
