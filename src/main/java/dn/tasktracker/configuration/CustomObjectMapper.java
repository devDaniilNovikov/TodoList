package dn.tasktracker.configuration;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomObjectMapper {

        public static ObjectMapper createObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SimpleModule module = new SimpleModule();
            module.addKeySerializer(Object.class, new NullKeySerializer());
            objectMapper.registerModule(module);




            return objectMapper;
        }

        static class NullKeySerializer extends StdSerializer<Object> {

            public NullKeySerializer() {
                super(Object.class);
            }

            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeFieldName("null"); // Заменяем null-ключ на строку "null"
            }
        }
    }

