package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Day8 implements AocDay<Long> {
    @Override
    public Long exercise1(String fileName) {
        String[][] input = FileReader.readAllLines(fileName, line -> line.split("")).toArray(new String[][] {});
        Node[][] nodes = new Node[input.length][input[0].length];
        for (int i=0;i<nodes.length;i++) {
            for (int j=0;j<nodes[0].length;j++) {
                nodes[i][j] = new Node(Integer.parseInt(input[i][j]));
                if (i > 0) {
                    nodes[i][j].coverNorth.set(Integer.max(nodes[i-1][j].height, nodes[i-1][j].coverNorth.get()));
                }

                if (j > 0) {
                    nodes[i][j].coverWest.set(Integer.max(nodes[i][j-1].height, nodes[i][j-1].coverWest.get()));
                }
            }
        }

        for (int i=nodes.length-2;i>=0;i--) {
            for (int j=nodes[0].length-2;j>=0;j--) {
                nodes[i][j].coverSouth.set(Integer.max(nodes[i+1][j].height, nodes[i+1][j].coverSouth.get()));
                nodes[i][j].coverEast.set(Integer.max(nodes[i][j+1].height, nodes[i][j+1].coverEast.get()));
            }
        }

        return Arrays.stream(nodes).flatMap(Arrays::stream).filter(Node::isVisible).count();
    }

    @Override
    public Long exercise2(String fileName) {
        String[][] input = FileReader.readAllLines(fileName, line -> line.split("")).toArray(new String[][] {});
        Node[][] nodes = new Node[input.length][input[0].length];
        for (int i=0;i<nodes.length;i++) {
            for (int j=0;j<nodes[0].length;j++) {
                nodes[i][j] = new Node(Integer.parseInt(input[i][j]));
            }
        }

        for (int i=1;i<nodes.length-1;i++) {
            for (int j=1;j<nodes[0].length-1;j++) {
                int k=1; int l=1; int m=1; int n=1;
                for (;k+1<=i && nodes[i-k][j].height < nodes[i][j].height;k++) {}
                for (;i+l+1<nodes.length && nodes[i+l][j].height < nodes[i][j].height;l++) {}
                for (;m+1<=j && nodes[i][j-m].height < nodes[i][j].height;m++) {}
                for (;j+n+1<nodes[0].length && nodes[i][j+n].height < nodes[i][j].height;n++) {}
                nodes[i][j].scenicScore.set(k*l*m*n);
                System.out.println("(" + i + "," + j + ") -> " + k*l*m*n);
            }
        }
        return Long.valueOf(Arrays.stream(nodes).flatMap(Arrays::stream).map(nd -> nd.scenicScore.get()).max(Comparator.naturalOrder()).orElseThrow());
    }

    private record Node(Integer height, AtomicInteger scenicScore, AtomicInteger coverNorth, AtomicInteger coverEast, AtomicInteger coverSouth,
                              AtomicInteger coverWest) {
        Node(Integer height) {
            this(height, new AtomicInteger(1), new AtomicInteger(-1), new AtomicInteger(-1), new AtomicInteger(-1),
                new AtomicInteger(-1));
        }

        boolean isVisible() {
            return height > coverNorth.get() || height > coverEast().get() || height > coverSouth().get() ||
                height > coverWest.get();
        }
    }
}
