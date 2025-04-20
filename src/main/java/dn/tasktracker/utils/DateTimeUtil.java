package dn.tasktracker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd||HH:mm");
        private static final ObjectMapper objectMapper = new ObjectMapper();
        public static LocalDateTime parseDateTime(String jsonString, String fieldName) throws JsonProcessingException, JsonProcessingException {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            if (jsonNode.has(fieldName)) {
                String dateTimeStr = jsonNode.get(fieldName).asText();
                return LocalDateTime.parse(dateTimeStr, formatter);
            }
            return null;
        }
        public static String formatDateTime(LocalDateTime dateTime) {
            return dateTime != null ? dateTime.format(formatter) : null;
        }
    }


