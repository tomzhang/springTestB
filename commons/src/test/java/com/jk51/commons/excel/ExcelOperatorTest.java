package com.jk51.commons.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22.
 */
public class ExcelOperatorTest {
    public static void main(String[] args) throws Exception {
        //D:\stores.xlsx
        String ss="D:\\stores.xlsx";

        //Workbook workbook=ExcelOperator.getWorkbook(new File(ss));
        // System.out.println(sheets.size());
       /* Sheet sheet=  book.getSheet("ES");
        for(Iterator ite=sheet.rowIterator();ite.hasNext();) {
            Row row = (Row) ite.next();

            System.out.println();
            for (Iterator itet = row.cellIterator(); itet.hasNext(); ) {
                Cell cell=(Cell)itet.next();
                System.out.print(cell.getStringCellValue()+"\t");
            }
        }*/
          /*  Row row=sheet.getRow(0);
        Cell cell=row.getCell(1);
        System.out.println(cell.getStringCellValue());*/

       /* List<Sheet> sheets= ExcelOperator.getWorkSheets(new File(ss));
        for(Sheet sheet1:sheets){
            for (Iterator ite = sheet1.rowIterator(); ite.hasNext(); ) {
                Row row = (Row) ite.next();

                System.out.println();
                for (Iterator itet = row.cellIterator(); itet.hasNext(); ) {
                    Cell cell=(Cell)itet.next();
                    System.out.print(cell.getStringCellValue()+"\t");
                }
            }
        }*/
        String title="test";
        List<String> list=new ArrayList<String>();
        String a="AAA";
        String b="BBB";

        list.add(a);
        list.add(b);
        HSSFWorkbook hssfWorkbook=ExcelOperator.createWorkbook(title,list);
        FileOutputStream out=new FileOutputStream(ss);
        ExcelOperator.finishEdit(hssfWorkbook,out);
      /*  HSSFCellStyle dataCellStyle=ExcelOperator.createContentAreaStyle(hssfWorkbook);
        List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("1","11111111");
        map.put("2","22222222");
        map.put("3","33333333");
        maps.add(map);
        ExcelOperator.writeData(hssfWorkbook,list,maps,dataCellStyle);*/
    }
}
