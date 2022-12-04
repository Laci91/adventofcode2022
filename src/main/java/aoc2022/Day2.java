package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Day2 implements AocDay {
    @Override
    public long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return input.stream().map(line -> line.split(" ")).map(this::calculateScore).reduce(0L, Long::sum);
    }

    @Override
    public long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return input.stream().map(line -> line.split(" ")).map(this::calculateScoreFromGoal).reduce(0L, Long::sum);
    }

    private long calculateScore(String[] game) {
        RPSEnum ourMove = RPSEnum.getFromOurMove(game[1]);
        RPSEnum theirMove = RPSEnum.getFromTheirMove(game[0]);
        return RPSEnum.getScore(ourMove, theirMove);
    }

    private long calculateScoreFromGoal(String[] game) {
        RPSEnum theirMove = RPSEnum.getFromTheirMove(game[0]);
        return RPSEnum.getScoreFromResult(theirMove, game[1]);
    }

    private enum RPSEnum {
        Rock(1), Paper(2), Scissors(3);

        private final int score;

        RPSEnum(int score) {
            this.score = score;
        }

        private static Map<RPSEnum, RPSEnum> winsMap() {
            return Map.of(Rock, Scissors, Scissors, Paper, Paper, Rock);
        }

        private static Map<RPSEnum, RPSEnum> losesMap() {
            return Map.of(Rock, Paper, Paper, Scissors, Scissors, Rock);
        }

        public static int getScoreFromResult(RPSEnum theirMove, String ourGoal) {
            return switch (ourGoal) {
                case "X" -> winsMap().get(theirMove).score;
                case "Y" -> theirMove.score + 3;
                case "Z" -> losesMap().get(theirMove).score + 6;
                default -> throw new IllegalStateException("Unexpected value: " + ourGoal);
            };
        }

        public static RPSEnum getFromTheirMove(String theirMove) {
            return switch(theirMove) {
                case "A" -> Rock;
                case "B" -> Paper;
                case "C" -> Scissors;
                default -> throw new IllegalStateException("Unexpected value: " + theirMove);
            };
        }

        public static RPSEnum getFromOurMove(String ourMove) {
            return switch (ourMove) {
                case "X" -> Rock;
                case "Y" -> Paper;
                case "Z" -> Scissors;
                default -> throw new IllegalArgumentException(ourMove + " is not a valid move");
            };
        }

        public static int getScore(RPSEnum ourMove, RPSEnum theirMove) {
            if (ourMove == theirMove) {
                return 3 + ourMove.score;
            }

            if (winsMap().get(ourMove) == theirMove) {
                return 6 + ourMove.score;
            }

            return ourMove.score;
        }
    }
}
