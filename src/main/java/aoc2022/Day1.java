package aoc2022;

import common.AocDay;
import common.FileReader;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class Day1 implements AocDay {

    @Override
    public long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return elfPayload(input).get(0).longValue();
    }

    @Override
    public long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return elfPayload(input).subList(0, 3).stream().reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
    }

    private List<BigDecimal> elfPayload(List<String> input) {
        List<BigDecimal> payloads = new ArrayList<>();
        BigDecimal current = BigDecimal.ZERO;
        for (String next: input) {
            if ("".equals(next)) {
                payloads.add(current);
                current = BigDecimal.ZERO;
                continue;
            }

            current = current.add(new BigDecimal(next));
        }
        payloads.add(current);
        payloads.sort(Comparator.reverseOrder());
        System.out.println(payloads);
        return payloads;
    }
}
