package com.example.coworking.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // Custom serializer for Page to match frontend expectations
        // Flattens the Page object so totalElements/totalPages are at root level, not nested
        module.addSerializer(new JsonSerializer<Page<?>>() {
            @Override
            public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeObjectField("content", page.getContent());
                gen.writeNumberField("totalElements", page.getTotalElements());
                gen.writeNumberField("totalPages", page.getTotalPages());
                gen.writeNumberField("size", page.getSize());
                gen.writeNumberField("number", page.getNumber());
                gen.writeBooleanField("first", page.isFirst());
                gen.writeBooleanField("last", page.isLast());
                gen.writeEndObject();
            }

            @Override
            public Class<Page<?>> handledType() {
                @SuppressWarnings("unchecked")
                Class<Page<?>> pageClass = (Class<Page<?>>) (Class<?>) Page.class;
                return pageClass;
            }
        });

        mapper.registerModule(module);
        return mapper;
    }
}