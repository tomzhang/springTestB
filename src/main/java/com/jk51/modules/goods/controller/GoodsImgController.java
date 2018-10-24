package com.jk51.modules.goods.controller;

import com.jk51.modules.goods.service.YbGoodsSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods/img")
public class GoodsImgController {
    @Autowired
    YbGoodsSyncService ybGoodsSyncService;
    /**
     * 添加商品图片
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String create() {
        return "todo";
    }

    /**
     * 删除商品图片
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete() {
        return "todo";
    }

    /**
     * 设置商品主图
     */
    @RequestMapping(value = "default", method = RequestMethod.POST)
    public String setDefault() {
        return "todo";
    }

    @RequestMapping(value = "queryByHash")
    public String queryByHash(@RequestParam(name = "hash") String hash) {
        return ybGoodsSyncService.queryByHash(hash);
    }

}
