package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day9 implements AocDay<Integer> {
    @Override
    public Integer exercise1(String fileName) {
        List<Instruction> input = FileReader.readAllLines(fileName, line -> new Instruction(line.split(" ")));
        Set<Point> visited = new HashSet<>();
        Point head = new Point(0, 0);
        Point tail = new Point(0, 0);
        for (Instruction instruction: input) {
            for (int i=0; i<instruction.steps;i++) {
                head = head.move(instruction.direction);
                visited.add(tail);
                tail = tail.moveTail(head);
            }
        }

        visited.add(tail);
        return visited.size();
    }

    @Override
    public Integer exercise2(String fileName) {
        List<Instruction> input = FileReader.readAllLines(fileName, line -> new Instruction(line.split(" ")));
        Set<Point> visited = new HashSet<>();
        Point[] tail = Collections.nCopies(10, new Point(0, 0)).toArray(new Point[]{});
        for (Instruction instruction: input) {
            for (int i=0; i<instruction.steps;i++) {
                tail[0] = tail[0].move(instruction.direction);
                visited.add(tail[9]);
                for (int j=1;j<tail.length;j++) {
                    tail[j] = tail[j].moveTail(tail[j-1]);
                }
                System.out.println("(" + tail[9].x + ", " + tail[9].y + ")");
            }
        }

        visited.add(tail[9]);
        return visited.size();
    }

    private record Point(int x, int y) {
        public Point move(DirectionEnum dir) {
            return switch(dir) {
                case UP -> new Point(x, y + 1);
                case DOWN -> new Point(x, y - 1);
                case LEFT -> new Point(x - 1, y);
                case RIGHT -> new Point(x + 1, y);
            };
        }

        public Point moveTail(Point reference) {
            if (Math.abs(reference.x - x) > 1 || Math.abs(reference.y - y) > 1) {
                return new Point(x + (reference.x - x) / Math.max(1, Math.abs(reference.x - x)),
                    y + (reference.y - y) / Math.max(1, Math.abs(reference.y - y)));
            }

            return this;
        }
    }

    private record Instruction(DirectionEnum direction, int steps) {
        Instruction(String[] line) {
            this(DirectionEnum.fromName(line[0]), Integer.parseInt(line[1]));
        }
    }

    private enum DirectionEnum {
        UP("U"), DOWN("D"), LEFT("L"), RIGHT("R");

        private final String name;

        DirectionEnum(String name) {
            this.name = name;
        }

        public static DirectionEnum fromName(String name) {
            for (DirectionEnum dir: DirectionEnum.values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }

            throw new IllegalArgumentException(name);
        }
    }
}
