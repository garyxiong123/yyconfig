[English](./index_en.md) | [官网](https://atayun.net)  | [社区](xxxxx) · [论坛](xxxxx) | [设计文档](doc/design.md) | [使用文档](./user_guide.md) | [安装文档](install.md) | [版本更新](./releases.md)

# Yyconfig - A Object Oriented configuration management system



Yyconfig（有鱼配置中心）是根据携程Apollo进行改造的，定制一般符合中小企业的配置中小服务（Apollo携程框架部门研发的分布式配置中心，能够集中化管理应用不同环境、不同集群的配置，配置修改后能够实时推送到应用端，并且具备规范的权限、流程治理等特性，适用于微服务配置管理场景。）
携程apollo的体系过于复杂和代码层面的不足， 所以进行针对性的改造

# 愿景
  Yyconfig会作为一个 面对对象的配置中心加上内存数据库 ， 但你写代码时，只要告诉 哪些对象交给yyconfig管理。 yyconfig会在提供页面操作，新增，修改删除。  让你写代码时，完全只考虑对象， 不用考虑数据库的存在。同时 不需要 进行 页面管理的开发   

# Features
* ** 携程apollo的不足**
  * 设计过于复杂,资源消耗过多，部署复杂，实际很多应用不需要兼容C#.net
  * 项目内部依赖过于混乱,对于中小企业的配置中心应用不够简单
  * client升级， 根据应用去读取对应的配置
  * 数据的安全性，部分数据需要加密展示，或者不展示，如用户名，密码（只能授权用户查看）
  * 接口安全问题： 目前接口可以直接通过页面的get请求获取-》 改造方案通过JWT通过各个app生成token来进行登陆验证
  * 增强权限控制，权限控制，目前只到组，而且同一时间只能一个组看到对应app。管理员应该看到所有，一个人只能属于一个部门，只能看到一个部门的项目; 改进方案 一个人下挂多个项目，  同时共有项目可以属于部门。 这个部门的项目可以继承这个部门的共有项目
  * 事件监听机制，改成内部消息
  * 应用分类，目前应用没有类型区分，为每个应用做对应的资源下拉。实际生产过程中应该安装业务相关，和业务无关（基础资源区分）
  * 页面UI，前端框架升级， 计划采用react进行对Anglar1的重构
  
  
* ** 增强改造点**  
  *  Yyconfig成为springboot体系标准配置中心,springboot版本升级2.0
  *  代码注解配置 自动映射到配置中心,支持 对象的数据 的映射和 展示
  *  用户在配置中心勾选所需要的命名空间， 然后在client端实时生效
  *  支持对象
  *  操作审计日志优化
  
# Screenshots
![数据库改造点](https://raw.githubusercontent.com/ctripcorp/apollo/master/doc/images/framework/db-frame.png)



  
服务端基于Spring Boot和Spring Cloud开发，打包后可以直接运行，不需要额外安装Tomcat等应用容器。

Java客户端不依赖任何框架，能够运行于所有Java运行时环境，同时对Spring/Spring Boot环境也有较好的支持。


更多产品介绍参见[Apollo配置中心介绍](https://github.com/ctripcorp/apollo/wiki/Apollo%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83%E4%BB%8B%E7%BB%8D)

本地快速部署请参见[Quick Start](https://github.com/ctripcorp/apollo/wiki/Quick-Start)

启动方式：  只要在apollo-portal 项目中，PortalApplication启动main方法就行



# Screenshots
![配置界面](https://raw.githubusercontent.com/ctripcorp/apollo/master/doc/images/apollo-home-screenshot.png)

# Features
* **统一管理不同环境、不同集群的配置**
  * Apollo提供了一个统一界面集中式管理不同环境（environment）、不同集群（clusterEntity）、不同命名空间（namespace）的配置。
  * 同一份代码部署在不同的集群，可以有不同的配置，比如zk的地址等
  * 通过命名空间（namespace）可以很方便的支持多个不同应用共享同一份配置，同时还允许应用对共享的配置进行覆盖

* **配置修改实时生效（热发布）**
  * 用户在Apollo修改完配置并发布后，客户端能实时（1秒）接收到最新的配置，并通知到应用程序。

* **版本发布管理**
  * 所有的配置发布都有版本概念，从而可以方便的支持配置的回滚。

* **灰度发布**
  * 支持配置的灰度发布，比如点了发布后，只对部分应用实例生效，等观察一段时间没问题后再推给所有应用实例。

* **权限管理、发布审核、操作审计**
  * 应用和配置的管理都有完善的权限管理机制，对配置的管理还分为了编辑和发布两个环节，从而减少人为的错误。
  * 所有的操作都有审计日志，可以方便的追踪问题。

* **客户端配置信息监控**
  * 可以方便的看到配置在被哪些实例使用

* **提供Java
  * 支持Spring Placeholder, Annotation和Spring Boot的ConfigurationProperties，方便应用使用（需要Spring 3.1.1+）

* **提供开放平台API**
  * Apollo自身提供了比较完善的统一配置管理界面，支持多环境、多数据中心配置管理、权限、流程治理等特性。
  * 不过Apollo出于通用性考虑，对配置的修改不会做过多限制，只要符合基本的格式就能够保存。
  * 在我们的调研中发现，对于有些使用方，它们的配置可能会有比较复杂的格式，如xml, json，需要对格式做校验。
  * 还有一些使用方如DAL，不仅有特定的格式，而且对输入的值也需要进行校验后方可保存，如检查数据库、用户名和密码是否匹配。
  * 对于这类应用，Apollo支持应用方通过开放接口在Apollo进行配置的修改和发布，并且具备完善的授权和权限控制

* **部署简单**
  * 配置中心作为基础服务，可用性要求非常高，这就要求Apollo对外部依赖尽可能地少
  * 目前唯一的外部依赖是MySQL，所以部署非常简单，只要安装好Java和MySQL就可以让Apollo跑起来
  * Apollo还提供了打包脚本，一键就可以生成所有需要的安装包，并且支持自定义运行时参数

# Usage
  1. [Apollo使用指南](https://github.com/ctripcorp/apollo/wiki/Apollo%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)
  2. [Java客户端使用指南](https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)
  5. [Apollo开放平台接入指南](https://github.com/ctripcorp/apollo/wiki/Apollo%E5%BC%80%E6%94%BE%E5%B9%B3%E5%8F%B0)
  6. [Apollo使用场景和示例代码](https://github.com/ctripcorp/apollo-use-cases)

# Design
  * [Apollo配置中心设计](https://github.com/ctripcorp/apollo/wiki/Apollo%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83%E8%AE%BE%E8%AE%A1)
  * [Apollo核心概念之“Namespace”](https://github.com/ctripcorp/apollo/wiki/Apollo%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5%E4%B9%8B%E2%80%9CNamespace%E2%80%9D)
  * [Apollo配置中心架构剖析](https://mp.weixin.qq.com/s/-hUaQPzfsl9Lm3IqQW3VDQ)
  * [Apollo源码解析](http://www.iocoder.cn/categories/Apollo/)（据说Apollo非常适合作为初学者第一个通读源码学习的分布式中间件产品）

# Development
  * [Apollo开发指南](https://github.com/ctripcorp/apollo/wiki/Apollo%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)
  * Code Styles
    * [Eclipse Code Style](https://github.com/ctripcorp/apollo/blob/master/apollo-buildtools/style/eclipse-java-google-style.xml)
    * [Intellij Code Style](https://github.com/ctripcorp/apollo/blob/master/apollo-buildtools/style/intellij-java-google-style.xml)

# Deployment
  * [Quick Start](https://github.com/ctripcorp/apollo/wiki/Quick-Start)
  * [分布式部署指南](https://github.com/ctripcorp/apollo/wiki/%E5%88%86%E5%B8%83%E5%BC%8F%E9%83%A8%E7%BD%B2%E6%8C%87%E5%8D%97)

# FAQ
  * [常见问题回答](https://github.com/ctripcorp/apollo/wiki/FAQ)
  * [部署&开发遇到的常见问题](https://github.com/ctripcorp/apollo/wiki/%E9%83%A8%E7%BD%B2&%E5%BC%80%E5%8F%91%E9%81%87%E5%88%B0%E7%9A%84%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)

# Presentation
  * [携程开源配置中心Apollo的设计与实现](doc/design.md) 
  * [Slides](http://techshow.ctrip.com/wp-content/uploads/2017/08/%E5%BC%80%E6%BA%90%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83Apollo%E7%9A%84%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0-%E6%90%BA%E7%A8%8B%E5%AE%8B%E9%A1%BA.pdf)

# Publication
  * [开源配置中心Apollo的设计与实现](doc/design.md) 

# Support
<table>
  <thead>
    <th>YyApollo配置中心技术支持<br />QQ号：502238410, 或者邮件联系 502238410@qq.com</th>
  </thead>
  <tbody>
  </tbody>
</table>

# Contribution
  * Source Code: https://github.com/ctripcorp/apollo
  * Issue Tracker: https://github.com/ctripcorp/apollo/issues

# License
The project is licensed under the [Apache 2 license](https://github.com/ctripcorp/apollo/blob/master/LICENSE).

# Known Users

> 按照登记顺序排序，更多接入公司，欢迎在[https://github.com/ctripcorp/apollo/issues/451](https://github.com/ctripcorp/apollo/issues/451)登记（仅供开源用户参考）

