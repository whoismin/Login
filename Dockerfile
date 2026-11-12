# ---------- Estágio 1: compilar a aplicação com GraalVM ----------
FROM ghcr.io/graalvm/jdk-community:17 AS build

RUN microdnf install -y tar gzip curl && microdnf clean all
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz \
    | tar -xz -C /opt \
    && ln -s /opt/apache-maven-3.9.9/bin/mvn /usr/bin/mvn

WORKDIR /build
COPY . .
RUN mvn -B -pl springframework -am clean package -DskipTests

# ---------- Estágio 2: imagem final (só o necessário para rodar) ----------
FROM ghcr.io/graalvm/jdk-community:17
LABEL MANTAINER="FATEC"

WORKDIR /app
COPY --from=build /build/springframework/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
