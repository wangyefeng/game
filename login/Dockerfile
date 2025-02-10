FROM openjdk:23-jdk-slim

# 将本地的 JAR 文件复制到容器中的工作目录
COPY login/target/login-1.0.0-SNAPSHOT.jar /app/login.jar
# 将本地的 hotfix.sh 文件复制到容器中的工作目录
COPY login/hotfix.sh /app/hotfix.sh
# 将arthas-bin目录复制到容器的/app/arthas-bin目录下
COPY arthas-bin/ /app/arthas-bin/

EXPOSE 80

# 设置工作目录
WORKDIR /app

RUN apt-get update && apt-get install -y curl

# 容器启动时执行的命令
CMD ["java", "-javaagent:arthas-bin/arthas-agent.jar", "-jar", "login.jar"]