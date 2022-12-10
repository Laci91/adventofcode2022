package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.List;
import java.util.stream.LongStream;

public class Day10 implements AocDay<Long> {
    @Override
    public Long exercise1(String fileName) {
        List<Instruction> input = FileReader.readAllLines(fileName, Instruction::fromLine);
        MachineState state = new MachineState();
        long signalStrength = 0;
        Instruction next = null;
        while(!input.isEmpty()) {
            if (next == null || next.length == 0) {
                next = input.remove(0);
            }
            next = next.executeOneCycle();
            state = state.applyInstruction(next);
            signalStrength += state.getSignalStrengthIncrement();
        }
        return signalStrength;
    }

    @Override
    public Long exercise2(String fileName) {
        List<Instruction> input = FileReader.readAllLines(fileName, Instruction::fromLine);
        String[][] crt = new String[6][40];
        MachineState state = new MachineState();
        Instruction next = null;
        while(!input.isEmpty() && state.cycle < 240) {
            System.out.println(state.cycle + " -> " + state.register);
            long currentCrtPosition = (state.cycle-1)%40;
            crt[(state.cycle-1)/40][(state.cycle-1)%40] = LongStream.range(state.register-1, state.register+2).filter(i -> i == currentCrtPosition).count() == 0 ? "." : "#";

            if (next == null || next.length == 0) {
                next = input.remove(0);
            }
            next = next.executeOneCycle();
            state = state.applyInstruction(next);
        }

        for(int i=0;i<crt.length;i++) {
            for (int j=0;j<crt[i].length;j++) {
                System.out.print(crt[i][j]);
            }
            System.out.println();
        }

        return 0L;
    }

    private record MachineState(int cycle, long register) {
        MachineState() {
            this(1, 1L);
        }

        MachineState applyInstruction(Instruction instruction) {
            if (instruction.length != 0) {
                return new MachineState(cycle + 1, register);
            }

            return new MachineState(cycle + 1, register + instruction.change);
        }

        private long getSignalStrengthIncrement() {
            if (List.of(20, 60, 100, 140, 180, 220).contains(cycle)) {
                return register * cycle;
            } else {
                return 0;
            }
        }
    }

    private record Instruction(int length, long change) {

        static Instruction fromLine(String line) {
            if ("noop".equals(line)) {
                return new Instruction(1, 0);
            }

            return new Instruction(2, Long.parseLong(line.substring(5)));
        }

        Instruction executeOneCycle() {
            return new Instruction(length - 1, change);
        }
    }
}
