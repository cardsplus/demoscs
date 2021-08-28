package esy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import esy.api.plan.AufgabeValue;
import esy.api.plan.ProjektValue;
import esy.api.team.NutzerValue;
import esy.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;

@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:endpoint.properties")
public class EndpointConfiguration {

    /**
     * Base path of the REST API requests.
     */
    static final String BASE = "/api";

    /**
     * CORS configuration for all REST API requests.
     * Same values must apply to Spring Security,
     * Spring Web AND Spring Data.
     */
    static final CorsConfiguration CORS = new CorsConfiguration();

    static {
        CORS.setAllowCredentials(true);
        CORS.addAllowedHeader("Authorization");
        CORS.addAllowedHeader("Content-Type");
        CORS.addAllowedHeader("Content-Length");
        CORS.addAllowedMethod("GET");
        CORS.addAllowedMethod("POST");
        CORS.addAllowedMethod("PUT");
        CORS.addAllowedMethod("DELETE");
        CORS.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://localhost",
                "https://localhost"));
        CORS.setMaxAge(3600L);
    }

    void applyCORS(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(CORS.getAllowCredentials())
                .allowedHeaders(CORS.getAllowedHeaders().toArray(String[]::new))
                .allowedMethods(CORS.getAllowedMethods().toArray(String[]::new))
                .allowedOriginPatterns(CORS.getAllowedOriginPatterns().toArray(String[]::new))
                .maxAge(CORS.getMaxAge());
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                applyCORS(registry);
            }
        };
    }

    /**
     * {@see <a href="https://spring.io/projects/spring-data-rest" />}
     * {@see <a href="https://stackoverflow.com/questions/44295231/how-to-set-default-value-of-exported-as-false-in-rest-resource-spring-data-rest" />}
     */
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {

            @Override
            public void configureJacksonObjectMapper(final ObjectMapper mapper) {
                // apply defaults
                JsonMapper.configure(mapper);
            }

            @Override
            public void configureRepositoryRestConfiguration(final RepositoryRestConfiguration configuration, final CorsRegistry registry) {
                // apply defaults
                configuration.setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED);
                configuration.setBasePath(BASE);
                // create JSON with content (not _embedded)
                configuration.setDefaultMediaType(MediaType.APPLICATION_JSON);
                configuration.useHalAsDefaultJsonMediaType(false);
                // expose value objects
                configuration.exposeIdsFor(AufgabeValue.class);
                configuration.exposeIdsFor(NutzerValue.class);
                configuration.exposeIdsFor(ProjektValue.class);
                // add CORS settings
                applyCORS(registry);
            }
        };
    }
}
