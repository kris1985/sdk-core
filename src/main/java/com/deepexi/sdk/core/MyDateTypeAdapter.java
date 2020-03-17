package com.deepexi.sdk.core;

import com.google.gson.*;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

/**
 * @author HuangTao
 * @version 1.0
 * @date 2020-03-17 12:14
 */

public class MyDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT1 = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_FORMAT2 = "MMM dd, yyyy HH:mm:ss a";

    @Override
    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(StringUtil.isBlank(json.getAsString())){
            return null;
        }
        if(json.getAsJsonPrimitive().isNumber()){
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }else {
            try {
                return   DateUtils.parseDate(json.getAsJsonPrimitive().getAsString(),
                        DATE_FORMAT,RFC822_DATE_FORMAT,DATE_FORMAT1,ALTERNATIVE_ISO8601_DATE_FORMAT
                        ,ISO8601_DATE_FORMAT,DATE_FORMAT2);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        String value = "";
        if(date != null){
            value = String.valueOf(date.getTime());
        }
        return new JsonPrimitive(value);
    }
}
