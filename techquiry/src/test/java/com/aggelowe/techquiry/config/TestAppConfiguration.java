package com.aggelowe.techquiry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.aggelowe.techquiry.helper.UserSessionHelper;
import com.aggelowe.techquiry.helper.UserSessionHelperTestImpl;

@Configuration
//@formatter:off
@ComponentScan(basePackages = { 
                "com.aggelowe.techquiry.service", 
                "com.aggelowe.techquiry.helper", 
                "com.aggelowe.techquiry.database", 
        })
//@formatter:on
@Import(TestSQLiteHikariConfig.class)
public class TestAppConfiguration {

    @Bean
    public UserSessionHelper userSessionHelper() {
        return new UserSessionHelperTestImpl();
    }

}