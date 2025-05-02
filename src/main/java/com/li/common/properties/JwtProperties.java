package com.li.common.properties;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "li.jwt")
@Data
public class JwtProperties {
    private String adminSecretKey;
    private String adminTokenName;
    private Long adminTtl;
    private String userSecretKey;
    private String userTokenName;
    private Long userTtl;
}
