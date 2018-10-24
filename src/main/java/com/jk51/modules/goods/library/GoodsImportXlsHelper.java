package com.jk51.modules.goods.library;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
public class GoodsImportXlsHelper {
    private static final Logger logger = LoggerFactory.getLogger(GoodsImportXlsHelper.class);
    private Workbook workbook;
    public boolean isOpen;

    public final static int MAX_NUM = 1000;

    public final static int STORAGE_MAX_NUM = 10000;
    final String SPLIT = "\t";

    final Map<String, String> titleOriginMap = new HashMap() {
        {
            // 药品类
            put("10", "商品编码\t批准文号\t通用名\t商品名\t规格\t生产企业\t品牌\t药品类别: 甲类非处方药，乙类非处方药，处方药，双轨药\t药品属性：化学药制剂，中成药，生物制品，抗生素，中药材，中药饮片，复方制剂，其它\t剂型: 片剂, 胶囊, 丸剂,颗粒,液体,软膏剂,贴剂,糖浆,散剂,栓剂,喷雾,溶液剂，乳剂，混悬剂，气雾剂，粉雾剂，洗剂，搽剂，糊剂，凝胶剂，滴眼剂，滴耳剂，眼膏剂，含漱剂，舌下片剂，粘贴剂，贴膜剂，滴剂，滴丸剂，芳香水剂，甘油剂，醑剂，注射剂，涂膜剂，合剂，混悬剂，酊剂，膜剂，糊剂，其它\t有效期(个月)\t使用方法: 口服, 外用, 注射, 含服, 其它\t适用人群：不限，成人，婴幼儿，儿童，男性，妇女，中老年\t是否医保：非医保，甲类医保，乙类医保\t医保编码\t商品条形码\t主要成分\t功能主治\t用法用量\t药理作用\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题\t标签\t现价（元）\t原价（元）\t成本价（元）不含运费\t库存(件)\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 其他类
            put("20", "商品编码\t批准文号\t商品名\t规格\t生产企业\t品牌\t保质期(个月)\t商品条形码\t主要成分\t功能介绍\t用法用量\t使用方法\t适用人群\t产品特色\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题（用户自定义）,字数限制在60-80\t标签\t现价（元）\t原价（元）\t成本价（元）\t库存(件)\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 保健品
            put("40", "商品编码\t批准文号\t商品名\t规格\t生产企业\t品牌\t剂型: 片剂, 胶囊, 丸剂,颗粒,液体,软膏剂,贴剂,糖浆,散剂,栓剂,喷雾,溶液剂，乳剂，混悬剂，气雾剂，粉雾剂，洗剂，搽剂，糊剂，凝胶剂，滴眼剂，滴耳剂，眼膏剂，含漱剂，舌下片剂，粘贴剂，贴膜剂，滴剂，滴丸剂，芳香水剂，甘油剂，醑剂，注射剂，涂膜剂，合剂，混悬剂，酊剂，膜剂，其它\t保质期(个月)\t商品条形码\t主要成分\t功能介绍\t用法用量\t使用方法\t适用人群\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题\t标签\t现价（元）\t原价（元）\t成本价（元）不含运费\t库存（件）\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 器械
            put("30", "商品编码\t批准文号\t商品名\t规格\t生产企业\t品牌\t类别:一类，二类，三类\t有效期(个月)\t商品条形码\t产品参数\t功能介绍\t用法用量\t使用方法\t适用人群\t产品特色\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题（长度不超过50中文字符）\t标签\t现价（元）\t原价（元）\t成本价（元）不含运费\t库存(件)\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 消毒
            put("80", "商品编码\t批准文号\t商品名\t规格\t生产企业\t品牌\t剂型: 片剂, 胶囊, 丸剂,颗粒,液体,软膏剂,贴剂,糖浆,散剂,栓剂,喷雾,溶液剂，乳剂，混悬剂，气雾剂，粉雾剂，洗剂，搽剂，糊剂，凝胶剂，滴眼剂，滴耳剂，眼膏剂，含漱剂，舌下片剂，粘贴剂，贴膜剂，滴剂，滴丸剂，芳香水剂，甘油剂，醑剂，注射剂，涂膜剂，合剂，混悬剂，酊剂，膜剂，其它\t保质期(个月)\t商品条形码\t主要成分\t功能介绍\t用法用量\t使用方法\t适用人群\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题\t标签\t现价（元）\t原价（元）\t成本价（元）\t库存（件）\t重量（克）\t限购数量：不限购，限购（件）\n");
            // 中药材
            put("70", "商品编码\t中药名\t别名\t规格\t产地分布\t是否方剂：非方剂，方剂\t药用部位：根茎类，茎木类，皮类，叶类，花类，全草类，果实种子类，矿物类，动物类，其它\t有效期(个月)\t主要成分\t功能主治\t临床应用\t使用方法\t药物性状\t药物形态\t性味归经\t注意事项\t禁忌\t贮藏\t商品分类\t商品标题\t标签\t现价（元）\t原价（元）\t成本价（元）不含运费\t库存（件）\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 化妆品
            put("60", "商品编码\t批准文号\t商品名\t规格\t生产企业\t品牌\t剂型: 片剂, 胶囊, 丸剂,颗粒,液体,软膏剂,贴剂,糖浆,散剂,栓剂,喷雾,溶液剂，乳剂，混悬剂，气雾剂，粉雾剂，洗剂，搽剂，糊剂，凝胶剂，滴眼剂，滴耳剂，眼膏剂，含漱剂，舌下片剂，粘贴剂，贴膜剂，滴剂，滴丸剂，芳香水剂，甘油剂，醑剂，注射剂，涂膜剂，合剂，混悬剂，酊剂，膜剂，其它\t保质期(个月)\t商品条形码\t主要成分\t功能介绍\t用法用量\t使用方法\t适用人群\t不良反应\t注意事项\t禁忌\t贮藏\t说明书\t商品分类\t商品标题\t标签\t现价（元）\t原价（元）\t成本价（元）不含运费\t库存(件)\t重量(克)\t限购数量：不限购，限购（件）\n");
            // 更新库存类
            put("110", "门店编码*\t商品编码*\t商品批号\t库存数量*\n");
            //更新商品的会员价格
            put("120", "商品编码*\t库存价格*（不能为0）\n");
            //更新门店商品的会员价格
            put("130","商品编码*\t门店编码\tERP价格*（价格不能为0）\n");
        }
    };

    public void open(String urlpath) {
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
//            conn.setRequestProperty("Range", "bytes=" + 2048 + "-");
            InputStream is = conn.getInputStream();
            // 1K的数据缓冲
//            byte[] bs = new byte[1024];
//            // 读取到的数据长度
//            int len;
//            /*ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = sra.getRequest();*/
//            String filename = "abc.xls";
////            applicationContext.getBean("")
//            // 输出的文件流
//            File file = new File(filename);
//            OutputStream os = new FileOutputStream(file);
//            // 开始读取
//            while ((len = is.read(bs)) != -1) {
//                os.write(bs, 0, len);
//            }
//            // 完毕，关闭所有链接
//            os.close();
//            is.close();
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //workbook = new HSSFWorkbook(is);
            workbook=this.createWorkbook(is);
            isOpen = true;

        } /*catch (MalformedURLException e) {
            logger.debug(e.getMessage());
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } */ catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
    ////读取Excel2003、Excel2007或更高级的兼容性问题
    public static Workbook createWorkbook(InputStream inp) throws IOException,InvalidFormatException {
// If clearly doesn't do mark/reset, wrap up
        if(! inp.markSupported()) {
            inp = new PushbackInputStream(inp, 8);
        }

        if(POIFSFileSystem.hasPOIFSHeader(inp)) {
            return new HSSFWorkbook(inp);
        }
        if(POIXMLDocument.hasOOXMLHeader(inp)) {
            return new XSSFWorkbook(OPCPackage.open(inp));
        }
        throw new IllegalArgumentException("导入Excel异常");
    }

    public Sheet getActiveSheet() {
        int activeSheetIndex = workbook.getActiveSheetIndex();
        return workbook.getSheetAt(activeSheetIndex);
    }


    /**
     * 跳过标题行遍历
     *
     * @param activeSheet
     */
    public Stream<Map> mapSkipTitle(Sheet activeSheet, Function<Row, Map> mapper, boolean parallel) {
        Iterator<Row> it = activeSheet.rowIterator();
        if (it.hasNext()) {
            // 跳过标题行
            it.next();
        }
        Iterable<Row> iterable = () -> it;
        // 跳过标题行 无序的并行处理
        return StreamSupport.stream(iterable.spliterator(), parallel).map(mapper);
    }

    public void checkFile(int detailTpl, String option) throws RuntimeException {
        if (!isOpen) {
            //
            throw new RuntimeException("打开文件失败");
        }

        Sheet activeSheet = getActiveSheet();
        if (activeSheet == null || activeSheet.getLastRowNum() < 1) {
            throw new RuntimeException("文件内容为空");
        }

        if (activeSheet.getLastRowNum() > MAX_NUM) {
            throw new RuntimeException("一次最多导入" + MAX_NUM + "条数据");
        }

        try {
            StringBuilder fieldSb = new StringBuilder();
            fieldSb.append(titleOriginMap.get(String.valueOf(detailTpl)));
            // 验证模板字段
            if (StringUtil.equalsIgnoreCase("update", option)) {
                // 更新模板最后多一个批号
                fieldSb.append(SPLIT + "商品批号");
                // 更新模板最后多一个购买方式
//                fieldSb.append(SPLIT + "购买方式");
            }
            String[] fields = StringUtil.split(fieldSb.toString(), SPLIT);
            Row titleRow = activeSheet.getRow(0);
            for (int i = 0, len = fields.length; i < len; i++) {
                String field = StringUtil.trimAll(fields[i]);
                Cell cell = titleRow.getCell(i);
                String xlsField = StringUtil.trimAll(cell.getStringCellValue());
                String xlsFieldPrefix = StringUtil.substring(xlsField, 0, StringUtil.length(field));
                if (!StringUtil.equals(field, xlsFieldPrefix)) {
                    logger.info("{} 模板字段{} 不匹配 {}", detailTpl, field, xlsField);
                    throw new RuntimeException("模板字段不匹配");
                }
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new RuntimeException("模板字段不匹配");
        }
    }

    public static <T> T getCellValueParse(Cell cell, Class<T> clazz) {
        String cellValue = "";
        try {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if ("java.lang.Integer".equals(clazz.getName())) {
                        cellValue = String.valueOf((int) cell.getNumericCellValue());
                    } else if ("java.lang.Long".equals(clazz.getName())) {
                        cellValue = String.valueOf((long) cell.getNumericCellValue());
                    } else {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    cellValue = cell.getBooleanCellValue() ? "1" : "0";
                    break;
                case BLANK:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.getStringCellValue();
                    break;
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        return JacksonUtils.getInstance().convertValue(cellValue, clazz);
    }

    public void checkFileFromStorage(int detailTpl, String option) throws RuntimeException {
        if (!isOpen) {
            //
            throw new RuntimeException("打开文件失败");
        }

        Sheet activeSheet = getActiveSheet();
        if (activeSheet == null || activeSheet.getLastRowNum() < 1) {
            throw new RuntimeException("文件内容为空");
        }
        logger.info("库存导入条数:{},xianzhi1:{}", activeSheet.getLastRowNum(), 10000);
        if (activeSheet.getLastRowNum() > 10000) {
            throw new RuntimeException("一次最多导入" + 10000 + "条数据");
        }
        try {
            StringBuilder fieldSb = new StringBuilder();
            fieldSb.append(titleOriginMap.get(String.valueOf(detailTpl)));
            // 验证模板字段
            String[] fields = StringUtil.split(fieldSb.toString(), SPLIT);
            Row titleRow = activeSheet.getRow(0);
            for (int i = 0, len = fields.length; i < len; i++) {
                String field = StringUtil.trimAll(fields[i]);
                Cell cell = titleRow.getCell(i);
                String xlsField = StringUtil.trimAll(cell.getStringCellValue());
                String xlsFieldPrefix = StringUtil.substring(xlsField, 0, StringUtil.length(field));
                if (!StringUtil.equals(field, xlsFieldPrefix)) {
                    logger.info("{} 模板字段{} 不匹配 {}", detailTpl, field, xlsField);
                    throw new RuntimeException("模板字段不匹配");
                }
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new RuntimeException("模板字段不匹配");
        }
    }

    public List<Sheet> getSheetList() {
        int num = workbook.getNumberOfSheets();
        List<Sheet> sheetList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            sheetList.add(workbook.getSheetAt(i - 1));
        }
        return sheetList;
    }

}
