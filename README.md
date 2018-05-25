# 版本说明

## 脚手架核心说明

* 统一项目结构，方便快速入手。
* 减少每个项目重新搭建成本。
* 统一三方工具以及驱动类。
* 统一日志打印，方便追溯。
* 集中优质资源，解放开发有更多时间放到业务设计上。
* 统一日志、资源池、消息组件、定时调度、缓存、监控的接入。

``
## 待支持功能点

* mq支持
* 分库分表支持
* 分布式调度支持 elastic-job
* 分布式配置中心支持 diamond

* 数据库连接池优化
* API接口文档支持
* 阿里云组件支持OSS等
* Sequence生成

## v1.2.1
**author:haiyang.song**
**date:2018-05-25**

### 修订内容
* Sequence生成支持，使用表结构加缓存方式支持自增序列的支持。
* redis工具支持。
* 系统存活监控。
* 编写测试使用多结构demo,demo-apple,demo-banana,demo-cherry。


## v1.2.0

**author:haiyang.song**
**date:2018-05-25**

### 修订内容

* 将项目从基本模版抽离成archetype模版方便快速部署。
* 升级dalgen版本，说明参考：https://gitee.com/bangis/mybatis.generator/wikis/pages?title=Home&parent=
* 增加规范化日志，具体参考log4j.xml
* 增加全链路跟踪ID,`REQ_ID'存储在Dubbo上下文中，通过拦截器直接打印到接口层日志。

### 快速说明

```shell
mvn archetype:generate \
    -DarchetypeGroupId=com.maven.archetype  \
    -DarchetypeArtifactId=archetype-springboot \
    -DarchetypeVersion=1.0  \
    -DgroupId=com.haiyang.test \
    -DartifactId=testgen \
    -Dversion=1.2.0 \
    -DarchetypeCatalog=internal 
```

### 日志说明

* 日志结构已经说明
```log
logs
├── logs
│   ├── biz-default.log
│   ├── biz-job.log
│   ├── common-alert.log
│   ├── common-default.log
│   ├── common-error.log
│   ├── common-perf.log
│   ├── framework-all.log
│   ├── integration-api-default.log
│   ├── integration-api-digest.log
│   ├── service-api-default.log
│   ├── service-api-digest.log
│   ├── web-api-default.log
│   └── web-api-digest.log
```

整个日志分为四大块。

> 第一块：所有的接口日志，分为WEB-API、SERVICE-API、INTEGRATION-API。

> default日志就是所有外部接口调用以及调用结果，用来管理控制。
    digest为摘要日志用来快速定位问题以及进行系统监控。

> 第二快：为Common以及框架日志，用来检查系统状态以及系统错误。

> 第三块：为Biz业务日志，在业务上的重要信息或者业务上的错误但是不影响提供的都打印在此。

> 第四块：为Data数据日志，包含数据库、redi、mq等调用的日志，尽量只打印摘要信息。

> 注意：common-alert.log 和 common-error.logs属于非可预见性系统错误，需要监控报警。



## v1.1.0

* 增加demo并修改dalgen的模版支持自动更新最后修改时间。
* 大量去掉少用的模块，根据快速开发精简模块，现有项目目录如下：


```
├── app
│   ├── biz
│   │   ├── facade-impl
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   ├── manager
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   ├── service
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   └── task
│   │       ├── pom.xml
│   │       └── src
│   │           └── main
│   │               ├── java
│   │               └── resources
│   ├── common
│   │   ├── dal
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   │ 
│   │   ├── service-facade
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   ├── service-integration
│   │   │   ├── pom.xml
│   │   │   └── src
│   │   │       └── main
│   │   │           ├── java
│   │   │           └── resources
│   │   └── util
│   │       ├── pom.xml
│   │       └── src
│   │           └── main
│   │               ├── java
│   │               └── resources
│   └── web
│       └── service
│           ├── pom.xml
│           └── src
│               └── main
│                   ├── java
│                   └── resources
├── dalgen
│   ├── config
│   ├── gen.sh
│   ├── meteringChargeTables
│   ├── pom.xml
│   ├── startdtAdminTables
│   ├── templates
│   └── testTables
└── pom.xml
```

## v1.0.0

* 完成整体项目基本结构，并且确定各个结构功能点。
* 增加dalgen支持，用于自动生成dal层。


