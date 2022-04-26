package com.example.librarysystemproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
@ServletComponentScan
public class LibrarySystemProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemProjectApplication.class, args);
        log.info("项目启动成功...");
    }
}
