package teste;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um método de teste. Mesmo nome e uso do JUnit 5 ({@code org.junit.jupiter.api.Test}).
 *
 * Este pacote "teste" é um mini-JUnit feito só com o JDK, para o curso rodar em
 * laboratório sem internet e sem Maven/Gradle. Para migrar para o JUnit de verdade,
 * basta trocar {@code import teste.*} por {@code import org.junit.jupiter.api.*}
 * e {@code import static teste.Assertions.*} por
 * {@code import static org.junit.jupiter.api.Assertions.*}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
