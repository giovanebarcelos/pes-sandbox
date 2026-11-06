package br.edu.pes.boletim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Boletim API - laboratorio de GitHub Actions (Aula 26B). */
@SpringBootApplication
public class BoletimApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoletimApplication.class, args);
    }

    @Bean
    CalculadoraBoletim calculadoraBoletim() {
        return new CalculadoraBoletim();
    }
}
