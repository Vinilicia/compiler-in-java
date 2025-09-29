package org.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {

    private final List<Character> characters = new ArrayList<>();

    public SourceReader(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));

        for (char c : content.toCharArray()) {
            characters.add(c);
        }
    }

    public List<Character> getCharacters() {
        return new ArrayList<>(characters);
    }

    @Override
    public String toString() {
        return characters.toString();
    }
}
