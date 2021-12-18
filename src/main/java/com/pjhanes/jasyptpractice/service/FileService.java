package com.pjhanes.jasyptpractice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//File Service
@Service("fileService")
public class FileService {

    @Value("${test.encrypted.pw}")
    private String pw;


    public void displayPW() {
        System.out.println("This is the pw: " + pw);

    }
}
