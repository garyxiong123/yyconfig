 [社区](xxxxx) | [设计文档](doc/design.md) | [使用文档](doc/user_guide.md) | [安装文档](doc/install.md) | [本地启动](doc/releases.md) | [版本更新](doc/releases.md) | [开发计划](doc/plain.md) 

# Yyconfig - A Object Oriented configuration management system



Yyconfig（有鱼配置中心）是根据携程Apollo进行改造的，定制一般符合中小企业的配置中小服务（Apollo携程框架部门研发的分布式配置中心，能够集中化管理应用不同环境、不同集群的配置，配置修改后能够实时推送到应用端，并且具备规范的权限、流程治理等特性，适用于微服务配置管理场景。）
携程apollo的体系过于复杂和代码层面的不足， 所以进行针对性的改造

#演示环境（Demo）:

https://yyconfig-test.yofish.com/
账号/密码:apollo/apollo

#本地启动步骤:


#1: 环境准备
```
Maven3+
Jdk1.7+
Mysql5.6+
```


#2：配置中心搭建步骤:

##2.1: 方式1：源码编译方式搭建：
###配置文件位置：
#### /yy-config/yyconfig-main/src/main/resources/application-dev.yml

###配置项说明：

###配置中心启动：
项目编译打包后，可直接通过命令行启动；


/ 方式1：使用默认配置，mysql默认为本地地址；
java -jar yyconfig.jar
// 方式2：支持自定义 mysql 地址；
java -jar yyconfig.jar --spring.datasource.url=jdbc:mysql://127.0.0.1:3306/yyconf?Unicode=true&characterEncoding=UTF-8

方式2：Docker 镜像方式搭建：

下载镜像
// Docker地址：https://hub.docker.com/r/garyxiong/apollo-mini/
docker pull garyxiong/apollo-mini:test

创建容器并运行
docker run -p 8080:8080 -v /tmp:/data/applogs --name yyconfig  -d yy/yyconfig

**
* 如需自定义 mysql 等配置，可通过 "PARAMS" 指定，参数格式 RAMS="--key=value  --key2=value2" ；
* 配置项参考文件：
*/

docker run -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/yyconfig?Unicode=true&characterEncoding=UTF-8 --spring.datasource.username=zhangsan --spring.datasource.password=zhangsan " -p 8080:8080 -v /tmp:/data/applogs --name yyconfig  -d yyconfig/yyconfig


2.4 “接入yyconfig的示例项目” 项目配置
项目：yyconfig-sample-springboot
作用：接入接入yyconfig的示例项目的示例项目，供用户参考学习。这里以 springboot 版本进行介绍，其他版本可参考各自sample项目。

A、引入maven依赖
<!-- yyconfig-client -->
<dependency>
    <groupId>com.yyconfig</groupId>
    <artifactId>yyconfig-core</artifactId>
    <version>{最新稳定版}</version>
</dependency>



# Support
<table>
  <thead>
    <th>YyConfig配置中心技术支持<br />QQ号：502238410, 或者邮件联系 502238410@qq.com</th>
  </thead>
  <tbody>
  </tbody>
</table>

yyconfig配置中心交流群
二维码
群号:910528732

# License
The project is licensed under the [Apache 2 license](https://github.com/ctripcorp/apollo/blob/master/LICENSE).

# Known Users



