package br.one.forum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternTest {

    private static final Pattern PATTERN = Pattern.compile("^\\{.+}$");

    @Test
    public void shouldValidateStringWithContentInsideBraces() {
        assertTrue(PATTERN.matcher("{abc}").matches());
        assertTrue(PATTERN.matcher("{123}").matches());
        assertTrue(PATTERN.matcher("{a1b2}").matches());
    }

    @Test
    public void shouldNotValidateEmptyBraces() {
        assertFalse(PATTERN.matcher("{}").matches());
    }

    @Test
    public void shouldNotValidateMissingOpeningBrace() {
        assertFalse(PATTERN.matcher("abc}").matches());
    }

    @Test
    public void shouldNotValidateMissingClosingBrace() {
        assertFalse(PATTERN.matcher("{abc").matches());
    }

    @Test
    public void shouldNotValidateStringWithoutBraces() {
        assertFalse(PATTERN.matcher("abc").matches());
    }

}
