package dsa.ajay;

import java.util.Arrays;
import java.util.Random;

public class Benchmark {

    private static final int ARRAY_SIZE = 1_000_000;
    private static final int NUM_WARMUP_RUNS = 5;
    private static final int NUM_MEASUREMENT_RUNS = 10;



    public static double getNormalizedPerformanceScore() {
        // Warm-up phase
        for (int i = 0; i < NUM_WARMUP_RUNS; i++) {
            runBenchmark();
        }

        // Measurement phase
        long totalDuration = 0;
        for (int i = 0; i < NUM_MEASUREMENT_RUNS; i++) {
            totalDuration += runBenchmark();
        }
        double averageDuration = (double) totalDuration / NUM_MEASUREMENT_RUNS;

        // Normalize to a 0-1 scale
        return averageDuration / 100_000_000;
    }

    private static long runBenchmark() {
        int[] array = generateRandomArray(ARRAY_SIZE);

        long startTime = System.nanoTime();
        Arrays.sort(array); // Task to measure
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }
}