FROM openjdk:8-jre-alpine
ADD yenza-ms-uaa-1.0.jar yenza-ms-uaa.jar
ADD docker_app_runner.sh docker_app_runner.sh
RUN apk add --no-cache unzip

RUN chmod 555 docker_app_runner.sh

ADD local_policy.jar ${JAVA_HOME}/jre/lib/security
ADD US_export_policy.jar ${JAVA_HOME}/jre/lib/security

ENV JAVA_OPTS="-Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -server -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.awt.headless=true -XX:NewSize=512m -Xss1M -Xms1G -Xmx1G -XX:MaxDirectMemorySize=10M"

EXPOSE 8080
ENTRYPOINT ["./docker_app_runner.sh"]