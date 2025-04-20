package dn.tasktracker.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

public class CharArrayDeserializer extends JsonDeserializer<char[]> {

    @Override
    public char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        String value = p.getValueAsString();
        return value != null ? value.toCharArray() : new char[0];
    }

}
