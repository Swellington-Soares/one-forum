package br.one.forum.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
@NoArgsConstructor
public class UploadProperties {

    private String baseDir;
    private ImageProperties images = new ImageProperties();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ImageProperties {

        private String dir;
        private String maxSize;
        private String allowedTypes;
    }
}
