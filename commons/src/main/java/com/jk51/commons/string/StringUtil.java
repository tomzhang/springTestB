package com.jk51.commons.string;

import com.jk51.commons.json.JacksonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串处理工具类
 * Created  on 2017/1/20.
 *
 * @author wangzf
 */
public class StringUtil extends StringUtils {
    public static final Character TRUE_CHARACTER = '1';

    /**
     * 是否为真
     *
     * @param c 字符串
     * @return
     */
    public static boolean isTrue(Character c) {
        if (TRUE_CHARACTER.equals(c)) {
            return true;
        }
        return false;
    }

    /**
     * 是否为真
     *
     * @param s 字符
     * @return
     */
    public static boolean isTrue(String s) {
        if (TRUE_CHARACTER.equals(s)) {
            return true;
        }
        return false;
    }

    /**
     * 追加双引号
     *
     * @param str 字符
     * @return 追加后字符
     */
    public static String quote(String str) {
        return str == null ? null : (new StringBuilder('"')).append(str).append('"').toString();
    }

    /**
     * 追加单引号
     *
     * @param str 字符
     * @return 追加后字符
     */
    public static String singleQuote(String str) {
        return str == null ? null : (new StringBuilder("'")).append(str).append("'").toString();
    }


    /**
     * 替换文本
     *
     * @param text        处理文本
     * @param placeholder 占位符
     * @param parameters  替换参数
     * @return 替换后文本
     */
    public static String replace(String text, String placeholder, String[] parameters) {
        if (!StringUtils.isEmpty(text) && !StringUtils.isEmpty(placeholder) && parameters != null) {
            for (String parameter : parameters) {
                text = replaceOnce(text, placeholder, parameter);
            }
        }
        return text;
    }

    /**
     * 替换文本
     *
     * @param text        处理文本
     * @param placeholder 占位符
     * @param parameters  替换参数
     * @return 替换后文本
     */
    public static String replace(String text, String placeholder, List<String> parameters) {
        if (parameters != null && parameters.size() > 0) {
            return replace(text, placeholder, parameters.toArray(new String[parameters.size()]));
        }
        return text;
    }

    /**
     * @param str
     * @return
     */
    public static String replaceColonToDot(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        return str.replaceAll(":", ".");
    }


    /**
     * 取得指定长度的数字字符组合
     *
     * @param length 长度
     * @return 数字字符
     */
    public static String getRandomChar(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 解析参数
     *
     * @param str   字符串
     * @param regex 表达式
     * @return 解析后的参数列表
     * @throws Exception 异常
     */
    public static List<String> parseParams(String str, String regex) throws Exception {
        List<String> params = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            params.add(matcher.group());
        }
        return params;
    }

    public static boolean isIn(String target, String... strings) {
        for (String str : strings) {
            if (target == str) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (isEmpty(value.toString())) return true;
        return false;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 去除空格
     *
     * @param str str
     * @return 去除后字符
     */
    public static String trimAll(String str) {
        if (str != null) {
            str = StringUtils.replace(str, " ", StringUtils.EMPTY);
        }
        return str != null ? str.trim() : null;
    }

    /**
     * 是否为数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 数字字符串转换为int ，转换失败时抛出runTimeException
     *
     * @param str
     * @throws RuntimeException
     * @int
     */
    public static int convertToInt(String str) {

        if (!isNumber(str)) {
            throw new RuntimeException("字符串转换为Int异常");

        }
        return Integer.parseInt(str);

    }

    public static String getRandomStr(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    public static boolean isDouble(String str) {

        Pattern pattern = Pattern.compile("^[-\\+]?\\d+\\.\\d+$");
        return pattern.matcher(str).matches();

    }

    public static double convertToDouble(String str) {
        if (!isDouble(str)) {
            throw new RuntimeException("字符串转换为double异常");

        }
        return Double.parseDouble(str);
    }


    /**
     * 以reg 为分隔符，返回字符串List
     * 字符串为空时返回空数组
     */
    public static List<String> toList(String str, String reg) {
        List<String> arrayList = new ArrayList<String>();
        if (isEmpty(str)) {
            return arrayList;
        }
        String[] array = str.split(reg);
        if (array != null && array.length > 0) {
            arrayList = Arrays.asList(array);
        }
        return arrayList;
    }

    /**
     * 验证手机号码格式
     *
     * @param mobile
     * @return boolean
     */
    public static boolean isMobileNO(String mobile) {

        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        Pattern p = Pattern.compile("^[1][0-9]{10}$");//Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 使用正则对时间字符串的合法性进行校验
     *
     * @param dateStr 将要被校验合法性的时间字符串，格式：pattern
     *                <p>
     *                //日期格式yyyy
     *                PatternsDict.date_y= (\d{4});
     *                //日期格式yyyy-mm
     *                PatternsDict.date_ym= (\d{4})-(0\d{1}|1[0-2]);
     *                //日期格式yyyy-mm-dd
     *                PatternsDict.date_ymd= (\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01]);
     *                //时间格式hh
     *                PatternsDict.time_h=(0\d{1}|1\d{1}|2[0-3]);
     *                //时间格式hh:mm
     *                PatternsDict.time_hm=(0\d{1}|1\d{1}|2[0-3]):([0-5]\d{1});
     *                //时间格式hh:mm:ss
     *                PatternsDict.time_hms=(0\d{1}|1\d{1}|2[0-3]):[0-5]\d{1}:([0-5]\d{1});
     */
    public static boolean checkDateFormat(String dateStr, String patternStr) {

        if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(patternStr)) {
            return false;
        }
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(dateStr);
        return matcher.matches();
    }

    public static <R> List<R> split(String str, String separatorChars, Function<String, R> function) {
        if (isEmpty(str)) {
            return Collections.emptyList();
        }

        return Arrays.stream(split(str, separatorChars)).map(function).collect(Collectors.toList());
    }

    /**
     * 文本处理，将空格、逗号（中英文）、分号、换行，等替换成","后转成List
     *
     * @param src
     * @return
     */
    public static List<String> convertToList(String src) {
        if (StringUtil.isEmpty(src) || StringUtil.isEmpty(src)) {
            return new ArrayList<String>();
        }
        String result = src.replaceAll("，", ",").replaceAll("；", ",").replaceAll(";", ",").replaceAll(" ", "").replaceAll("\n", "");
        List<String> StrList = Arrays.stream(result.split(",")).collect(Collectors.toList());
        return StrList;
    }

    /**
     * 把字符串转换成数值List
     *
     * @param str
     * @return
     */
    public static Optional<List<Integer>> convertToIdsList(String str) {
        if (StringUtils.isBlank(str))
            return Optional.empty();

        List<Integer> list = null;
        if (str.startsWith("[")) {
            try {
                list = JacksonUtils.json2list(str, Integer.class);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            list = Arrays.stream(str.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        }

        if (list == null || list.size() == 0)
            return Optional.empty();
        else
            return Optional.of(list);
    }

    /**
     * @param ids
     * @param id
     * @return
     */
    public static String addIdSeparateWithComma(String ids, String id) {
        if (StringUtils.isBlank(id))
            throw new NullPointerException();

        return StringUtils.isBlank(ids) ? id : ids + "," + id;
    }
}
