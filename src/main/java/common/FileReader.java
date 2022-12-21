package common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class FileReader {

    public static <T> List<T> readAllLines(String fileName, Function<String, T> converter) {
     return readAllLines(fileName, converter, any -> true);
    }

    public static <T> List<T> readAllLines(String fileName, Function<String, T> converter, Predicate<String> condition) {
        List<T> result = new ArrayList<>();

        InputStream stream = FileReader.class.getClassLoader().getResourceAsStream(fileName);
        try {
            assert stream != null;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (condition.test(line)) {
                        result.add(converter.apply(line));
                    }
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return result;
    }
}
