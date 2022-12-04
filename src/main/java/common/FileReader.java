package common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileReader {
    public static <T> List<T> readAllLines(String fileName, Function<String, T> converter) {
        List<T> result = new ArrayList<>();

        InputStream stream = FileReader.class.getClassLoader().getResourceAsStream(fileName);
        try {
            assert stream != null;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.add(converter.apply(line));
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return result;
    }
}
