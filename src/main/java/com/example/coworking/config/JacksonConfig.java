package com.example.coworking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // Custom serializer for Page to match frontend expectations
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
                return (Class<Page<?>>) (Class<?>) Page.class;
            }
        });

        objectMapper.registerModule(module);
        return objectMapper;
    }
}
