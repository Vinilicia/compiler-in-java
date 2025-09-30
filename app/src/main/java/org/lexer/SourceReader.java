package org.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {

    private final List<Character> characters = new ArrayList<>();

    public SourceReader(String resourceName) throws IOException {
        InputStream in = getClass().getResourceAsStream("/" + resourceName);
        if (in == null) {
            throw new IOException("Resource not found: " + resourceName);
        }

        String content = new String(in.readAllBytes());

        for (char c : content.toCharArray()) {
            characters.add(c);
        }
        characters.add(' ');
    }

    public List<Character> getCharacters() {
        return new ArrayList<>(characters);
    }

    @Override
    public String toString() {
        return characters.toString();
    }
}
