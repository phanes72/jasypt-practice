package com.pjhanes.jasyptpractice;

import com.pjhanes.jasyptpractice.configuration.AppConfig;
import com.pjhanes.jasyptpractice.service.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


@SpringBootApplication
public class JasyptPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JasyptPracticeApplication.class, args);
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        FileService fileService = (FileService) context.getBean("fileService");
        fileService.displayPW();
    }

}
