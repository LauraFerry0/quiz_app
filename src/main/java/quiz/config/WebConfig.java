package quiz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${music.folder.path}")
    private String musicFolderPath;

    @Value("${poster.folder.path}")
    private String posterFolderPath;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/css/**"}).addResourceLocations(new String[]{"classpath:/static/css/"}).setCachePeriod(0);
        registry.addResourceHandler(new String[]{"/js/**"}).addResourceLocations(new String[]{"classpath:/static/js/"}).setCachePeriod(0);
        registry.addResourceHandler(new String[]{"/posters/**"}).addResourceLocations(new String[]{"file:///" + posterFolderPath.replace("\\", "/") + "/"}).setCachePeriod(0);
        registry.addResourceHandler("/music/**")
                .addResourceLocations("file:///" + musicFolderPath.replace("\\", "/") + "/");

        registry.addResourceHandler(new String[]{"/profile-pics/**"}).addResourceLocations(new String[]{"file:/app/profile-pics/"});
    }
}
