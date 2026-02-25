package com.aimarketplace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aimarketplace.mapper")
public class AiMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMarketplaceApplication.class, args);
    }
}
