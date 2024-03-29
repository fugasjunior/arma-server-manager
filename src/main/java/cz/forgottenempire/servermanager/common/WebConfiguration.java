package cz.forgottenempire.servermanager.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{a:\\w+}")
                .setViewName("forward:/");
        registry.addViewController("/{a:[^(img)]}/{b:\\w+}")
                .setViewName("forward:/");
        registry.addViewController("/{a:\\w+}/{b:[^(img)]}{c:?!(|\\.js|\\.css)$}")
                .setViewName("forward:/");
    }
}
