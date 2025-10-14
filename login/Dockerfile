FROM openjdk:25-jdk

# 将本地的 hotfix.sh 文件复制到容器中的工作目录
COPY login/hotfix.sh /app/hotfix.sh
# 将本地的 JAR 文件复制到容器中的工作目录
COPY login/target/login-1.0.0-SNAPSHOT.jar /app/login.jar

EXPOSE 10000

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-jar", "login.jar"]