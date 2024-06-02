package com.receipt2recipe.r2r.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.receipt2recipe.r2r.repository")
public class RepositoryConfig {
}
