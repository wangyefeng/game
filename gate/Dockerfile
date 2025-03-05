FROM openjdk:24-jdk-slim

# 将本地的 JAR 文件复制到容器中的工作目录
COPY gate/target/gate-1.0.0-SNAPSHOT.jar /app/gate.jar

# 将arthas-bin目录复制到容器的/app/arthas-bin目录下
COPY arthas-bin/ /app/arthas-bin/

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-jar", "gate.jar"]
