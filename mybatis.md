# 使用Mybatis Generator 自动生成代码

## 生成DB

mysql> create database hfang default charset utf-8;

mysql> use hfang

mysql> 一段创建表的语句，此处可以在可视化工具中自行创建

## 使用MAVEN 下载以来jar包

mvn -f mybatis-pom.xml dependency:copy-dependencies

## 运行Generator

mvn -f mybatis-pom.xml mybatis-generator:generate

##参考文档

http://www.mybatis.org/generator/configreference/xmlconfig.html
