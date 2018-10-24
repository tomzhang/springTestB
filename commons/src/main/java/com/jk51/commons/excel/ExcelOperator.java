package com.jk51.commons.excel;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导出Excel的工具类,该工具类提供Excel表格的常用操作操作，基于apache poi 组件开发
 *  @author wangzf
 */
public class ExcelOperator {

    public static List<HSSFCellStyle> createContentAreaStyles(int size){
        //声明一个工作簿
        HSSFWorkbook workbook =new HSSFWorkbook();
        List<HSSFCellStyle> styles=new ArrayList<HSSFCellStyle>();
        HSSFCellStyle style=workbook.createCellStyle();
        //生成一个字体
        HSSFFont font=workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);
        styles.add(style);
        return styles;
    }
    public static HSSFCellStyle createContentAreaStyle(HSSFWorkbook workbook){
        HSSFCellStyle style = workbook.createCellStyle();
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }
    public static Workbook getWorkbook(File file) throws IOException {
        String suffix = FileUtil.getSuffix(file);
        if(CommonConstant.FILE_SUFFIX_EXCEL_2003.equals(suffix)) return new HSSFWorkbook(new FileInputStream(file));
        else if(CommonConstant.FILE_SUFFIX_EXCEL_2007.equals(suffix)) return new XSSFWorkbook(new FileInputStream(file));
        else throw new IOException("无法解析的文件格式!");
    }
    public static HSSFWorkbook createWorkbook(String title,List<String> headers) throws IOException{
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index); //背景色

        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//对齐方式
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        //font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style.setFont(font);

        int rowIndex = 0;
        // 产生表格标题行
        HSSFRow row = sheet.createRow(rowIndex);
        rowIndex++;
        for (int i = 0; i < headers.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            String[] dbTableField = headers.get(i).split(CommonConstant.REGEXP_DOT);
            HSSFRichTextString text = new HSSFRichTextString(dbTableField[0]);//headers.get(i)
            cell.setCellValue(text);
        }
        return workbook;
    }
    /**
     * 向工作簿中写入数据
     * @param workbook 工作簿
     * @param headers 列名集合
     * @param rows 行数据集合
     *  rowIndex 从哪一行开始写行号(对于批量写入的时候用，一次性写入时填写1)
     * @throws IOException
     */
    public static void writeData(HSSFWorkbook workbook,List<String> headers,List<Map<String,Object>> rows,HSSFCellStyle dataCellStyle) throws IOException{
        HSSFSheet sheet = workbook.getSheetAt(0);
        //如果表格的第一位为空，则表示没有设置
        if(sheet.getRow(0) == null || sheet.getRow(0).getCell(0) == null){
            throw new IOException("未设置表头.");
        }
        //表格的风格取表头的风格
        for(Map<String,Object> rowMap:rows){
            HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int i = 0; i < headers.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(dataCellStyle);
                HSSFRichTextString text = null;
                if(rowMap.get(headers.get(i).toLowerCase()) == null){
                    text = new HSSFRichTextString(StringUtils.EMPTY);
                }else{
                    text = new HSSFRichTextString(rowMap.get(headers.get(i)).toString());
                }
                cell.setCellValue(text);
            }
        }
    }

    /**
     * 完成对Excel对象的标记
     * @param workbook 工作簿对象
     * @param out 文件输出流
     * @throws IOException
     */
    public static void finishEdit(HSSFWorkbook workbook,OutputStream out) throws IOException{
        workbook.write(out);
    }

    /**
     * 获得Excel文件中的sheet列表
     * @param fileName 文件名称
     * @param is 文件输入流
     * @return sheet列表
     * @throws IOException 解析文件时可能发生的异常
     */
    public static List<Sheet> getWorkSheets(String fileName,InputStream is) throws IOException {
        Workbook wb = null;
        try {
            String suffix = FileUtil.getSuffix(fileName);
            if(CommonConstant.FILE_SUFFIX_EXCEL_2003.equals(suffix)) wb = new HSSFWorkbook(is);
            else if(CommonConstant.FILE_SUFFIX_EXCEL_2007.equals(suffix)) wb = new XSSFWorkbook(is);
            else throw new Exception();
        } catch (Exception e) {
            throw new IOException("Excel格式错误或不能识别（只支持后缀为“.xls”或“.xlsx”的Excel文件）。");
        }
        List<Sheet> sheets = new ArrayList<Sheet>(wb.getNumberOfSheets());

        for(int i=0;i<wb.getNumberOfSheets();i++){
            sheets.add(wb.getSheetAt(i));
        }
        if (sheets.size() == 0)
            throw new IOException("Excel未包含有效的合法数据.");
        return sheets;
    }

    /**
     * 获得Excel文件中的sheet列表
     * @param fileName 文件名称
     * @param is
     * @return
     * @throws Exception
     */
    public static Sheet getWorkSheet(String fileName,InputStream is) throws Exception {
        return getWorkSheets(fileName, is).get(0);
    }

    /**
     * 获得Excel文件中的sheet列表
     * @param file excel文件
     * @return sheet列表
     * @throws IOException 解析文件时可能发生的异常
     */
    public static List<Sheet> getWorkSheets(File file) throws IOException {
        return getWorkSheets(file.getName(),new FileInputStream(file));
    }

    /**
     * 列索引转换成列号字母
     * @param i 列索引值 从0开始
     * @return
     */
    public static String intToExcelHeadString(int i){
        if(i < 26) return (char)(i+65) +"";
        int cycle = i/26;
        String str = "";
        for(int k=0;k<cycle;k++){
            str += "A";
        }
        int remainder = i%26;
        str += intToExcelHeadString(remainder);
        return str;
    }

}
