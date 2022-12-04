package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.List;

public class Day4 implements AocDay {

    @Override
    public long exercise1(String fileName) {
        List<String[]> input = FileReader.readAllLines(fileName, str -> str.split(","));
        return input.stream().filter(this::contains).count();
    }

    private boolean contains(String[] assignments) {
        Assignment first = new Assignment(assignments[0].split("-"));
        Assignment second = new Assignment(assignments[1].split("-"));
        return (first.from <= second.from && first.to >= second.to) ||
            (first.from >= second.from && first.to <= second.to);
    }

    @Override
    public long exercise2(String fileName) {
        List<String[]> input = FileReader.readAllLines(fileName, str -> str.split(","));
        return input.stream().filter(this::overlaps).count();
    }

    private boolean overlaps(String[] assignments) {
        Assignment first = new Assignment(assignments[0].split("-"));
        Assignment second = new Assignment(assignments[1].split("-"));
        return contains(assignments) || ((first.from <= second.from && second.from <= first.to) ||
            (first.from <= second.to && second.to <= first.to));
    }

    private record Assignment(int from, int to) {
        Assignment(String[] arr) {
            this(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        }
    }
}
