package com.wcj.forest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:38
 */
@Data
@ConfigurationProperties(prefix = "forest")
public class ForestProperties {

    private Integer port;

}
