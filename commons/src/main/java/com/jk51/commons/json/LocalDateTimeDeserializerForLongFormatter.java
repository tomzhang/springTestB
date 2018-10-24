package com.jk51.commons.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.commons.java8datetime.Transform;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.jk51.commons.java8datetime.ParseAndFormat.longFormatter;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/14                                <br/>
 * 修改记录:                                         <br/>
 */
public class LocalDateTimeDeserializerForLongFormatter extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (p.getText().matches("^\\d*$")) {
            long longTime = Long.parseLong(p.getText());
            return Transform.uDateToLocalDateTime(new Date(longTime));
        } else {
            return LocalDateTime.parse(p.getText(), longFormatter);
        }
    }
}
