package com.jinsi.housedemo.entity;

import lombok.Data;

@Data
public class DictDataVO {
    private String value;
    private String label;

    public DictDataVO(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
