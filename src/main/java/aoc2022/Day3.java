package aoc2022;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.AocDay;
import common.FileReader;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day3 implements AocDay {

    @Override
    public long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return input.stream().map(this::splitInHalf).map(this::findCommonLetter).map(this::calculateScore).reduce(0L, Long::sum);
    }

    private char findCommonLetter(String[] arr) {
        Set<Character> first = arr[0].chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        Set<Character> second = arr[1].chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        return Sets.intersection(first, second).stream().findFirst().orElseThrow();
    }

    private long calculateScore(char letter) {
        return letter > 96 ? letter - 96 : letter - 38;
    }

    private String[] splitInHalf(String input) {
        return new String[] { input.substring(0, input.length() / 2), input.substring(input.length() / 2)};
    }

    @Override
    public long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return Lists.partition(input, 3).stream().map(this::findCommonLetter).map(this::calculateScore).reduce(0L, Long::sum);
    }

    private char findCommonLetter(List<String> group) {
        Set<Character> first = group.get(0).chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        Set<Character> second = group.get(1).chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        Set<Character> third = group.get(2).chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        return Sets.intersection(Sets.intersection(first, second), third).stream().findFirst().orElseThrow();
    }
}
