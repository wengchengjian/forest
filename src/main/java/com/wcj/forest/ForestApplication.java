package com.wcj.forest;

import com.wcj.forest.config.ForestProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:25
 */

@EnableScheduling
@EnableConfigurationProperties(ForestProperties.class)
@SpringBootApplication
public class ForestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForestApplication.class, args);
    }
}
