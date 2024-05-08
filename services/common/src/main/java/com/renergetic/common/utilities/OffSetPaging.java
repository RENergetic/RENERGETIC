package com.renergetic.common.utilities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffSetPaging implements Pageable {
    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffSetPaging(long offset, int limit) {
        if(offset < 0)
            throw new IllegalArgumentException("Offset must not be less than zero.");
        if(limit < 0)
            throw new IllegalArgumentException("Limit must not be less than zero.");
        else {
            this.limit = limit;
            this.offset = offset;
        }
        this.sort= Sort.unsorted();
    }

    public OffSetPaging(long offset, int limit, Sort sort) {
        if(offset < 0)
            throw new IllegalArgumentException("Offset must not be less than zero.");
        if(limit < 0)
            throw new IllegalArgumentException("Limit must not be less than zero.");
        else {
            this.limit = limit;
            this.offset = offset;
        }
        if(sort==null)
            throw new NullPointerException("null sort argument");
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public Pageable first() {
        return new OffSetPaging(0, this.limit);
    }

    @Override
    public Pageable withPage(int i) {
        return null;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return new OffSetPaging(this.offset + this.limit, this.limit);
    }

    @Override
    public Pageable previousOrFirst() {
        if(this.offset-this.limit <= 0)
            return new OffSetPaging(0, this.limit);

        return new OffSetPaging(this.offset - this.limit, this.limit);
    }

    @Override
    public boolean hasPrevious() {
        return this.offset > 0;
    }
}
