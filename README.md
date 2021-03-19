<div align="center">
    <img src="http://p.ananas.chaoxing.com/star3/origin/3b432cacff18301899f8d7832bd5cba8.png" alt="logo" title="logo" width="50%" style="text-align:center;">
</div>

# 作业互评系统

## 预览截图 screenshot

### 首页

<br/>

<img src="http://p.ananas.chaoxing.com/star3/origin/c585ae0a4c9eb03237a01206a3a735e0.png" alt="首页" title="首页" >

<br/>

<img src="http://p.ananas.chaoxing.com/star3/origin/77dc5d4e818b94b3f9ad987cd16c5b19.png" alt="首页" title="首页" >


### 课程

<br/>

<img src="http://p.ananas.chaoxing.com/star3/origin/82b7120bd98d34bdd5bfbd51c422a809.png" alt="课程页" title="课程页" >

<br/>

<img src="http://p.ananas.chaoxing.com/star3/origin/f577695eff31fe5d79a06556790a5ab3.png" alt="课程页" title="课程页" >

<br/>

<img src="http://p.ananas.chaoxing.com/star3/origin/3e8e6a7f51809653641cd858d1279eca.png" alt="课程页" title="课程页" >

### 社区

<img src="http://p.ananas.chaoxing.com/star3/origin/12c8190ab05e7f7c057b39d187495f8d.png" alt="社区" title="社区" >


<img src="http://p.ananas.chaoxing.com/star3/origin/f7902c836badf657e3a0dfff0e7b7b32.png" alt="社区" title="社区" >




## 运行环境

|           组件           |                        技术                        |
| :-------------------------: | :--------------------------------------------------: |
|           前端           |                       vue.js 2                       |
|           后端           |                     Spring Boot                     |
|     前端构建环境     | Node.js 14, Npm 6.14,[vue-cli3](https://cli.vuejs.org/) |
|     后端运行环境     |                        Java11                        |
|        后端构建        |                      Maven 3.6                      |
|       数据库版本       |         MySQL 8.0，Redis 6.0，MongoDB 4.4         |
| 服务发现与配置中心 |                     Nacos 1.4.1                     |
|           搜索           |                 Elasticsearch 7.10.1                 |
|        消息队列        |                     Kafka 2.7.0                     |
|      数据同步canal      |                 Alibaba Canal 1.1.5                 |
|      对象存储      |                 MinIO                 |
|      文档转换      |                 Libre Office 6.4                 |


## 快速运行

修改配置文件，设置好各个中间件地址

### 方法一

首先进入 common 中执行

```bash
mvn clean install
```

之后依次编译打包其它组件运行


### 方法二

直接导入 IDEA 运行


### 构建前端

具体细节请查看前端web-v2文件下 README

构建完成后进入

```bash
127.0.0.1:8000
```

查看运行结果


## 简介 describe

即将开发完成，基本功能如下

### 评价

教师以班级和课程为分组发布作业，设置结束时间与开始时间

可以提前结束与延长时间

结束后开启作业互评

学生可以对作业进行评价打分

每个班级课程设置讨论区

### 用户

采用邀请码或邀请链接注册或者教师导入账号

邀请码需要由教师创建，学生输入邀请码或链接进行注册，注册成功后自动加入课程班级

已经注册的学生可以自动导入

### 角色

学生，基本权限，提交作业，评价作业，提问

助教，包含学生的所有权限，禁言，删帖，审查作业与批改作业权限

教师，包含助教的所有权限，拥有设置助教删除助教，发布作业，设置作业状态，创建班级课程，导入学生名单，创建邀请码

论坛版主：负责管理所关联的话题下的论坛内容

管理员，拥有全部权限

