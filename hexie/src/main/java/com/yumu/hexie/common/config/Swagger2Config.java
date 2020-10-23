package com.yumu.hexie.common.config;

import java.io.UnsupportedEncodingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 在线文档生成,参考网址：https://hacpai.com/article/1534735914420
 * https://blog.csdn.net/sanyaoxu_2/article/details/80555328
 * 
 * @author davidhardosn
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Bean("swagger4sales")
	public Docket api4Sales() throws UnsupportedEncodingException {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Sales").apiInfo(apiInfo()).select()
				// 自行修改为自己的包路径
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any()).build();
	}
	
	@Bean("swagger4coupon")
	public Docket api4Coupon() throws UnsupportedEncodingException {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Coupon").apiInfo(apiInfo()).select()
				// 自行修改为自己的包路径
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any()).build();
	}


	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("模块接口").description("")// swagger UI接入教程
				// 服务条款网
				.termsOfServiceUrl("")
				// 版本号
				.version("1.0").build();
	}

}