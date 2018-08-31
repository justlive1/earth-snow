/*
 * Copyright (C) 2018 justlive1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.common.web.base;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * web容器通用配置
 * 
 * @author wubo
 *
 */
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Primary
  @Bean
  ObjectMapper objectMapper() {

    ObjectMapper mapper = new ObjectMapper();
    // 使用JsonView处理某个具体请求时Pojo转换成Json时显示内容
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }

  @Primary
  @Bean
  public MappingJackson2HttpMessageConverter jsonConverter() {

    MappingJackson2HttpMessageConverter converter =
        new MappingJackson2HttpMessageConverter(objectMapper());
    converter.setSupportedMediaTypes(
        Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN));
    return converter;
  }

  @Bean
  public Jaxb2RootElementHttpMessageConverter xmlConverter() {
    Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_XML));
    return converter;
  }

  @Bean
  public HttpMessageConverter<String> stringConverter() {
    StringHttpMessageConverter converter = new StringHttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.ALL));
    return converter;
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jsonConverter());
    converters.add(stringConverter());
    converters.add(xmlConverter());
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {}

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {}

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {}

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {}

  @Override
  public void addFormatters(FormatterRegistry registry) {}

  @Override
  public void addInterceptors(InterceptorRegistry registry) {}

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {}

  @Override
  public void addCorsMappings(CorsRegistry registry) {}

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {}

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {}

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {}

  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {}

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {}

  @Override
  public void configureHandlerExceptionResolvers(
      List<HandlerExceptionResolver> exceptionResolvers) {}

  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {}

  @Override
  public Validator getValidator() {
    return null;
  }

  @Override
  public MessageCodesResolver getMessageCodesResolver() {
    return null;
  }
}
