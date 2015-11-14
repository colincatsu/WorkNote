# maven多环境配置 dev test 

```java
	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ethank</groupId>
        <artifactId>ethank-server</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>..</relativePath>
    </parent>

    <artifactId>ethank-ktv-manager</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <!-- <url>http://maven.apache.org</url> -->

      <profiles>
        <!-- 开发环境 dev for SaaS development, company for Enterprise development -->
        <profile>
            <id>dev</id>
            <activation>
                <!-- 默认 -->
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <param>dev</param>
            </properties>
    
        </profile>
        <profile>

            <id>prod</id>
            <activation>
            </activation>
            <properties>
                <param>prod</param>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <param>test</param>
            </properties>
        </profile>
    </profiles>

    <build>

        <resources>
            <resource>
                <directory>${project.basedir}/src/main/resources</directory>
                <!-- 资源根目录排除各环境的配置，使用单独的资源目录来指定 -->
                <excludes>
                    <exclude>dev/*</exclude>
                    <exclude>prod/*</exclude>
                    <exclude>test/*</exclude>
                </excludes>
            </resource>
            <resource>
                <directory>${project.basedir}/src/main/resources-${param}</directory>
            </resource>
        </resources>

        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>

            <plugin>
                <artifactId>maven-war-plugin</artifactId>
                <version>2.4</version>
                <configuration>
                    <webResources>
                        <resource>
                            <directory>src/main/webapp</directory>
                        </resource>
                    </webResources>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>



        </plugins>
    </build>
</project>


```

**    <profile>
            <id>dev</id>
            <activation>
                <!-- 默认 -->
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <param>dev</param>
            </properties>
    
        </profile>
        <profile>

            <id>prod</id>
            <activation>
            </activation>
            <properties>
                <param>prod</param>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <param>test</param>
            </properties>
        </profile>
    </profiles> **