FROM openjdk:24-jdk-slim

# 安装curl
RUN apt-get update && apt-get install -y curl

# 将本地的 hotfix.sh 文件复制到容器中的工作目录
COPY login/hotfix.sh /app/hotfix.sh
# 将arthas-bin目录复制到容器的/app/arthas-bin目录下
COPY arthas-bin/ /app/arthas-bin/
# 将本地的 JAR 文件复制到容器中的工作目录
COPY login/target/login-1.0.0-SNAPSHOT.jar /app/login.jar

EXPOSE 80

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-javaagent:arthas-bin/arthas-agent.jar", "-jar", "login.jar"]