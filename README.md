
# 基础服务


## 介绍

- snow-common-base J2SE基础
- snow-common-web J2EE基础
- snow-common-web-vertx Vertx Web扩展
- snow-common-spring-rabbit spring-rabbit封装
- 依赖方式

```
<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-base</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-web</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-web-vertx</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-web-vertx</artifactId>
    <version>${snow.version}</version>
</dependency>

```


## 特性

* 基于properties配置进行项目定制
* 极简xml
* 自动装载 依赖jar配置properties无需额外配置

## 开发
	
	
	snow-common-base
		公共常量
		异常处理封装
		工具类
	snow-common-web
		web异常处理封装
		webmvc基本组件配置提供
		用户相关工具类
		公共对外rest接口提供
		
	snow-common-web-vertx
		扩展Route增加注解实现
		
	snow-common-spring-rabbit
		spring-rabbit的封装
		

## 部署
[Release](https://gitee.com/justlive1/earth-snow/releases)

