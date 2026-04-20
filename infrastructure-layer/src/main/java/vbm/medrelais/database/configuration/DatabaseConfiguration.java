package vbm.medrelais.database.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "vbm.medrelais"
)
@EnableJpaAuditing
public class DatabaseConfiguration {
}
