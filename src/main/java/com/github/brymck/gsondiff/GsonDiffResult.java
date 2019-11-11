package com.github.brymck.gsondiff;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GsonDiffResult {
  private Map<String, GsonDiff<String>> strings;
  private Map<String, GsonDiff<Double>> doubles;
  private Map<String, GsonDiff<Integer>> integers;
  private Map<String, GsonDiff<Boolean>> booleans;

  public GsonDiffResult(
      Map<String, GsonDiff<String>> strings,
      Map<String, GsonDiff<Double>> doubles,
      Map<String, GsonDiff<Integer>> integers,
      Map<String, GsonDiff<Boolean>> booleans) {
    this.strings = strings;
    this.doubles = doubles;
    this.integers = integers;
    this.booleans = booleans;
  }

  GsonDiffResult(Builder builder) {
    this(builder.strings, builder.doubles, builder.integers, builder.booleans);
  }

  public @NotNull GsonDiff<@NotNull String> getStringDiff(@NotNull String key) {
    return strings.get(key);
  }

  public @NotNull GsonDiff<@NotNull Double> getDoubleDiff(@NotNull String key) {
    return doubles.get(key);
  }

  public @NotNull GsonDiff<@NotNull Integer> getIntegerDiff(@NotNull String key) {
    return integers.get(key);
  }

  public @NotNull GsonDiff<@NotNull Boolean> getBooleanDiff(@NotNull String key) {
    return booleans.get(key);
  }

  public int size() {
    return strings.size() + doubles.size() + integers.size() + booleans.size();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    Map<String, GsonDiff<String>> strings = new HashMap<>();
    Map<String, GsonDiff<Double>> doubles = new HashMap<>();
    Map<String, GsonDiff<Integer>> integers = new HashMap<>();
    Map<String, GsonDiff<Boolean>> booleans = new HashMap<>();

    public Builder put(@NotNull String key, @Nullable String left, @Nullable String right) {
      GsonDiff<String> value = new GsonDiff<>(key, left, right);
      strings.put(key, value);
      return this;
    }

    public Builder put(@NotNull String key, @Nullable Double left, @Nullable Double right) {
      GsonDiff<Double> value = new GsonDiff<>(key, left, right);
      doubles.put(key, value);
      return this;
    }

    public Builder put(@NotNull String key, @Nullable Integer left, @Nullable Integer right) {
      GsonDiff<Integer> value = new GsonDiff<>(key, left, right);
      integers.put(key, value);
      return this;
    }

    public Builder put(@NotNull String key, @Nullable Boolean left, @Nullable Boolean right) {
      GsonDiff<Boolean> value = new GsonDiff<>(key, left, right);
      booleans.put(key, value);
      return this;
    }

    public @NotNull GsonDiffResult build() {
      return new GsonDiffResult(this);
    }
  }
}
