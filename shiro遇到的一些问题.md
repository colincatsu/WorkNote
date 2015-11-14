# shiro使用过程中遇到的一些问题

* 配置session共享的问题

> **注意：**我实现的是redis session共享方法。

```java```

/**
 * Created by colinsu on 2015/11/13.
 */
public class MySessionDao extends CachingSessionDAO {
    @Autowired
    private RedisUtil redisUtil;


    private static  ObjectsTranscoder serializeTranscoder = new ObjectsTranscoder();
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        try{
            redisUtil.getInstance().set(sessionId.toString().getBytes(Charset.defaultCharset()), serializeTranscoder.serialize(session));
        }catch(Exception e){

        }

        return session.getId();
    }
    protected void doUpdate(Session session) {
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return; //如果会话过期/停止 没必要再更新了
        }
        try {
            redisUtil.getInstance().set(session.getId().toString().getBytes(Charset.defaultCharset()), serializeTranscoder.serialize(session));
        }catch (Exception e){

        }

    }
    protected void doDelete(Session session) {

        redisUtil.getInstance().del(session.getId().toString().getBytes(Charset.defaultCharset()));

    }
    protected Session doReadSession(Serializable sessionId) {
        try {
            byte[] sessionString = redisUtil.getInstance().get(sessionId.toString().getBytes(Charset.defaultCharset()));
            if(sessionString == null ) return null;
            return (Session)serializeTranscoder.deserialize(sessionString);
        }catch (Exception e){
            return  null;
        }

    }
}


```

## 主要遇到的问题
* 使用 `JedisPoll.getResource()`方法获取不到相应的 `Jedis对象` 导致session共享失败
* 配置文件配置错误

因为我使用的是CachingSessionDAO 方法，所以需要对cache进行配置

```xml```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:aop="http://www.springframework.org/schema/aop" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa-1.0.xsd">
  	
  	<bean id="securityRealm" class="com.ethank.security.SecurityRealm">
  		<property name="cachingEnabled" value="true"/>
  	</bean>
	<bean id="mySessionDao" class="com.ethank.security.MySessionDao">
		<property name="activeSessionsCacheName" value="shiro-activeSessionCache"/>
		<property name="sessionIdGenerator" ref="sessionIdGenerator"/>
	</bean>

  	<!-- id生成器 -->
  	<bean id="sessionIdGenerator" class="org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator"/>
    <!-- 缓存管理器 -->
    <bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
        <property name="cacheManagerConfigFile" value="classpath:conf/ehcache-shiro.xml"/>
    </bean>
    <!-- 会话DAO -->
    <!--<bean id="sessionDAO" class="org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO">-->
    	<!--<property name="activeSessionsCacheName" value="shiro-activeSessionCache"/>-->
        <!--<property name="sessionIdGenerator" ref="sessionIdGenerator"/>-->
    <!--</bean>-->
    <!-- 会话验证调度器 -->  
    <bean id="sessionValidationScheduler" class="org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler">  
        <property name="sessionValidationInterval" value="86400000"/>  
        <property name="sessionManager" ref="sessionManager"/>  
    </bean>
    <!-- 会话管理器 -->
    <bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
    	<property name="sessionValidationSchedulerEnabled" value="true"/>
    	<property name="deleteInvalidSessions" value="true"/>
        <property name="sessionValidationScheduler" ref="sessionValidationScheduler"/>
        <property name="globalSessionTimeout" value="86400000"/>
        <property name="sessionDAO" ref="mySessionDao"/>
    </bean>

    <!-- 安全管理器 -->
	<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
		<property name="realm" ref="securityRealm"/>
		<property name="sessionManager" ref="sessionManager"/>
		<property name="cacheManager" ref="cacheManager"/>
	</bean>
	<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
	   <property name="securityManager" ref="securityManager" />
	   <property name="successUrl" value="/page/toindex.json" />
	   <property name="loginUrl" value="/page/tologin.json" />
	   <property name="unauthorizedUrl" value="/page/401.json" />
	   <property name="filterChainDefinitions">
	     <value>

	             <!--  /** = anon  -->
		 </value>
	   </property> 
	</bean>
	<bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor" />
</beans>
```
`activeSessionsCacheName` 和 `sessionIdGenerator` 必须也进行DI注入





