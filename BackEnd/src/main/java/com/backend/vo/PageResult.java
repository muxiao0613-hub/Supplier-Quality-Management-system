package com.backend.vo;

import lombok.Data;

@Data
public class PageResult<T> {
    private Long total;
    private Long current;
    private Long size;
    private java.util.List<T> records;

    public PageResult(Long total, Long current, Long size, java.util.List<T> records) {
        this.total = total;
        this.current = current;
        this.size = size;
        this.records = records;
    }
}
