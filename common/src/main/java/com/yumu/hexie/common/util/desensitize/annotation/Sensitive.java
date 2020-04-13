package com.yumu.hexie.common.util.desensitize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yumu.hexie.common.util.desensitize.enums.SensitiveType;
import com.yumu.hexie.common.util.desensitize.serialize.SensitiveSerialize;

/**
 * 敏感字段
 *
 * @author huym
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerialize.class)
public @interface Sensitive {
    SensitiveType value();
}
