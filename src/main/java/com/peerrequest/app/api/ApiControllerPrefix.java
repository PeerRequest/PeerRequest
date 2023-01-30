package com.peerrequest.app.api;

import com.peerrequest.app.WebConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Puts every annotated controller under the /api path.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@RequestMapping(WebConfiguration.BASE_API_PATH)
public @interface ApiControllerPrefix {
    @SuppressWarnings("checkstyle:MissingJavadocMethod") @AliasFor(annotation = Component.class)
    String value() default "";
}