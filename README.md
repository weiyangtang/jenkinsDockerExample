# jenkins多分支流水线部署
> 本文将利用jenkins的多分支流水线的部署方式，并结合docker实现根据branch和tag部署及回滚
>
> 文中演示的项目地址：
> `https://github.com/weiyangtang/jenkinsDockerExample`
>
>`https://github.com/weiyangtang/jenkinsDockerExample.git`
>
> **`jenkinsfile`和`Dockerfile`在文末附录**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720155019701.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)


1. 创建一个多分支流水线项目

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153040213.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)2. 配置

   - 显示名称（不太重要）

   - 分支源（非常重要）,添加Git远程仓库的url，选择用户凭证（username/password,  ssh private key）,行为选择发现分支、发现标签

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153039646.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)
   - 填写jenkinsfile在git仓库的相对路径
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153039598.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)

   - 保存

     

3. 立即扫描多分支流水线![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153042250.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)
4. 选择项目的版本号tag(标签)，本文以1.1为例开始部署
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020072015304045.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153040151.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)

5. 查看运行日志

   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200720153043549.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)6. 各阶段的耗时

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020072015304325.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXlhbmdfdGFuZw==,size_16,color_FFFFFF,t_70)

> ## `jenkinsfile`

```groovy
#!/usr/bin/env groovy

final def releaseTag = (env.TAG_NAME ?: env.BRANCH_NAME).replace("/", "-")

pipeline {

    agent any

    tools {
        maven 'maven3'
    }


    stages {
        stage("maven 编译") {
            steps {
                echo "releaseTag:${releaseTag}"
                echo 'Building..'
                sh 'mvn clean package -Dmaven.test.skip'
            }
        }
        stage('镜像编译') {
            steps {
                echo 'image build'
                sh "docker build -f ./src/main/docker/Dockerfile -t tangweiyang/jenkinsdocker:${releaseTag} . "
            }
        }
        stage('镜像发布') {
            steps {
                echo 'image push docker hub'
                sh "docker push tangweiyang/jenkinsdocker:${releaseTag} "
            }
        }
        stage('停止旧容器') {
            steps {
                sh '''             
                        docker rm -f jenkinsdocker &> /dev/null                  
                   '''
            }
        }
        stage('部署') {
            steps {
                echo 'deloy'
                sh "docker run -it -d -p 8081:80 --name jenkinsdocker tangweiyang/jenkinsdocker:${releaseTag}"
            }
        }
    }
}
```

> ## `Dockerfile`

```dockerfile
FROM openjdk:8u171-jre-alpine
ENV JAR_NAME=jenkinsDocker-1.0-SNAPSHOT.jar
COPY ./target/$JAR_NAME  /apps/srv/instance/$JAR_NAME
EXPOSE 80
#ENTRYPOINT ["java","-jar","/apps/srv/instance/jenkinsDocker-1.0-SNAPSHOT.jar","--server.port=80"]
ENTRYPOINT java -jar /apps/srv/instance/$JAR_NAME --server.port=80
```

