package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.function.Function;
import java.util.stream.Collectors;

public class Day6 implements AocDay<Integer> {
    @Override
    public Integer exercise1(String fileName) {
        String input = FileReader.readAllLines(fileName, Function.identity()).get(0);
        return findFirst4CharacterNonRepeatingPosition(input, 4);
    }

    private Integer findFirst4CharacterNonRepeatingPosition(String input, int markerLength) {
        for (int i=markerLength;i<input.length();i++) {
            if (input.substring(i - markerLength, i).chars().boxed().collect(Collectors.toSet()).size() == markerLength) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Integer exercise2(String fileName) {
        String input = FileReader.readAllLines(fileName, Function.identity()).get(0);
        return findFirst4CharacterNonRepeatingPosition(input, 14);
    }
}
