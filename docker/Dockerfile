FROM registry.gs.youyuwo.com/base/docker-springboot-pinpoint:latest

COPY bazooka-gateway/target/bazooka-gateway-1.0.0.RELEASE.jar /app.jar

ENV PATH="${JAVA_HOME}/bin:${PATH}"

EXPOSE 6060

CMD ["java","-jar","app.jar"]
