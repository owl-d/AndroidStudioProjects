package com.example.db_test;

public class info {
    String number;    // 글번호
    String content;    // 내용

    public info(String number, String content) {
        this.number = number;
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }
}