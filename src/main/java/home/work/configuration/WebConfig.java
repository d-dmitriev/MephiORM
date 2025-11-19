package home.work.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig {
    /** Список разрешённых источников (origins) для CORS. Берётся из application.yml. */
    @Value("${app.http.cors.origins:*}")
    List<String> httpCorsOrigins;

    /** Список разрешённых заголовков для CORS. Берётся из application.yml. */
    @Value("${app.http.cors.headers:*}")
    List<String> httpCorsHeaders;

    /** Список разрешённых HTTP-методов для CORS. Берётся из application.yml. */
    @Value("${app.http.cors.methods:*}")
    List<String> httpCorsMethods;

    /** Флаг, разрешены ли учётные данные (cookies, авторизационные заголовки) в CORS. Берётся из application.yml. */
    @Value("${app.http.cors.allow-credentials:false}") // <-- Добавлено
    boolean allowCredentials;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(httpCorsOrigins.toArray(new String[0]))
                        .allowedMethods(httpCorsMethods.toArray(new String[0]))
                        .allowedHeaders(httpCorsHeaders.toArray(new String[0]))
                        .allowCredentials(allowCredentials);
            }
        };
    }
}
