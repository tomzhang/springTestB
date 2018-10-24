package com.jk51.modules.goods.library;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.goods.request.GoodsData;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.annotation.Repeat;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class GoodsImportPropertyTest {

    @Repeat(5)
    @Test
    public void conv2GoodsInfo() throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("ttt");
        Row row = sheet.createRow(0);
        List<String> xlsData = new ArrayList();
        xlsData.add("73821");
        xlsData.add("国药准字H20013294");
        xlsData.add("复方薄荷脑软膏");
        xlsData.add("复方薄荷脑软膏");
        xlsData.add("28克");
        xlsData.add("曼秀雷敦药业有限公司");
        xlsData.add("哈六");
        xlsData.add("甲类非处方药");
        xlsData.add("化学药制剂");
        xlsData.add("膏剂");
        xlsData.add("12");
        xlsData.add("外用");
        xlsData.add("成人，不限");
        xlsData.add("非医保");
        xlsData.add("属于医保的话，填入医保编码");
        xlsData.add("6917246200887");
        xlsData.add("本品为复方制剂，其组份为每瓶含：薄荷脑135mg、樟脑900mg、水杨酸甲酯、桉油、松节油、凡士林。");
        xlsData.add("本品用于伤风感冒所致的鼻塞，昆虫叮咬，皮肤皲裂，轻度烧烫伤，擦伤、晒伤及皮肤瘙痒。");
        xlsData.add("外用。伤风感冒涂于鼻下，昆虫叮咬，皮肤皲裂，轻度烧烫伤，擦伤和晒伤及皮肤瘙痒涂于患处。");
        xlsData.add("本品具有消炎、止痛、和止痒作用。");
        xlsData.add("偶见皮肤刺激症状");
        xlsData.add("本品仅限于外用2、若伤风");
        xlsData.add("尚不明确");
        xlsData.add("阴凉干燥处，密封保存");
        xlsData.add("【药品名称】");
        xlsData.add("激素类（填入商品属于哪个种类，如中药或医药等）");
        xlsData.add("曼秀雷敦薄荷膏28g ");
        xlsData.add("感冒，消炎，止痒");
        xlsData.add("20.01");
        xlsData.add("53");
        xlsData.add("10");
        xlsData.add("1000");
        xlsData.add("28");
        xlsData.add("不限购");

        for (int i = 0, len = xlsData.size(); i < len; i++) {
            row.createCell(i).setCellValue(xlsData.get(i));
        }

        Map goodsInfo = GoodsImportProperty.conv2GoodsInfo(row, 10);
        GoodsData goodsData = JacksonUtils.map2pojo(goodsInfo, GoodsData.class);
        System.out.println(goodsData);
    }

    @Test
    public void columnIndexFormString() throws Exception {
        int as = GoodsImportProperty.columnIndexFormString("A");
        assertEquals(as, 'A');
        as = GoodsImportProperty.columnIndexFormString("Z");
        assertEquals(as, 'Z');

        as = GoodsImportProperty.columnIndexFormString("AA");
        assertEquals(as, 'Z' + 1);
    }

    @Test
    public void test2() {
        // 验证模板字段
        String ypTitle = "商品编码	批准文号	通用名	商品名	规格	生产企业	品牌	药品类别: 甲类非处方药，乙类非处方药，处方药，双轨药	药品属性：化学药制剂，中成药，生物制品，抗生素，中药材，中药饮片，复方制剂，其它	剂型: 片剂, 胶囊, 丸剂,颗粒,液体,软膏剂,贴剂,糖浆,散剂,栓剂,喷雾,溶液剂，乳剂，混悬剂，气雾剂，粉雾剂，洗剂，搽剂，糊剂，凝胶剂，滴眼剂，滴耳剂，眼膏剂，含漱剂，舌下片剂，粘贴剂，贴膜剂，滴剂，滴丸剂，芳香水剂，甘油剂，醑剂，注射剂，涂膜剂，合剂，混悬剂，酊剂，膜剂，糊剂，其它	有效期(个月)	使用方法: 口服, 外用, 注射, 含服, 其它	适用人群：不限，成人，婴幼儿，儿童，男性，妇女，中老年	是否医保：非医保，甲类医保，乙类医保	医保编码	商品条形码	主要成分	功能主治	用法用量	药理作用	不良反应	注意事项	禁忌	贮藏	说明书	商品分类	商品标题	标签	现价（元）	原价（元）	成本价（元）不含运费	库存(件)	重量(克)	限购数量：不限购，限购（件）";
        String[] fields = StringUtil.split(ypTitle, "\t");

        System.out.println(String.format("%d,%d,%s", 1, 1111111111111111111l, "3"));
        System.out.println(fields);
    }

    @Test
    public void testListToMap() {
        Map a = new HashMap();
        a.put("a", "a");
        Map b = new HashMap();
        b.put("a", "b");
        Map c = new HashMap();
        c.put("a", "c");
        Map d = new HashMap();
        d.put("a", "d");

        List<Map> list = new ArrayList();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        Map t = list.parallelStream().collect(Collectors.toMap(m -> m.get("a"), Function.identity(), (o, n) -> o));
        System.out.print(t);
    }

    @Test
    public void testMemUsage() throws InterruptedException {
        List list = new ArrayList();
        for (int i = 0; i < 5000; i++) {
            list.add(new String());
        }
        long start = System.currentTimeMillis();
        Iterator it = list.iterator();
        it.next();
        Iterable<String> iterable= () -> it;
        List a = StreamSupport.stream(iterable.spliterator(), true).unordered().map(row -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap abc = new HashMap<>();
            for (int i = 0; i < 20; i++) {
                abc.put(new Random().nextInt(), "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
            }
            return abc;
        }).collect(Collectors.toList());
        long end = System.currentTimeMillis();

        System.out.printf("总共运行时间 %d\n", end - start);
    }
}