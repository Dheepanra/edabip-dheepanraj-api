package com.edabip.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    public PageResponse(Page<T> p) {
        this.content = p.getContent();
        this.page = p.getNumber();
        this.size = p.getSize();
        this.totalElements = p.getTotalElements();
        this.totalPages = p.getTotalPages();
        this.last = p.isLast();
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}
