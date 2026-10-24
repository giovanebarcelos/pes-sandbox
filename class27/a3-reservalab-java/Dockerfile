FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# 1) dependências em camada própria: só é refeita se o pom.xml mudar
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 2) código; o teste rápido baixa o provider do JUnit para que "run testes" funcione offline
COPY src ./src
RUN mvn -B -q test -Dtest=CatalogoTest

CMD ["mvn", "-B", "-q", "-o", "compile", "exec:java"]
