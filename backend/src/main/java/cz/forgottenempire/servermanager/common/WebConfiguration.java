package cz.forgottenempire.servermanager.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new ReactResourceResolver();
        registry.addResourceHandler("/**")
                .resourceChain(true)
                .addResolver(resolver);
    }

    public static class ReactResourceResolver implements ResourceResolver {
        private static final String REACT_DIR = "/static/";
        private static final String REACT_STATIC_DIR = "assets";

        private static final Resource INDEX = new ClassPathResource(REACT_DIR + "index.html");
        private static final Set<String> ROOT_STATIC_FILES = Set.of("site.webmanifest", "browserconfig.xml");
        private static final Set<String> IMAGE_FORMATS = Set.of(".png", ".jpg", ".jpeg", ".webp", ".gif", ".svg", ".ico");

        @Override
        public Resource resolveResource(
                HttpServletRequest request, @NotNull String requestPath,
                @NotNull List<? extends Resource> locations, @NotNull ResourceResolverChain chain) {

            return resolve(requestPath);
        }

        @Override
        public String resolveUrlPath(
                @NotNull String resourcePath,
                @NotNull List<? extends Resource> locations,
                @NotNull ResourceResolverChain chain
        ) {
            Resource resolvedResource = resolve(resourcePath);
            if (resolvedResource == null) {
                return null;
            }
            try {
                return resolvedResource.getURL().toString();
            } catch (IOException e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath) {
            if (requestPath == null) {
                return null;
            }

            if (ROOT_STATIC_FILES.contains(requestPath) || requestPath.startsWith(REACT_STATIC_DIR) || isImage(requestPath)) {
                return new ClassPathResource(REACT_DIR + requestPath);
            } else {
                return INDEX;
            }
        }

        private boolean isImage(String requestPath) {
            String lowerCasePath = requestPath.toLowerCase();
            return IMAGE_FORMATS.stream()
                    .anyMatch(lowerCasePath::endsWith);
        }
    }
}
