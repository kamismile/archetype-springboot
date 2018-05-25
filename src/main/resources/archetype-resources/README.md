## 项目说明

author:haiyang.song

version:1.1.0

## 整体服务架构设计

![整体服务架构设计](http://oppxlx6i0.bkt.clouddn.com/%E6%9C%89%E5%83%8F%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

### 环境地址

* 开发环境zookeeper监控中心 

地址:http://?:8090/ 账号+密码：?/?

* 开发环境difconf 

地址:http://?:8081/ 账号+密码：?/?

* nexus私服

地址:http://?:8081/nexus/#welcome 账号+密码：?/?

### maven配置修改

因为增加maven私服设置所以需要配置账号和密码。

修改maven的setting.xml配置文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <pluginGroups>
    </pluginGroups>

    <proxies>
    </proxies>

    <servers>
        <server>
            <id>public</id>
            <username>startdt</username>
            <password>admin_nexus_hello123</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>startdt</username>
            <password>admin_nexus_hello123</password>
        </server>
        <server>
            <id>thirdparty</id>
            <username>startdt</username>
            <password>admin_nexus_hello123</password>
        </server>
        <server>
            <id>central</id>
            <username>startdt</username>
            <password>admin_nexus_hello123</password>
        </server>
        <server>
            <id>releases</id>
            <username>startdt</username>
            <password>admin_nexus_hello123</password>
        </server>
    </servers>

    <mirrors>
        <mirror>
            <id>public</id>
            <mirrorOf>*</mirrorOf>
            <url>http://118.31.10.136:8081/nexus/content/groups/public/</url>
        </mirror>
    </mirrors>
</settings>
```

### 基本说明

本项目是SOA化结构，采用springboot+dubbox结构，所有本系统依赖zookeeper作为配置中心使用。
每个发布的服务都会通过zookeeper进行注册，所以在配置中心可以查看已经注册的服务和依赖的服务。

### 项目依赖关系
![项目依赖关系](http://oppxlx6i0.bkt.clouddn.com/SOA%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1.png)

#### web-service 接口服务模块，外部http接口

`web-service`作为对外部提供http接口的层级，同时也是系统启动的依赖层，启动系统需要的main方法在`ServletInitializer`，除了网关`gateway`系统，其他系统不需要对该层级进行开发，网关对外提供的http接口以及外部的调用都通过该模块。

#### biz-manager 业务模块，业务逻辑服务

`biz-manager`作为业务对外提供mananger，manager层级依赖dao以及server层级，对`common-service-facade`提供业务逻辑的合并和处理。

比如用户需要登陆，则在manager完成用户状态，安全信息的检查和处理，在facade的实现层不要写过多的业务逻辑，对于用户的检查很多接口可能都需要依赖。

#### biz-server 业务服务，基础粒度服务

`biz-service`是提供更为基础粒度的服务，比如单一的用户状态检查，单一的信息查询，所有信息的组装以及业务逻辑是在`biz-mananger`层完成的。

#### biz-task 定时调度外部层

`biz-task`：内部不包含有调度逻辑，只有被调度以后的接口实现，业务在`biz-manager`内实现。

*注意*:定时调度需要支持重复调用，业务不异常，要允许定时任务挂了以后重复调用补偿。

#### common-service-facade 外部接口，biz-facade-impl 外部接口实现

`common-service-facade`：只提供外部接口、接口通信依赖的实体，没有功能实现，这个jar包会被其他系统依赖。

*注意*： 这个对外提供的接口需要完整的功能以及参数注释，对外不允许使用枚举，尽量平铺参数，以免造成服务jar依赖，导致接口升级外部系统同时发布的依赖。

`biz-facade-impl`：提供对外部接口的实现，作为业务的处理，如果逻辑公共将逻辑放到`biz-manager`并通过引入的方式使用。

*注意*：外部接口需要打印接口请求，以及返回结果的摘要日志。需要通过aop统一处理。

#### common-dal 数据库

提供数据库操作的操作.

*注意*：这个层级所有的方法都是要通过dalgen生成，具体操作方法看工具的介绍。如果dalgen无法满足的功能在手写sql的话，需要自己增加dao、sqlmap等，统一以ext开头。

#### common-util 工具类

整个系统肯定会有很多工具类，比如加解密、时间格式化等等，这些工具类统一在这层完成编写。

#### common-service-intergration 外部服务依赖

外部服务依赖，依赖的外部服务需要在该层进行引用，并且对外部异常需要进行处理，每个单独的外部系统需要通过包的方式进行区分，不允许多个系统写到一起，这里只提供单系统的接口依赖服务，多个系统的通过biz业务聚合。


### dalgen工具

在app同级别目录内有个dalgen的工具，原有的工具里生成的sql方法过多，dba无法统计和优化，开发人员也不知道自己使用了哪些代码对应的sql所以更换为这个有严格要求的工具类

命令：`mvn mybatis:gen`
然后输入表名的方式生成。如果(systemname)tables文件夹内已经有了表则根据内容直接生成dal层，如果没有会直接根据db表结构进行生成。

*注意*：进行写严格的sql少写通用性sql，特别是update方法。

#### 模板使用说明

* 执行 mvn mybatis:gen 首先会初始化配置,减少了一步自己copy 配置文件的麻烦

```xml
config.xml 支持分页,支持OB,支持参数简写为 ?
<?xml version="1.0" encoding="UTF-8"?>

<!-- ============================================================== -->
<!-- Master configuration file for auto-generation of iPaycore dal. -->
<!-- ============================================================== -->

<config>
    <!-- ========================================================== -->
    <!-- The typemap("Type Map") maps from one java type to another -->
    <!-- java type. If you feel the original sql data type to java -->
    <!-- type mapping is not satisfactory, you can use typemap to -->
    <!-- convert it to a more appropriate one. -->
    <!-- ========================================================== -->
    <typemap from="java.sql.Date" to="java.util.Date"/>
    <typemap from="java.sql.Time" to="java.util.Date"/>
    <typemap from="java.sql.Timestamp" to="java.util.Date"/>
    <typemap from="java.math.BigDecimal" to="Long"/>
    <typemap from="byte" to="int"/>
    <typemap from="short" to="int"/>

    <!-- ========================================================== -->
    <!-- datasource config  可以配置多个-->
    <!-- ========================================================== -->
    <database name="risk" class="org.gjt.mm.mysql.Driver" type="mysql">
        <property name="url" value="jdbc:mysql://10.209.176.32:3406/xxpt_rec_dc"/>
        <property name="userid" value="xxpt_rec_dc"/>
        <property name="password" value="hello1234"/>
        <property name="schema" value="xxpt_rec_dc"/>
    </database>

    <!--ob 配置 执行失败则需要 替换${java_home}/jre/lib/security/ 下面的local_policy.jar和US_export_policy.jar-->
    <database name="fporgassetcenter" type="ob">
        <property name="url" value="http://obconsole.test.alibaba-inc.com/ob-config/config.co?dataId=daily_052"/>
    </database>

    <!-- ========project.name pom.xml中的值========================= -->
    <!--<package value="com.oschina.${name}.common.dal.${database.name}.auto"/>-->
    <package value="com.alibaba.recruit.datacenter.dal.${database.name}"/>

    <!-- ========================================================== -->
    <!-- 省略前置 支持多个 -->
    <tablePrefix value="dc_bg"/><!--长的放前面-->
    <tablePrefix value="dc"/>
    <tablePath value="${database.name}Tables/"/>
    <!--分库分表规则  分表后缀 支持多个-->
    <splitTableSuffix value="_000"/>

</config>

```

##### table.xml 例子
*首次执行输出DC_BG_RISK_SCAN 后DC_BG_RISK_SCAN.xml会自动生成,您仅需要添加自己的sql即可
```xml
DC_BG_RISK_SCAN.xml
<!DOCTYPE table SYSTEM "../table-config-1.1.dtd">
<!-- sqlname逻辑表,用于生成对象   physicalName物理表,用于从数据中获取数据 -->
<table sqlname="DC_BG_RISK_SCAN" physicalName="DC_BG_RISK_SCAN">
    <!--  特殊字符说明  &lt;&gt;   <> -->
    <operation name="insert" paramtype="object" remark="插入表:DC_BG_RISK_SCAN">
        <selectKey resultType="java.lang.Long" keyProperty="id" order="AFTER">
            SELECT
            LAST_INSERT_ID()
        </selectKey>
        INSERT INTO DC_BG_RISK_SCAN(
            ID
            ,NAME
            ,RISK
            ,DETAIL
            ,EDU_INFO
            ,ID_CARD_NO
            ,DETAIL_URL
            ,GMT_CREATE
            ,GMT_MODIFIED
        )VALUES(
             #{id,jdbcType=BIGINT}
            , #{name,jdbcType=VARCHAR}
            , #{risk,jdbcType=CHAR}
            , #{detail,jdbcType=VARCHAR}
            , #{eduInfo,jdbcType=VARCHAR}
            , #{idCardNo,jdbcType=VARCHAR}
            , #{detailUrl,jdbcType=VARCHAR}
            , #{gmtCreate,jdbcType=TIMESTAMP}
            , #{gmtModified,jdbcType=TIMESTAMP}
        )
    </operation>

    <operation name="update" paramtype="object" remark="更新表:DC_BG_RISK_SCAN">
        UPDATE DC_BG_RISK_SCAN
        SET
            ID              = #{id,jdbcType=BIGINT}
            ,NAME            = #{name,jdbcType=VARCHAR}
            ,RISK            = #{risk,jdbcType=CHAR}
            ,DETAIL          = #{detail,jdbcType=VARCHAR}
            ,EDU_INFO        = #{eduInfo,jdbcType=VARCHAR}
            ,ID_CARD_NO      = #{idCardNo,jdbcType=VARCHAR}
            ,DETAIL_URL      = #{detailUrl,jdbcType=VARCHAR}
            ,GMT_CREATE      = #{gmtCreate,jdbcType=TIMESTAMP}
            ,GMT_MODIFIED    = #{gmtModified,jdbcType=TIMESTAMP}
        WHERE
            ID              = #{id,jdbcType=BIGINT}
    </operation>

    <operation name="deleteByPrimary" multiplicity="one" remark="根据主键删除数据:DC_BG_RISK_SCAN">
        DELETE FROM
            DC_BG_RISK_SCAN
        WHERE
            ID = #{id,jdbcType=BIGINT}
    </operation>

    <operation name="getByPrimary" multiplicity="one"  remark="根据主键获取数据:DC_BG_RISK_SCAN">
        SELECT *
        FROM DC_BG_RISK_SCAN
        WHERE
            ID = #{id,jdbcType=BIGINT}
    </operation>

    <!--自定义resultMap-->
    <resultmap name="myResultMap" type="MyResult">
        <column name="name" javatype="String"/>
        <column name="risk" javatype="String"/>
    </resultmap>

    <!-- idCardNoXX 自定义参数,需要指定类型,可以通过 jdbcType 也可以通过 javaType -->
    <operation name="getMyResultMap" resultmap="myResultMap" remark="自定义ResultMap">
        select
            name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        or
        id_card_no=#{idCardNoXX,jdbcType=VARCHAR}
        limit 1
    </operation>

    <!-- idCardNoXX 自定义参数,需要指定类型,可以通过 jdbcType 也可以通过 javaType -->
    <operation name="getListParams" resultmap="myResultMap" remark="foreach支持">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
        limit 1
    </operation>

    <!-- idCardNoXX 自定义参数,需要指定类型,可以通过 jdbcType 也可以通过 javaType -->
    <operation name="getListParamsMany" multiplicity="many" resultmap="myResultMap" remark="foreach支持 many">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
    </operation>
</table>
```
###### 生成结果
```xml
RiskScanDOMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.alibaba.recruit.datacenter.risk.dal.mapper.RiskScanDOMapper">
    <!-- 自动生成,请修改 DC_BG_RISK_SCAN.xml -->
    <resultMap id="BaseResultMap"  type="com.alibaba.recruit.datacenter.risk.dal.dataobject.RiskScanDO">
        <id column="ID" property="id" jdbcType="BIGINT" javaType="Long"/>
        <result column="NAME" property="name" jdbcType="VARCHAR" javaType="String"/>
        <result column="RISK" property="risk" jdbcType="CHAR" javaType="String"/>
        <result column="DETAIL" property="detail" jdbcType="VARCHAR" javaType="String"/>
        <result column="EDU_INFO" property="eduInfo" jdbcType="VARCHAR" javaType="String"/>
        <result column="ID_CARD_NO" property="idCardNo" jdbcType="VARCHAR" javaType="String"/>
        <result column="DETAIL_URL" property="detailUrl" jdbcType="VARCHAR" javaType="String"/>
        <result column="GMT_CREATE" property="gmtCreate" jdbcType="TIMESTAMP" javaType="java.util.Date"/>
        <result column="GMT_MODIFIED" property="gmtModified" jdbcType="TIMESTAMP" javaType="java.util.Date"/>
    </resultMap>

    <resultMap id="myResultMap"  type="com.alibaba.recruit.datacenter.risk.dal.resultmap.MyResult">
        <result column="NAME" property="name" jdbcType="VARCHAR" javaType="String"/>
        <result column="RISK" property="risk" jdbcType="CHAR" javaType="String"/>
    </resultMap>

    <sql id="Base_Column_List">
        ID,NAME,RISK,DETAIL,EDU_INFO,ID_CARD_NO,DETAIL_URL,GMT_CREATE,GMT_MODIFIED
    </sql>


    <!--插入表:DC_BG_RISK_SCAN-->
    <insert id="insert" >
        <selectKey resultType="java.lang.Long" keyProperty="id" order="AFTER">
            SELECT
            LAST_INSERT_ID()
        </selectKey>
        INSERT INTO DC_BG_RISK_SCAN(
            ID
            ,NAME
            ,RISK
            ,DETAIL
            ,EDU_INFO
            ,ID_CARD_NO
            ,DETAIL_URL
            ,GMT_CREATE
            ,GMT_MODIFIED
        )VALUES(
             #{id,jdbcType=BIGINT}
            , #{name,jdbcType=VARCHAR}
            , #{risk,jdbcType=CHAR}
            , #{detail,jdbcType=VARCHAR}
            , #{eduInfo,jdbcType=VARCHAR}
            , #{idCardNo,jdbcType=VARCHAR}
            , #{detailUrl,jdbcType=VARCHAR}
            , #{gmtCreate,jdbcType=TIMESTAMP}
            , #{gmtModified,jdbcType=TIMESTAMP}
        )
    </insert>

    <!--更新表:DC_BG_RISK_SCAN-->
    <update id="update" >
        UPDATE /*MS-AUTODALGEN-DC-BG-RISK-SCAN-UPDATE*/ DC_BG_RISK_SCAN
        SET
            ID              = #{id,jdbcType=BIGINT}
            ,NAME            = #{name,jdbcType=VARCHAR}
            ,RISK            = #{risk,jdbcType=CHAR}
            ,DETAIL          = #{detail,jdbcType=VARCHAR}
            ,EDU_INFO        = #{eduInfo,jdbcType=VARCHAR}
            ,ID_CARD_NO      = #{idCardNo,jdbcType=VARCHAR}
            ,DETAIL_URL      = #{detailUrl,jdbcType=VARCHAR}
            ,GMT_CREATE      = #{gmtCreate,jdbcType=TIMESTAMP}
            ,GMT_MODIFIED    = #{gmtModified,jdbcType=TIMESTAMP}
        WHERE
            ID              = #{id,jdbcType=BIGINT}
    </update>

    <!--根据主键删除数据:DC_BG_RISK_SCAN-->
    <delete id="deleteByPrimary" >
        DELETE /*MS-AUTODALGEN-DC-BG-RISK-SCAN-DELETEBYPRIMARY*/ FROM
            DC_BG_RISK_SCAN
        WHERE
            ID = #{id,jdbcType=BIGINT}
    </delete>

    <!--根据主键获取数据:DC_BG_RISK_SCAN-->
    <select id="getByPrimary" resultMap="BaseResultMap">
        SELECT /*MS-AUTODALGEN-DC-BG-RISK-SCAN-GETBYPRIMARY*/  <include refid="Base_Column_List" />
        FROM DC_BG_RISK_SCAN
        WHERE
            ID = #{id,jdbcType=BIGINT}
    </select>

    <!--自定义ResultMap-->
    <select id="getMyResultMap" resultMap="myResultMap">
        select
            name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        or
        id_card_no=#{idCardNoXX,jdbcType=VARCHAR}
        limit 1
    </select>

    <!--foreach支持-->
    <select id="getListParams" resultMap="myResultMap">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
        limit 1
    </select>

    <!--foreach支持 many-->
    <select id="getListParamsMany" resultMap="myResultMap">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
    </select>
</mapper>
```

```java
package com.alibaba.recruit.datacenter.risk.dal.mapper;

import com.alibaba.recruit.datacenter.risk.dal.dataobject.RiskScanDO;
import com.alibaba.recruit.datacenter.risk.dal.resultmap.MyResult;
import java.util.List;

/**
 *The Table DC_BG_RISK_SCAN.
 *风险扫描
 */
public interface RiskScanDOMapper{

    /**
     *desc:插入表:DC_BG_RISK_SCAN.<br/>
     *descSql =  SELECT LAST_INSERT_ID() INSERT INTO DC_BG_RISK_SCAN( ID ,NAME ,RISK ,DETAIL ,EDU_INFO ,ID_CARD_NO ,DETAIL_URL ,GMT_CREATE ,GMT_MODIFIED )VALUES( #{id,jdbcType=BIGINT} , #{name,jdbcType=VARCHAR} , #{risk,jdbcType=CHAR} , #{detail,jdbcType=VARCHAR} , #{eduInfo,jdbcType=VARCHAR} , #{idCardNo,jdbcType=VARCHAR} , #{detailUrl,jdbcType=VARCHAR} , #{gmtCreate,jdbcType=TIMESTAMP} , #{gmtModified,jdbcType=TIMESTAMP} )
     *@param RiskScanDO RiskScanDO
     *@return Long
     */
    Long insert(entity RiskScanDO);
    /**
     *desc:更新表:DC_BG_RISK_SCAN.<br/>
     *descSql =  UPDATE DC_BG_RISK_SCAN SET ID = #{id,jdbcType=BIGINT} ,NAME = #{name,jdbcType=VARCHAR} ,RISK = #{risk,jdbcType=CHAR} ,DETAIL = #{detail,jdbcType=VARCHAR} ,EDU_INFO = #{eduInfo,jdbcType=VARCHAR} ,ID_CARD_NO = #{idCardNo,jdbcType=VARCHAR} ,DETAIL_URL = #{detailUrl,jdbcType=VARCHAR} ,GMT_CREATE = #{gmtCreate,jdbcType=TIMESTAMP} ,GMT_MODIFIED = #{gmtModified,jdbcType=TIMESTAMP} WHERE ID = #{id,jdbcType=BIGINT}
     *@param RiskScanDO RiskScanDO
     *@return Long
     */
    Long update(entity RiskScanDO);
    /**
     *desc:根据主键删除数据:DC_BG_RISK_SCAN.<br/>
     *descSql =  DELETE FROM DC_BG_RISK_SCAN WHERE ID = #{id,jdbcType=BIGINT}
     *@param id id
     *@return Long
     */
    Long deleteByPrimary(Long id);
    /**
     *desc:根据主键获取数据:DC_BG_RISK_SCAN.<br/>
     *descSql =  SELECT *FROM DC_BG_RISK_SCAN WHERE ID = #{id,jdbcType=BIGINT}
     *@param id id
     *@return RiskScanDO
     */
    RiskScanDO getByPrimary(Long id);
    /**
     *desc:自定义ResultMap.<br/>
     *descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} or id_card_no=#{idCardNoXX,jdbcType=VARCHAR} limit 1
     *@param idCardNo idCardNo
     *@param idCardNoXX idCardNoXX
     *@return MyResult
     */
    MyResult getMyResultMap(String idCardNo,String idCardNoXX);
    /**
     *desc:foreach支持.<br/>
     *descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} and name in #{name,jdbcType=VARCHAR} limit 1
     *@param idCardNo idCardNo
     *@param names names
     *@return MyResult
     */
    MyResult getListParams(String idCardNo,List<String> names);
    /**
     *desc:foreach支持 many.<br/>
     *descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} and name in #{name,jdbcType=VARCHAR} 
     *@param idCardNo idCardNo
     *@param names names
     *@return List<MyResult>
     */
    List<MyResult> getListParamsMany(String idCardNo,List<String> names);
}
```

##### 增加分页支持

```xml
multiplicity="paging" --标记为此方法需要走分页查询
paging="QueryRisk"    --分页查询参数类名称
                      --自动生成DAO类,通过Spring 自动扫描方法注入,不提供xml配置项目生成了.
                      --DOMapper接口与DAO类区别在于对分页的支持
    <operation name="getListParamsPage" multiplicity="paging" paging="QueryRisk" resultmap="myResultMap" remark="foreach支持 many">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
        order by gmt_modified desc
    </operation>

Mapper.xml结果
    <!--foreach支持 many pageCount-->
    <select id="getListParamsPageCount"  resultType="int">
        SELECT COUNT(*) AS total FROM
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>

    </select>
    <!--foreach支持 many pageResult-->
    <select id="getListParamsPageResult"  resultMap="myResultMap">
        select
        name,risk
        from
        DC_BG_RISK_SCAN
        where
        id_card_no=#{idCardNo}
        and
        name in
        <foreach collection="names" item="name" index="index" open="(" close=")" separator=",">
            #{name,jdbcType=VARCHAR}
        </foreach>
        order by gmt_modified desc
        limit #{startRow},#{limit}
    </select>

DOMapper.java
   /**
     * desc:foreach支持 many.<br/>
     * descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} and name in #{name,jdbcType=VARCHAR} order by gmt_modified desc
     * @param queryRisk queryRisk
     * @return int
     */
    int getListParamsPageCount(QueryRiskPage queryRisk);
    /**
     * desc:foreach支持 many.<br/>
     * descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} and name in #{name,jdbcType=VARCHAR} order by gmt_modified desc
     * @param queryRisk queryRisk
     * @return List<MyResult>
     */
    List<MyResult> getListParamsPageResult(QueryRiskPage queryRisk);

DAO.java
    /**
     * desc:foreach支持 many.<br/>
     * descSql =  select name,risk from DC_BG_RISK_SCAN where id_card_no=#{idCardNo} and name in #{name,jdbcType=VARCHAR} order by gmt_modified desc
     * @param queryRisk queryRisk
     * @return QueryRiskPage
     */
    public QueryRiskPage getListParamsPage(QueryRiskPage queryRisk){
        int total = riskScanDOMapper.getListParamsPageCount(queryRisk);
        if(total>0){
            queryRisk.setDatas(riskScanDOMapper.getListParamsPageResult(queryRisk));
        }
        queryRisk.setTotal(total);
        return queryRisk;
    }
如需分页会自动创建分页类 BasePage.java
```
