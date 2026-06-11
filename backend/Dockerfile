FROM maven:3.8.4-openjdk-8-slim AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:8-jre-slim

WORKDIR /app

ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=builder /app/target/accompany-system.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "accompany-system.jar"]
