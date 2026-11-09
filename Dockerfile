# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Etapa 1: compilar el proyecto y armar el WAR sin depender de NetBeans.
# Se usa la imagen de Tomcat porque ya trae el JDK y el servlet-api.jar.
# ---------------------------------------------------------------------------
FROM tomcat:11.0-jdk21-temurin AS build

WORKDIR /build

COPY src ./src
COPY lib ./lib
COPY web ./web

RUN mkdir -p build/classes build/war/WEB-INF/classes build/war/WEB-INF/lib \
    && find src/java -name '*.java' > /tmp/sources.txt \
    && javac --release 21 -encoding UTF-8 \
        -cp "lib/*:/usr/local/tomcat/lib/servlet-api.jar" \
        -d build/classes @/tmp/sources.txt \
    && cp -R web/. build/war/ \
    && cp -R build/classes/. build/war/WEB-INF/classes/ \
    && cp lib/*.jar build/war/WEB-INF/lib/ \
    && cd build/war \
    && jar --create --file ../API_HealthBlood_512.war .

# ---------------------------------------------------------------------------
# Etapa 2: imagen final, solo con Tomcat y el WAR.
# ---------------------------------------------------------------------------
FROM tomcat:11.0-jre21-temurin

ENV LANG=C.UTF-8 \
    JAVA_OPTS="-Xms64m -Xmx256m -Dfile.encoding=UTF-8"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /build/build/API_HealthBlood_512.war \
     /usr/local/tomcat/webapps/API_HealthBlood_512.war

COPY docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
