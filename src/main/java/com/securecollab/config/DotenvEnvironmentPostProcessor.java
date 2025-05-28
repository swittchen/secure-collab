package com.securecollab.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;
import java.util.stream.Collectors;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        Map<String, Object> envMap = dotenv.entries().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (existing, replacement) -> replacement
                ));


        MapPropertySource dotenvPropertySource = new MapPropertySource("dotenv", envMap);
        environment.getPropertySources().addFirst(dotenvPropertySource);
    }
}
