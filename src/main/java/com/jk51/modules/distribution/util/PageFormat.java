package com.jk51.modules.distribution.util;

import com.jk51.modules.distribution.result.Page;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-17 13:21
 * 修改记录:
 */
public class PageFormat {

    /**
     * 分页基本参数处理
     * @param map
     * @return
     */
    public static Map<String,Object> pageFormat(Map<String,Object> map){

        map.put("pageIndex", ParamFormat.formatToInteger(map.get("pageIndex")));
        map.put("pageSize", ParamFormat.formatToInteger(map.get("pageSize")));

        try {
            map.put("rowsIndex" , ((Integer)map.get("pageIndex")-1) * (Integer)map.get("pageSize"));
        } catch (Exception e) {
            map.put("pageSize", Integer.valueOf(15));
            map.put("pageIndex", 1);
            map.put("rowsIndex" , 0);
        }

        return map;

    }

    /**
     * 封装分页数据
     * @param count
     * @param map
     * @param data
     * @return
     */
    public static Page createPage(Long count,Map<String,Object> map,Object data){
        Page page = new Page();
        /**
         * 封装分页数据
         */
        page.setCount(Long.valueOf(count));

        page.setPageIndex(Long.valueOf((Integer)map.get("pageIndex")));

        page.setNextPageIndex(Long.valueOf(((Integer)map.get("pageIndex")) + 1));

        page.setPrePageIndex(Long.valueOf(((Integer)map.get("pageIndex")) - 1));

        page.setPageSize(Long.valueOf((Integer)map.get("pageSize")));

        page.setRowsIndex(Long.valueOf((Integer)map.get("rowsIndex")));

        page.setPageCount((count-1)/page.getPageSize() + 1l);

        page.setData(data);

        return page;
    }

}
