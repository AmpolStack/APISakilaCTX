package com.sakila.sakila_project.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourcesConfiguration {

    // Sakila Db datasource - in mariaDb
    @Primary
    @Bean("SakilaDS_prop")
    @ConfigurationProperties(prefix = "spring.datasource.sakila")
    public DataSourceProperties SKDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("SakilaDS")
    public DataSource SKdataSource(
            @Qualifier("SakilaDS_prop") DataSourceProperties dataSourcePropertie) {
        return dataSourcePropertie.initializeDataSourceBuilder().build();
    }


    // Jwt Db datasource - in mysql
    @Bean("TokensDS_prop")
    @ConfigurationProperties("spring.datasource.tokens")
    public DataSourceProperties TKDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("TokensDS")
    public DataSource TKDataSource(@Qualifier("TokensDS_prop") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}
