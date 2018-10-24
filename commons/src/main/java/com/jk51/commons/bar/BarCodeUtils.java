package com.jk51.commons.bar;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.qr.QRCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.Hashtable;

/**
 * Created by hulan on 2017/1/16.
 */
public class BarCodeUtils {
    /***
     * zxing 生成条形码  不带数字（com.google.zxing）
     * @param content
     * @param width
     * @param height
     * @param imgPath
     */
    public static void encode(String content, int width, int height, String imgPath) throws Exception {
        int codeWidth = 3 +  //start guard
                (7 * 6) +  //left bars
                5 +   // middle guard
                (7 * 6) +  // right bars
                3;  // end guard
        codeWidth = Math.max(codeWidth, width);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.EAN_13, codeWidth, height, null);
        MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(new File(imgPath)));

    }

    /**
     * 生成文件  （EAN13）
     *
     * @param msg
     * @return
     */
    public static File generateFile_EAN13(String msg) throws Exception {
        String path = Instant.now().toEpochMilli() + CommonConstant.BARCODE_PNG;
        File file = new File("E://" + path);
        generateEAN_13(msg, new FileOutputStream(file));

        return file;
    }

    /**
     * 生成文件  （Code39）
     *
     * @param msg (长度不能大于12)
     * @return
     */
    public static File generateFile_Code39(String msg) throws Exception {
        String path = Instant.now().toEpochMilli() + CommonConstant.BARCODE_PNG;
        File file = new File("E://barcode" + path);
        generate_Code39(msg, new FileOutputStream(file));

        return file;
    }

    /**
     * 生成字节  （EAN13）
     *
     * @param msg （长度不能大于12）
     * @return
     */
    public static byte[] generateByte_EAN13(String msg) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generateEAN_13(msg, out);
        return out.toByteArray();
    }

    /**
     * 生成字节  （Code39）
     *
     * @param msg
     * @return
     */
    public static byte[] generateByte_Code39(String msg) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generate_Code39(msg, out);
        return out.toByteArray();
    }

    /**
     * 生成到流  Code39
     *
     * @param msg
     * @param out
     */
    public static void generate_Code39(String msg, OutputStream out) throws Exception {
        if (StringUtils.isEmpty(msg) || out == null) {
            return;
        }
        Code39Bean bean = new Code39Bean();

        //精细度
        final int dpi = 150;
        //moudle宽度
        final double moduleWidth = UnitConv.in2mm(1.0f / dpi);

        //配置对象
        bean.setModuleWidth(15.0);
        bean.setBarHeight(15.0);
        bean.setFontSize(4.0);
        bean.setQuietZone(10.0);
        bean.setModuleWidth(moduleWidth);
        bean.setWideFactor(3);

        bean.doQuietZone(false);
        String format = "image/png";

        //输出到流
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, format, dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        //生成条形码
        bean.generateBarcode(canvas, msg);

        //结束绘制
        canvas.finish();
    }

    /**
     * 生成到流  EAN_13
     *
     * @param msg (长度不能大于12)
     * @param out
     */
    public static void generateEAN_13(String msg, OutputStream out) throws Exception {
        if (StringUtils.isEmpty(msg) || out == null) {
            return;
        }

        EAN13Bean bean = new EAN13Bean();
        final int dpi = 150;
        bean.setModuleWidth(1.0);
        bean.setBarHeight(40.0);
        bean.setFontSize(10.0);
        bean.setQuietZone(10.0);
        bean.doQuietZone(true);

        //配置对象
        String format = "image/png";

        //输出到流
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, format, dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        //生成条形码
        bean.generateBarcode(canvas, msg);

        //结束绘制
        canvas.finish();

    }

    /**
     * 解析条形码
     *
     * @param file 条形码图片地址
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image = null;
        Result result = null;
        image = ImageIO.read(file);
        if (image == null) {
            System.out.println("the decode image may be not exit.");
        }
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CommonConstant.CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 解析条形码
     *
     * @param path 条形码图片地址
     * @return
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return QRCodeUtil.decode(new File(path));
    }
}
