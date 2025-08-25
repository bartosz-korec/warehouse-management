package com.bartoszkorec.warehouse_management.config;

import com.bartoszkorec.warehouse_management.utils.TrimmingStringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module stringTrimmingModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new TrimmingStringDeserializer());
        return module;
    }
}
