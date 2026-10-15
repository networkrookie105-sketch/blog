package com.rookie.common;

import com.rookie.common.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RookieCommonApplicationTests {
    @Test
    void preservesApiDateFormatWithBootManagedJackson() throws Exception {
        ObjectMapper mapper = new JacksonConfig().objectMapper();
        LocalDateTime date = LocalDateTime.of(2026, 10, 12, 12, 30);
        assertEquals("\"2026-10-12 12:30:00\"", mapper.writeValueAsString(date));
        assertEquals(date, mapper.readValue("\"2026-10-12 12:30:00\"", LocalDateTime.class));
    }
}
