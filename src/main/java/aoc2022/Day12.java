package aoc2022;

import com.google.common.collect.Sets;
import common.AocDay;
import common.FileReader;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class Day12 implements AocDay<Integer> {
    @Override
    public Integer exercise1(String fileName) {
        Set<Node> nodes = getNodes(fileName);
        breadthFirstSearch(1, nodes.stream().filter(nd -> nd.start).collect(Collectors.toSet()));
        return nodes.stream().filter(nd -> nd.end).findAny().map(nd -> nd.distance).orElse(Integer.MAX_VALUE);
    }

    private Set<Node> getNodes(String fileName) {
        char[][] input = FileReader.readAllLines(fileName, String::toCharArray).toArray(new char[0][0]);
        Node start = null;
        Map<Point, Node> nodes = new HashMap<>();
        for (int i=0;i<input.length;i++) {
            for (int j=0;j<input[i].length;j++) {
                Point p = new Point(i, j);
                Node n = switch (input[i][j]) {
                    case 'S' -> {
                        start = Node.startNode(p);
                        yield start;
                    }
                    case 'E' -> Node.endNode(p);
                    default -> new Node(p, input[i][j]);
                };
                for (Point neighbour: p.neighbours()) {
                    Node other = nodes.get(neighbour);
                    if (other == null) {
                        continue;
                    }

                    if (other.elevation - n.elevation <= 1) {
                        n.connections.add(other);
                    }

                    if (n.elevation - other.elevation <= 1) {
                        other.connections.add(n);
                    }
                }

                nodes.put(p, n);
            }
        }

        return Sets.newHashSet(nodes.values());
    }

    @Override
    public Integer exercise2(String fileName) {
        Set<Node> nodes = getNodes(fileName);
        breadthFirstSearch(1, nodes.stream().filter(nd -> nd.elevation == 'a').collect(Collectors.toSet()));
        return nodes.stream().filter(nd -> nd.end).findAny().map(nd -> nd.distance).orElse(Integer.MAX_VALUE);
    }

    private void breadthFirstSearch(int depth, Set<Node> currentNodes) {
        if (currentNodes.isEmpty()) {
            return;
        }

        Set<Node> newNodes = currentNodes.stream().flatMap(nd -> nd.connections.stream()).filter(nd -> nd.distance > depth).collect(Collectors.toSet());
        newNodes.forEach(nd -> nd.distance = depth);

        breadthFirstSearch(depth + 1, newNodes);
    }

    private record Point(int x, int y) {
        List<Point> neighbours() {
            return List.of(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1));
        }
    }

    private static class Node {
        private Point place;
        private char elevation;
        private int distance;
        private Set<Node> connections;
        private boolean start;
        private boolean end;

        Node(Point place, char elevation, Set<Node> connections, boolean start, boolean end) {
            this.place = place;
            this.elevation = elevation;
            this.distance = Integer.MAX_VALUE;
            this.connections = connections;
            this.start = start;
            this.end = end;
        }

        Node(Point place, char elevation) {
            this(place, elevation, new HashSet<>(), false, false);
        }

        static Node startNode(Point place) {
            return new Node(place, 'a', new HashSet<>(), true, false);
        }

        static Node endNode(Point place) {
            return new Node(place, 'z', new HashSet<>(), false, true);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, "connections");
        }

        @Override
        public boolean equals(Object other) {
            return EqualsBuilder.reflectionEquals(this, other, "connections");
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toStringExclude(this, "connections");
        }
    }
}
