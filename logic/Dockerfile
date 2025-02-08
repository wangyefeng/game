FROM openjdk:23-jdk-slim

# 将本地的 JAR 文件复制到容器中的工作目录
COPY logic/target/logic-1.0.0-SNAPSHOT.jar /app/logic.jar

# 将arthas-bin目录复制到容器的/app/arthas-bin目录下
COPY arthas-bin/ /app/arthas-bin/

EXPOSE 8080 50051 9999

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-jar", "logic.jar"]
