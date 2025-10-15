/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import jakarta.servlet.Filter;

/**
 * Spring MVC 환경 설정
 *
 * @since 1.0.0-SNAPSHOT
 */
@Configuration
@EnableWebMvc
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class DefaultWebMvcConfigurer implements WebMvcConfigurer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DefaultWebMvcConfigurer.class);

    private static final boolean USE_TRAILING_SLASH_MATCH = true;

    private final WebMvcProperties webMvcProperties;

    private final WebProperties.Resources resourceProperties;

    /**
     * Spring MVC 환경 설정 생성자
     *
     * @param webProperties 환경변수의 spring.web 속성 정보
     */
    public DefaultWebMvcConfigurer(WebMvcProperties webMvcProperties, WebProperties webProperties) {
        this.webMvcProperties = webMvcProperties;
        this.resourceProperties = webProperties.getResources();
    }

    /**
     * RequestMapping 매칭 검사시, 요청 Url 의 마지막 slash 의 유무에 관계 없이 매칭한다.
     *
     * @param configurer Url 패턴 관리 객체
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(USE_TRAILING_SLASH_MATCH);
    }

    /**
     * static resources 경로 등록
     *
     * @param registry 리소스처리 관리 객체
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(this.webMvcProperties.getStaticPathPattern())
                .addResourceLocations(this.resourceProperties.getStaticLocations())
                .setCacheControl(this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl())
                .setUseLastModified(this.resourceProperties.getCache().isUseLastModified());
    }

    @Bean
    public FilterRegistrationBean<Filter> multipartFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MultipartFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> hiddenHttpMethodFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HiddenHttpMethodFilter());
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

}
