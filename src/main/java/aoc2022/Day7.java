package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day7 implements AocDay<Long> {
    private final Pattern DOWN = Pattern.compile("\\$ cd (\\S+)");

    private final Pattern DIRECTORY = Pattern.compile("dir (\\S+)");

    private final Pattern FILE = Pattern.compile("(\\d+) (\\S+)");
    @Override
    public Long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        Node root = buildGraph(input);
        Counter c = new Counter();
        sumSmallDirectories(root, val -> { if (val < 100000) { c.add(val); }});
        return c.get();
    }

    private long sumSmallDirectories(Node root, Consumer<Long> accumulator) {
        if (root.children.size() == 0) {
            return root.size;
        }

        long childSizeSum = root.children.values().stream().map(child -> sumSmallDirectories(child, accumulator))
            .reduce(0L, Long::sum);

        accumulator.accept(childSizeSum);

        return childSizeSum;
    }

    private Node buildGraph(List<String> input) {
        Node current = null;
        for(String line: input) {
            Matcher mDown = DOWN.matcher(line);
            Matcher mDirectory = DIRECTORY.matcher(line);
            Matcher mFile = FILE.matcher(line);

            if ("$ cd /".equals(line)) {
                current = new Node("/", 0L, null, new HashMap<>());
            } else if ("$ cd ..".equals(line)) {
                current = current.parent;
            } else if ("$ ls".equals(line)) {

            } else if (mDown.find()) {
                current = current.children.get(mDown.group(1));
            } else if (mFile.find()) {
                Node newNode = new Node(mFile.group(2), Long.valueOf(mFile.group(1)), current, new HashMap<>());
                current.children.put(mFile.group(2), newNode);
            } else if (mDirectory.find()) {
                Node newNode = new Node(mDirectory.group(1), 0L, current, new HashMap<>());
                current.children.put(mDirectory.group(1), newNode);
            }
        }

        while(current.parent != null) {
            current = current.parent;
        }

        return current;
    }

    @Override
    public Long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        Node root = buildGraph(input);
        List<Long> dirSizes = new ArrayList<>();
        long totalAvailable = 40000000;
        long totalTaken = sumSmallDirectories(root, val -> dirSizes.add(val));
        System.out.println(dirSizes);
        return dirSizes.stream().sorted().dropWhile(val -> val < (totalTaken - totalAvailable)).findFirst().get();
    }

    private record Node(String name, Long size, Node parent, Map<String, Node> children) {
        Node resize(long newSize) {
            return new Node(this.name, newSize, this.parent, this.children);
        }
    }

    private class Counter {
        private long value = 0;

        public void add(long increment) {
            value += increment;
        }

        public long get() {
            return value;
        }
    }
}
