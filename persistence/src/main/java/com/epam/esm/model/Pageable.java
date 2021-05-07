package com.epam.esm.model;

import lombok.Data;

@Data
public class Pageable {
    private static final Integer DEFAULT_PAGE = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final int ONE = 1;

    private Integer page;
    private Integer size;

    public Pageable(Integer page, Integer size) {
        setPageIfExistOrDefaultPage(page);
        setSizeIfExistOrDefaultSize(size);
    }

    public Pageable(Integer page) {
        setPageIfExistOrDefaultPage(page);
        this.size = DEFAULT_PAGE_SIZE;
    }

    public Pageable() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_PAGE_SIZE;
    }

    public void setPageIfExistOrDefaultPage(Integer page) {
        this.page = page == null ? DEFAULT_PAGE : page;
    }

    public void setSizeIfExistOrDefaultSize(Integer size) {
        this.size = size == null ? DEFAULT_PAGE_SIZE : size;
    }

    public int getOffset() {
        return (page - ONE) * size;
    }
}
