package org.lexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {

    private final List<Character> characters = new ArrayList<>();

    public SourceReader(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);

        String content = new String(in.readAllBytes());

        for (char c : content.toCharArray()) {
            characters.add(c);
        }
        characters.add(' ');
        in.close();
    }

    public List<Character> getCharacters() {
        return new ArrayList<>(characters);
    }

    @Override
    public String toString() {
        return characters.toString();
    }
}
