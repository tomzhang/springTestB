package com.jk51.commons.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OldStyle extends ResponseContent {
    public static String render(Object obj) {
        Map map = new HashMap();
        map.put("status", true);
        map.put("result", obj);

        return map2json(map);
    }

    public static String render(int code, String msg) {
        Map map = new HashMap();
        map.put("status", false);

        Map result = new HashMap();
        result.put("code", code);
        if (StringUtil.isNotEmpty(msg)) {
            result.put("msg", msg);
        }
        map.put("result", result);

        return map2json(map);
    }

    private static String map2json(Map map) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 输出json的时候转为下划线风格
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        // 中文的unicode
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //使Jackson JSON支持Unicode编码非ASCII字符
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new StringUnicodeSerializer());
        objectMapper.registerModule(module);
        //设置null值不参与序列化(字段不被显示)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //jackson 2.0 版本已经去掉CustomSerializerFactory类，上面可以替代功能原有功能，如果有问题，可以找 @hefang
//        CustomSerializerFactory serializerFactory= new CustomSerializerFactory();
//
//        serializerFactory.addSpecificMapping(String.class, new StringUnicodeSerializer());
//        objectMapper.setSerializerFactory(serializerFactory);
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class StringUnicodeSerializer extends JsonSerializer<String> {

        private final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
        private final int[] ESCAPE_CODES = CharTypes.get7BitOutputEscapes();

        private void writeUnicodeEscape(JsonGenerator gen, char c) throws IOException {
            gen.writeRaw('\\');
            gen.writeRaw('u');
            gen.writeRaw(HEX_CHARS[(c >> 12) & 0xF]);
            gen.writeRaw(HEX_CHARS[(c >> 8) & 0xF]);
            gen.writeRaw(HEX_CHARS[(c >> 4) & 0xF]);
            gen.writeRaw(HEX_CHARS[c & 0xF]);
        }

        private void writeShortEscape(JsonGenerator gen, char c) throws IOException {
            gen.writeRaw('\\');
            gen.writeRaw(c);
        }

        @Override
        public void serialize(String str, JsonGenerator gen,
                              SerializerProvider provider) throws IOException,
                JsonProcessingException {
            int status = ((JsonWriteContext) gen.getOutputContext()).writeValue();
            switch (status) {
                case JsonWriteContext.STATUS_OK_AFTER_COLON:
                    gen.writeRaw(':');
                    break;
                case JsonWriteContext.STATUS_OK_AFTER_COMMA:
                    gen.writeRaw(',');
                    break;
                case JsonWriteContext.STATUS_EXPECT_NAME:
                    throw new JsonGenerationException("Can not write string value here");
            }
            gen.writeRaw('"');//写入JSON中字符串的开头引号
            for (char c : str.toCharArray()) {
                if (c >= 0x80){
                    writeUnicodeEscape(gen, c); // 为所有非ASCII字符生成转义的unicode字符
                }else {
                    // 为ASCII字符中前128个字符使用转义的unicode字符
                    int code = (c < ESCAPE_CODES.length ? ESCAPE_CODES[c] : 0);
                    if (code == 0){
                        gen.writeRaw(c); // 此处不用转义
                    }else if (code < 0){
                        writeUnicodeEscape(gen, (char) (-code - 1)); // 通用转义字符
                    }else {
                        writeShortEscape(gen, (char) code); // 短转义字符 (\n \t ...)
                    }
                }
            }
            gen.writeRaw('"');//写入JSON中字符串的结束引号
        }

    }
}
