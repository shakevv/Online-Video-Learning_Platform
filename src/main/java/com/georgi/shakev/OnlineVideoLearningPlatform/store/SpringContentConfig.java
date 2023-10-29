package com.georgi.shakev.OnlineVideoLearningPlatform.store;

import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.content.fs.config.FilesystemStoreConverter;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Configuration
public class SpringContentConfig {

    public Converter<UUID, String> converter() {
        return new FilesystemStoreConverter<UUID, String>() {
            @Override
            public String convert(UUID source) {
                return String.format("/%s", source.toString().replaceAll("-", "/"));
            }
        };
    }

    @Bean
    public FilesystemStoreConfigurer configurer(){
        return new FilesystemStoreConfigurer() {
            @Override
            public void configureFilesystemStoreConverters(ConverterRegistry registry) {
                registry.addConverter(converter());
            }
        };
    }

    @Bean
    File filesystemRoot() {
        try{
            return Files.createTempDirectory("").toFile();
        } catch (IOException e) {
            return null;
        }
    }

    @Bean
    FileSystemResourceLoader fileSystemResourceLoader(){
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }
}
