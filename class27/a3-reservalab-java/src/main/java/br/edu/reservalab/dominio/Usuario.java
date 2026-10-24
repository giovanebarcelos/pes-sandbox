package br.edu.reservalab.dominio;

/** Só matrícula, nome e perfil: nada além do necessário (LGPD, RNF-05). */
public record Usuario(String matricula, String nome, Perfil perfil) { }
