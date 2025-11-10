package br.one.forum.factories;

import java.util.List;
import java.util.Random;

public class FakeCategoryFactory {
    private static final List<String> categories = List.of(
            "Programação",
            "Banco de Dados",
            "Desenvolvimento Web",
            "Mobile",
            "Inteligência Artificial",
            "DevOps",
            "Segurança da Informação",
            "UI/UX Design",
            "Ciência de Dados",
            "Engenharia de Software"
    );

    public static String get() {
        return categories.get(new Random().nextInt(0, categories.size()));
    }
}
