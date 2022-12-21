package aoc2022;

import common.AocDay;
import common.FileReader;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day15 implements AocDay<BigDecimal> {

    Pattern INPUT_PATTERN = Pattern.compile("Sensor at x=(-?[0-9]+), y=(-?[0-9]+): closest beacon is at x=(-?[0-9]+), y=(-?[0-9]+)");

    @Override
    public BigDecimal exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        String lineStr = input.remove(0);
        int line = Integer.parseInt(lineStr.split("=")[1]);
        input.remove(0);
        Set<Sensor> sensors = input.stream().map(i -> {
            Matcher m = INPUT_PATTERN.matcher(i);
            if (m.matches()) {
                int x = Integer.parseInt(m.group(1)); int y = Integer.parseInt(m.group(2));
                int beaconX = Integer.parseInt(m.group(3)); int beaconY = Integer.parseInt(m.group(4));
                return new Sensor(x, y, Math.abs(x - beaconX) + Math.abs(y - beaconY));
            }
            throw new IllegalArgumentException();
        }).collect(Collectors.toSet());
        Set<Point> beacons = input.stream().map(i -> {
            Matcher m = INPUT_PATTERN.matcher(i);
            if (m.matches()) {
                int beaconX = Integer.parseInt(m.group(3)); int beaconY = Integer.parseInt(m.group(4));
                return new Point(beaconX, beaconY);
            }
            throw new IllegalArgumentException();
        }).collect(Collectors.toSet());

        Set<Point> excluded = sensors.stream().flatMap(s -> s.getExcluded(line)).collect(Collectors.toSet());
        return BigDecimal.valueOf(excluded.size() - (int)beacons.stream().filter(p -> p.y == line).count());
    }

    @Override
    public BigDecimal exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        input.remove(0);
        int upperBound = Integer.parseInt(input.remove(0).split("=")[1]);
        Point upper = new Point(upperBound, upperBound);
        Set<Sensor> sensors = input.stream().map(i -> {
            Matcher m = INPUT_PATTERN.matcher(i);
            if (m.matches()) {
                int x = Integer.parseInt(m.group(1)); int y = Integer.parseInt(m.group(2));
                int beaconX = Integer.parseInt(m.group(3)); int beaconY = Integer.parseInt(m.group(4));
                return new Sensor(x, y, Math.abs(x - beaconX) + Math.abs(y - beaconY));
            }
            throw new IllegalArgumentException();
        }).collect(Collectors.toSet());

        Point lower = new Point (0, 0);
        Point current = lower;
        while(current.y <= upper.y) {
            final Point referencePoint = current;
            int steps = sensors.stream().map(s -> s.endOfHorizontalRange(referencePoint)).filter(n -> n != 0).max(Comparator.naturalOrder()).orElse(-1) + 1;
            if (steps == 0) {
                return BigDecimal.valueOf(current.x).multiply(BigDecimal.valueOf(4000000)).add(BigDecimal.valueOf(current.y));
            }

            if (current.x + steps > upper.x) {
                current = new Point(lower.x, current.y + 1);
            } else {
                current = new Point(current.x + steps, current.y);
            }
        }

        throw new RuntimeException();
    }

    private record Sensor(int x, int y, int covers) {

        public Stream<Point> getExcluded(int line) {
            int verticalDistance = Math.abs(y - line);
            if (verticalDistance > covers) {
                return Stream.empty();
            }
            return IntStream.rangeClosed(x - (covers - verticalDistance), x + (covers - verticalDistance))
                .mapToObj(n -> new Point(n, line));
        }

        public int endOfHorizontalRange(Point p) {
            if (Math.abs(x - p.x) + Math.abs(y - p.y) > covers) {
                return 0;
            }
            int verticalDistance = Math.abs(y - p.y);
            return x - p.x + covers - verticalDistance;
        }
    }

    public record Point(int x, int y) {
    }
}
