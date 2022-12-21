package aoc2022;

import com.google.common.base.Splitter;
import common.AocDay;
import common.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day13 implements AocDay<Integer> {
    @Override
    public Integer exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        List<Pair> pairs = new ArrayList<>();
        for (int i=0;i<(input.size()+1)/3;i++) {
            pairs.add(new Pair(buildExpression(input.get(i * 3)), buildExpression(input.get(i * 3 + 1))));
        }
        return IntStream.range(1, pairs.size() + 1)
            .filter(i -> wellOrdered(pairs.get(i-1).left, pairs.get(i-1).right) != WellOrdered.FALSE)
            .peek(System.out::println)
            .sum();
    }

    @Override
    public Integer exercise2(String fileName) {
        List<List<?>> input = FileReader.readAllLines(fileName, this::buildExpression, str -> !"".equals(str));
        input.add(buildExpression("[[2]]"));
        input.add(buildExpression("[[6]]"));
        input.sort((a, b) -> wellOrdered(a, b).compareToNumber);

        input.forEach(System.out::println);

        return (input.indexOf(List.of(List.of(2))) + 1) * (input.indexOf(List.of(List.of(6))) + 1);
    }

    private List<?> buildExpression(String str) {
        System.out.println("Evaluating section: " + str);
        List<String> chars = Splitter.fixedLength(1).splitToList(str);
        Stack<List<Object>> listStack = new Stack<>();
        List<Object> current = null;
        StringBuilder numberChars = new StringBuilder();
        for (String ch: chars) {
            switch (ch) {
                case "[" -> {
                    if (current != null) {
                        listStack.push(current);
                    }
                    current = new ArrayList<>();
                }
                case "]" -> {
                    if (!numberChars.isEmpty()) {
                        current.add(Integer.parseInt(numberChars.toString()));
                        numberChars = new StringBuilder();
                    }
                    if (listStack.empty()) {
                        return current;
                    }
                    List<Object> next = listStack.pop();
                    next.add(current);
                    current = next;
                }
                case "," -> {
                    if (!numberChars.isEmpty()) {
                        current.add(Integer.parseInt(numberChars.toString()));
                    }
                    numberChars = new StringBuilder();
                }
                default -> numberChars.append(ch);
            }
        }

        return current;
    }

    WellOrdered wellOrdered(List<?> left, List<?> right) {
        System.out.println("Checking if " + left + " and " + right + " are well ordered");
        for(int i=0;i<left.size();i++) {
            if (right.size() <= i) {
                return WellOrdered.FALSE;
            }

            Object nextLeftItem = left.get(i);
            Object nextRightItem = right.get(i);

            if (nextLeftItem instanceof Integer l) {
                if (nextRightItem instanceof Integer r) {
                    if (l.equals(r)) {
                        continue;
                    }

                    return WellOrdered.fromBoolean(l < r);
                } else if (nextRightItem instanceof List<?> r) {
                    WellOrdered wo = wellOrdered(List.of(l), r);
                    if (wo == WellOrdered.DONT_KNOW) {
                        continue;
                    }
                    return wo;
                }
            } else if (nextLeftItem instanceof List<?> l) {
                List<?> r = switch (nextRightItem) {
                    case Integer ri -> List.of(ri);
                    case List<?> rl -> rl;
                    default -> throw new IllegalArgumentException("Argument is " + nextRightItem.getClass());
                };

                WellOrdered wo = wellOrdered(l, r);
                if (wo == WellOrdered.DONT_KNOW) {
                    continue;
                }
                return wo;
            }
        }

        return right.size() > left.size() ? WellOrdered.TRUE : WellOrdered.DONT_KNOW;
    }

    private enum WellOrdered {
        TRUE(-1), FALSE(1), DONT_KNOW(0);

        final int compareToNumber;

        WellOrdered(int compareToNumber) {
            this.compareToNumber = compareToNumber;
        }

        static WellOrdered fromBoolean(boolean b) {
            return b ? TRUE : FALSE;
        }
    }

    private record Pair(List<?> left, List<?> right) {

    }

}
