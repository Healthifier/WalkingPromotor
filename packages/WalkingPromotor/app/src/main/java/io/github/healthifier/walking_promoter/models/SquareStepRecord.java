package io.github.healthifier.walking_promoter.models;

/**
 * Created by exKAZUu on 2017/10/04.
 */
public final class SquareStepRecord {
    public final int id;
    public final boolean exampleChecked;
    public final int correctPercentage;
    public final int solvedMilliseconds;

    public SquareStepRecord(int id) {
        this(id, false, 0, 0);
    }

    public SquareStepRecord(int id, int exampleChecked, int correctPercentage, int solvedMilliseconds) {
        this(id, exampleChecked != 0, correctPercentage, solvedMilliseconds);
    }

    public SquareStepRecord(int id, boolean exampleChecked, int correctPercentage, int solvedMilliseconds) {
        this.id = id;
        this.exampleChecked = exampleChecked;
        this.correctPercentage = correctPercentage;
        this.solvedMilliseconds = solvedMilliseconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SquareStepRecord that = (SquareStepRecord) o;

        if (id != that.id) return false;
        if (exampleChecked != that.exampleChecked) return false;
        if (correctPercentage != that.correctPercentage) return false;
        return solvedMilliseconds == that.solvedMilliseconds;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (exampleChecked ? 1 : 0);
        result = 31 * result + correctPercentage;
        result = 31 * result + solvedMilliseconds;
        return result;
    }

    @Override
    public String toString() {
        return String.format("id=%s, exampleChecked=%s, correctPercentage=%d solvedMilliseconds=%d", id, exampleChecked, correctPercentage, solvedMilliseconds);
    }
}
