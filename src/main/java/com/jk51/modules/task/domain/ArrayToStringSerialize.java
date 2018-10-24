package com.jk51.modules.task.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.shiro.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

public class ArrayToStringSerialize extends JsonSerializer<Integer[]> {

    @Override
    public void serialize(Integer[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

        String stringValue = StringUtils.join(Arrays.asList(value).iterator(), ",");
        gen.writeString(stringValue);
    }
}
