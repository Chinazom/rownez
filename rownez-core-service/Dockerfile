FROM maven:3-openjdk-17 AS mvnbuild
COPY . .
RUN mvn clean package -Dskiptests





FROM openjdk:17

EXPOSE 8080

LABEL maintainer="chinazom.nnoli"

COPY --from=mvnbuild /target/rownez-resort.jar rownez-resort.jar

ENTRYPOINT ["java", "-jar", "rownez-resort.jar"]
