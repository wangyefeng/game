FROM openjdk:23-jdk-slim

# 将本地的 JAR 文件复制到容器中的工作目录
COPY target/login-1.0.0-SNAPSHOT.jar /app/login.jar

EXPOSE 80

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-jar", "login.jar"]
