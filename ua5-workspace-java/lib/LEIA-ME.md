# lib/ — JARs opcionais

O workspace roda **só com o JDK**. Esta pasta existe para UM caso opcional: o adaptador **JDBC** da Aula 20.

O JDK traz a API JDBC (`java.sql`), mas não traz o *driver* de nenhum banco. Sem driver, os testes de JDBC aparecem como **PULADO** e tudo o mais funciona (o adaptador em arquivo CSV cumpre o mesmo papel).

Para ligar o JDBC, baixe **um** destes arquivos e coloque-o aqui:

| Banco | Arquivo | Onde baixar |
|---|---|---|
| **H2** (recomendado: 100% Java, um arquivo só) | `h2-2.2.224.jar` | <https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar> |
| SQLite | `sqlite-jdbc-3.46.1.3.jar` | <https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.1.3/sqlite-jdbc-3.46.1.3.jar> |

O `ferramentas/Rodar.java` coloca automaticamente no classpath todo `.jar` desta pasta. Os `.jar` não vão para o Git (`.gitignore`).
