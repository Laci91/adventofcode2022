package aoc2022;

import com.google.common.base.Splitter;
import common.AocDay;
import common.FileReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day14 implements AocDay<Integer> {
    @Override
    public Integer exercise1(String fileName) {
        List<List<Point>> input = FileReader.readAllLines(fileName,
            str -> Splitter.on(" -> ")
                .splitToList(str)
                .stream()
                .map(s -> {
                    String[] coordinates = s.split(",");
                    return new Point(coordinates[0], coordinates[1]);
                })
                .collect(Collectors.toList()));
        Set<Point> occupied = new HashSet<>();
        for (List<Point> points: input) {
            occupied.addAll(processChain(points));
        }

        int wallCount = occupied.size();
        Optional<Point> restingPlace;
        do {
            restingPlace = dropSand(new Point(500, 0), occupied);
            restingPlace.ifPresent(occupied::add);
        } while(restingPlace.isPresent());
        return occupied.size() - wallCount;
    }

    @Override
    public Integer exercise2(String fileName) {
        List<List<Point>> input = FileReader.readAllLines(fileName,
            str -> Splitter.on(" -> ")
                .splitToList(str)
                .stream()
                .map(s -> {
                    String[] coordinates = s.split(",");
                    return new Point(coordinates[0], coordinates[1]);
                })
                .collect(Collectors.toList()));
        Set<Point> occupied = new HashSet<>();
        for (List<Point> points: input) {
            occupied.addAll(processChain(points));
        }

        int wallCount = occupied.size();
        int floor = occupied.stream().map(Point::y).max(Comparator.naturalOrder()).orElse(0) + 2;
        Optional<Point> restingPlace;
        do {
            restingPlace = dropSand(new Point(500, 0), occupied, floor);
            restingPlace.ifPresent(occupied::add);

            if (occupied.size() % 500 == 0) {
                System.out.println(occupied.size());
            }
        } while(restingPlace.filter(rp -> rp.equals(new Point(500, 0))).isEmpty());
        return occupied.size() - wallCount;
    }

    private Optional<Point> dropSand(Point p, Set<Point> occupied) {
        return dropSand(p, occupied, -1);
    }

    private Optional<Point> dropSand(Point p, Set<Point> occupied, int floor) {
        Optional<Point> belowOccupiedSpace = occupied.stream()
            .filter(occ -> occ.x == p.x && occ.y > p.y)
            .min(Comparator.comparing(Point::y));

        if (belowOccupiedSpace.isEmpty() && floor == -1) {
            return Optional.empty();
        } else if (belowOccupiedSpace.isEmpty()) {
            return Optional.of(new Point(p.x, floor - 1));
        }

        Point verticalStopper = belowOccupiedSpace.get();
        Point verticalStop = new Point(verticalStopper.x, verticalStopper.y - 1);
        Point leftDown = new Point(verticalStop.x - 1, verticalStop.y + 1);
        Point rightDown = new Point(verticalStop.x + 1, verticalStop.y + 1);
        if (!occupied.contains(leftDown)) {
            return dropSand(leftDown, occupied, floor);
        } else if (!occupied.contains(rightDown)) {
            return dropSand(rightDown, occupied, floor);
        } else {
            return Optional.of(verticalStop);
        }
    }

    private Set<Point> processChain(List<Point> chain) {
        Set<Point> result = new HashSet<>();
        Point prev = chain.remove(0);
        while(!chain.isEmpty()) {
            Point curr = chain.remove(0);
            if (curr.x == prev.x) {
                result.addAll(stream(curr.y, prev.y).mapToObj(n -> new Point(curr.x, n)).collect(Collectors.toSet()));
            } else {
                result.addAll(stream(curr.x, prev.x).mapToObj(n -> new Point(n, curr.y)).collect(Collectors.toSet()));
            }

            prev = curr;
        }

        return result;
    }

    private static IntStream stream(int curr, int prev) {
        return curr <= prev ? IntStream.rangeClosed(curr, prev) : IntStream.rangeClosed(prev, curr);
    }

    private record Point(int x, int y) {
        Point(String x, String y) {
            this(Integer.parseInt(x), Integer.parseInt(y));
        }
    }
}
