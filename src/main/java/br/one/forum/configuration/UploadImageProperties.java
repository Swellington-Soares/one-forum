package br.one.forum.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "upload.config")
@Getter @Setter
public class UploadImageProperties {

    private String storagePathBase = "upload/";
    private List<String> allowedExtensions = List.of("jpg", "png");
    private boolean autoProcess = true;

}
