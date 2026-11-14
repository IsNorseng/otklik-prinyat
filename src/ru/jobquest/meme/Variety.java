package ru.jobquest.meme;

import java.util.Random;

final class Variety {
    private static final int VACANCIES_PER_RUN = 12;
    private static final int RECRUITERS_PER_RUN = 3;

    private final int vacancyCount;
    private final int recruiterCount;
    private final Random random = new Random();
    private int[] vacancies = new int[0];
    private int[] recruiters = new int[0];

    Variety(int vacancyCount, int recruiterCount) {
        if (vacancyCount < VACANCIES_PER_RUN || recruiterCount < RECRUITERS_PER_RUN) {
            throw new IllegalArgumentException("Banks must contain enough items for a complete run");
        }
        this.vacancyCount = vacancyCount;
        this.recruiterCount = recruiterCount;
        next();
    }

    int[] vacancies() { return vacancies.clone(); }
    int[] recruiters() { return recruiters.clone(); }

    void next() {
        vacancies = sample(vacancyCount, VACANCIES_PER_RUN, vacancies);
        recruiters = sample(recruiterCount, RECRUITERS_PER_RUN, recruiters);
    }

    private int[] sample(int bankSize, int runSize, int[] previous) {
        boolean[] usedLastRun = new boolean[bankSize];
        for (int index : previous) usedLastRun[index] = true;

        int[] pool = new int[bankSize];
        int freshCount = 0;
        for (int index = 0; index < bankSize; index++) {
            if (!usedLastRun[index]) pool[freshCount++] = index;
        }
        System.arraycopy(previous, 0, pool, freshCount, previous.length);

        int[] result = new int[runSize];
        for (int i = 0; i < runSize; i++) {
            int limit = i < freshCount ? freshCount : bankSize;
            int selected = i + random.nextInt(limit - i);
            result[i] = pool[selected];
            pool[selected] = pool[i];
        }
        // Keep every fresh pick, but distribute any unavoidable repeats throughout the run.
        for (int i = result.length - 1; i > 0; i--) {
            int selected = random.nextInt(i + 1);
            int swap = result[selected];
            result[selected] = result[i];
            result[i] = swap;
        }
        return result;
    }
}
