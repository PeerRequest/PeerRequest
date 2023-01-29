package com.peerrequest.app;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web configuration to server the frontend as Single Page Application.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    public static final String BASE_API_PATH = "/api";

    static HashSet<String> findFileTypes(File[] files) {
        var result = new HashSet<String>();

        if (files == null) {
            return result;
        }

        for (final File f : files) {
            if (!f.exists()) {
                continue;
            }

            if (f.isDirectory()) {
                var set = findFileTypes(f.listFiles());
                result.addAll(set);
            } else {
                var splits = f.getName().split("\\.");
                result.add(splits[splits.length - 1]);
            }
        }

        return result;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // build filters for all static files for the frontend
        var fileFrontendMatchers =
            findFileTypes(new File("public").listFiles())
                .stream().map(extension -> "/**/*." + extension)
                .toList().toArray(new String[0]);

        if (fileFrontendMatchers.length == 0) {
            throw new RuntimeException("fileFrontendMatchers empty");
        }

        registry
            .addResourceHandler(fileFrontendMatchers)
            .setCachePeriod(0)
            .addResourceLocations("file:public/");

        registry.addResourceHandler("/", "/**")
            .setCachePeriod(0)
            .addResourceLocations("file:public/index.html")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(@NonNull String resourcePath, @NonNull Resource location)
                    throws IOException {
                    if (resourcePath.startsWith(BASE_API_PATH) || resourcePath.startsWith(BASE_API_PATH.substring(1))) {
                        return null;
                    }

                    return location.exists() && location.isReadable() ? location : null;
                }
            });
    }

}