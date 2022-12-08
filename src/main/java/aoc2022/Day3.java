package aoc2022;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.AocDay;
import common.FileReader;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day3 implements AocDay<Long> {

    @Override
    public Long exercise1(String fileName) {
        List<Bag> input = FileReader.readAllLines(fileName, Bag::new);
        return input.stream().map(this::findCommonLetter).map(this::calculateScore).reduce(0L, Long::sum);
    }

    private int findCommonLetter(Bag bag) {
        return Sets.intersection(bag.first, bag.second).iterator().next();
    }

    private long calculateScore(int letter) {
        return letter > 96 ? letter - 96 : letter - 38;
    }

    @Override
    public Long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return Lists.partition(input, 3).stream().map(this::findCommonLetter).map(this::calculateScore).reduce(0L, Long::sum);
    }

    private int findCommonLetter(List<String> group) {
        Set<Integer> first = group.get(0).chars().boxed().collect(Collectors.toSet());
        Set<Integer> second = group.get(1).chars().boxed().collect(Collectors.toSet());
        Set<Integer> third = group.get(2).chars().boxed().collect(Collectors.toSet());
        return Sets.intersection(Sets.intersection(first, second), third).stream().findFirst().orElseThrow();
    }

    private record Bag(Set<Integer> first, Set<Integer> second) {
        Bag(String first, String second) {
            this(first.chars().boxed().collect(Collectors.toSet()),
                second.chars().boxed().collect(Collectors.toSet()));
        }

        Bag(String full) {
            this(full.substring(0, full.length()/2), full.substring(full.length()/2));
        }
    }
}
