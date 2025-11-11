package br.one.forum;

import net.datafaker.Faker;

import java.util.Locale;

public class DataFaker {
    private static Faker instance;

    public static Faker faker() {
        if (instance == null) {
            instance = new Faker(Locale.of("pt", "BR"));
        }
        return instance;
    }


}
