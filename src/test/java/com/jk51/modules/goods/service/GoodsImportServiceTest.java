package com.jk51.modules.goods.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.request.BatchImportDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GoodsImportServiceTest {
    @Autowired
    GoodsImportService goodsImportService;
    @Autowired
    GoodsMapper goodsMapper;

    @Test
    public void batchImportTask() throws Exception {
        System.out.print(1);
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row title = sheet.createRow(0);
//        title.createCell()
    }

    @Test
    public void asyncImport() throws Exception {
        String json = "{\"fileurl\":\"http://localhost:8765/download/productImport/51jk_upload_1493004518641_ypl.xls\",\"use51\":true,\"siteId\":\"100030\",\"detailTpl\":\"0\",\"option\":\"update\"}";
        BatchImportDto bi = JacksonUtils.json2pojo(json, BatchImportDto.class);
        goodsImportService.batchImportTask(bi);
    }

    @Test
    public void test2() {
        // 查找商品
        Map selectParam = new HashMap();
        selectParam.put("approval_number", "QS粤XK16-204-02450");
        selectParam.put("specif_cation", "9.3*9.3*2.7cm");
        selectParam.put("goods_stats", "1,2");
        int count = goodsMapper.queryGoodsIdByCond(selectParam);
        System.out.println(count);
    }
}