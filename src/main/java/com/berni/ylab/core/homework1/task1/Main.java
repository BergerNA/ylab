package com.berni.ylab.core.homework1.task1;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.IntConsumer;

import static java.lang.System.out;

public class Main {

    private static final int RANDOM_NUMBER_BOUNDARY = 100;
    private static final int MATRIX_SIZE = 5;

    public static void main(String[] args) {
        Report<SummaryStatistics> matrixSummaryStatisticsTemplate =
                statistic -> String.format("%nMatrix statistics:%nmaximum = %d%nminimum = %d%naverage = %.2f%n",
                        statistic.max, statistic.min, statistic.avg);

        int[][] matrix = MatrixUtils.generate(MATRIX_SIZE);
        MatrixUtils.print(matrix);
        matrixStatistic(matrix, matrixSummaryStatisticsTemplate).ifPresent(out::println);
    }

    private static Optional<SummaryStatistics> matrixStatistic(final int[][] matrix, final Report<SummaryStatistics> reportTemplate) {
        if (MatrixUtils.isEmpty(matrix)) {
            return Optional.empty();
        }
        final var summaryStatistics = new SummaryStatistics(reportTemplate);
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                summaryStatistics.accept(anInt);
            }
        }
        return Optional.of(summaryStatistics);
    }

    private interface Report<T> {
        String generate(T reportData);
    }

    private static class SummaryStatistics implements IntConsumer {
        private int sum = 0;
        private int count = 0;
        private double avg = 0;
        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private final Report<SummaryStatistics> reportTemplate;

        private SummaryStatistics(final Report<SummaryStatistics> reportTemplate) {
            this.reportTemplate = reportTemplate;
        }

        @Override
        public void accept(int value) {
            count++;
            sum += value;
            avg = (double) sum / count;
            min = Math.min(value, min);
            max = value > max ? value : max; // for example
        }

        @Override
        public String toString() {
            return reportTemplate.generate(this);
        }
    }

    private static class MatrixUtils {

        private static int[][] generate(int size) {
            int[][] matrix = new int[size][size];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] = Random.nextInt(RANDOM_NUMBER_BOUNDARY);
                }
            }
            return matrix;
        }

        private static boolean isEmpty(final int[][] matrix) {
            if (matrix != null && matrix.length != 0) {
                for (int[] ints : matrix) {
                    if (ints.length != 0) {
                        return false;
                    }
                }
            }
            return true;
        }

        private static void print(final int[][] matrix) {
            int digits = String.valueOf(RANDOM_NUMBER_BOUNDARY).length() + 1;
            String template = MessageFormat.format("%-{0}d", digits);
            for (int[] ints : matrix) {
                for (int anInt : ints) {
                    out.printf(template, anInt);
                }
                out.println();
            }
        }
    }

    private static class Random {

        private static long seed = 8682522807148012L ^ System.nanoTime();

        /**
         * @param boundary upper number of randomize
         * @return random int number from 0 to boundary (exclude boundary)
         */
        private static int nextInt(int boundary) {
            seed = 8253729 * seed + 2396403;
            seed = seed >>> 1;
            return (int) (seed % boundary);
        }
    }
}
