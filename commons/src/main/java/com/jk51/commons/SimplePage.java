package com.jk51.commons;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class SimplePage<T> {
    private PageInfo<T> pageInfo;
    private static final ThreadLocal<Integer> realPageSize = new ThreadLocal<>();

    public SimplePage(List<T> list) {
        pageInfo = new PageInfo(list);
        int realPageSize = getLocalRealPageSize();
        pageInfo.setPageSize(realPageSize);
        if (list.size() > realPageSize) {
            pageInfo.setHasNextPage(true);
            list.remove(list.size() - 1);
        }
    }

    public PageInfo<T> getPageInfo() {
        return pageInfo;
    }

    public static <E> Page<E> startPage(int pageNum, int pageSize) {
        setLocalRealPageSize(pageSize);
        int offset = (pageNum - 1) * pageSize;
        return PageHelper.offsetPage(offset, pageSize + 1, false);
    }

    protected static void setLocalRealPageSize(int pageSize) {
        realPageSize.set(pageSize);
    }

    protected static int getLocalRealPageSize() {
        Integer pageSize = realPageSize.get();

        return pageSize == null ? 0 : pageSize;
    }
}
