package com.jk51.commons.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by think on 2017/2/17.
 */
public class ExcelObject {

    private String type;

    private final String TYPE_XLS="xls";
    private final String TYPE_XLSX="xlsx";
    private MultipartFile file;
    private ArrayList<ArrayList<Cell>> result;
    private ArrayList<Cell> row;
    HSSFWorkbook wb;
    XSSFWorkbook xwb;

    public ExcelObject(MultipartFile file){
        this.file=file;
        if(file.getOriginalFilename().endsWith(TYPE_XLSX))
            setType(TYPE_XLSX);
        if(file.getOriginalFilename().endsWith(TYPE_XLS))
            setType(TYPE_XLS);
        result=new ArrayList<>();
    }

    public ArrayList<ArrayList<Cell>> read()throws IOException{

        if(StringUtils.isBlank(type))
            throw new IOException("file is not found");

        try{
            switch (type){
                case TYPE_XLS:
                    readXls(file.getInputStream());
                    break;
                case TYPE_XLSX:
                    readXlsx(file.getInputStream());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            throw e;
        }

        return result;
    }

    private void readXls(InputStream is)throws IOException{
        wb = new HSSFWorkbook(is);
        HSSFSheet sheet=wb.getSheetAt(0);
        sheet.forEach(r->{
            addrow();
            r.forEach(cell -> {addcolumn(cell);});
        });
        addrow();
        is.close();
    }

    private void readXlsx(InputStream is)throws IOException{
        xwb = new XSSFWorkbook(is);

        XSSFSheet sheet=xwb.getSheetAt(0);

        sheet.forEach(r->{
            addrow();
            r.forEach(cell -> {addcolumn(cell);});
        });

        addrow();
        is.close();
    }

    private void addrow(){
        if(row!=null)
            result.add(row);
        row=new ArrayList<Cell>();
    }

    private void addcolumn(Cell value){
        row.add(value);
    }

    private String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }
}
