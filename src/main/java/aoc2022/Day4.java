package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 implements AocDay<Long> {

    Pattern pattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");

    @Override
    public Long exercise1(String fileName) {
        List<AssignmentPair> input = FileReader.readAllLines(fileName, str -> {
            Matcher m = pattern.matcher(str);
            m.find();
            return new AssignmentPair(new Assignment(m.group(1), m.group(2)), new Assignment(m.group(3), m.group(4)));
        });
        return input.stream().filter(ap -> contains(ap.first, ap.second)).count();
    }

    private boolean contains(Assignment first, Assignment second) {
        return (first.from <= second.from && first.to >= second.to) ||
            (first.from >= second.from && first.to <= second.to);
    }

    @Override
    public Long exercise2(String fileName) {
        List<AssignmentPair> input = FileReader.readAllLines(fileName, str -> {
            Matcher m = pattern.matcher(str);
            m.find();
            return new AssignmentPair(new Assignment(m.group(1), m.group(2)), new Assignment(m.group(3), m.group(4)));
        });
        return input.stream().filter(ap -> overlaps(ap.first, ap.second)).count();
    }

    private boolean overlaps(Assignment first, Assignment second) {
        return contains(first, second) || ((first.from <= second.from && second.from <= first.to) ||
            (first.from <= second.to && second.to <= first.to));
    }

    private record AssignmentPair(Assignment first, Assignment second) {
    }

    private record Assignment(int from, int to) {
        Assignment(String from, String to) {
            this(Integer.parseInt(from), Integer.parseInt(to));
        }
    }
}
