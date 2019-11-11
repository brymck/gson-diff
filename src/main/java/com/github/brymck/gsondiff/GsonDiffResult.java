package com.github.brymck.gsondiff;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a collection of diffs of various types between two objects, keyed on
 * property name
 */
public class GsonDiffResult {
  private Map<String, GsonDiff<String>> strings;
  private Map<String, GsonDiff<Double>> doubles;
  private Map<String, GsonDiff<Integer>> integers;
  private Map<String, GsonDiff<Boolean>> booleans;

  /**
   * Create an instance of {@link GsonDiffResult}
   *
   * @param strings a list of string diffs
   * @param doubles a list of double diffs
   * @param integers a list of integer diffs
   * @param booleans a list of boolean diffs
   */
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

  /**
   * Create an instance of {@link GsonDiffResult}
   *
   * @param builder a finalized {@link Builder}
   */
  GsonDiffResult(Builder builder) {
    this(builder.strings, builder.doubles, builder.integers, builder.booleans);
  }

  /**
   * Retrieve a string-based diff
   *
   * @param key the property's name
   * @return a diff containing the property's name and the string values on the left- and right-hand side
   */
  public @NotNull GsonDiff<@NotNull String> getStringDiff(@NotNull String key) {
    return strings.get(key);
  }

  /**
   * Retrieve a double-based diff
   *
   * @param key the property's name
   * @return a diff containing the property's name and the double values on the left- and right-hand side
   */
  public @NotNull GsonDiff<@NotNull Double> getDoubleDiff(@NotNull String key) {
    return doubles.get(key);
  }

  /**
   * Retrieve an integer-based diff
   *
   * @param key the property's name
   * @return a diff containing the property's name and the integer values on the left- and right-hand side
   */
  public @NotNull GsonDiff<@NotNull Integer> getIntegerDiff(@NotNull String key) {
    return integers.get(key);
  }

  /**
   * Retrieve a boolean-based diff
   *
   * @param key the property's name
   * @return a diff containing the property's name and the boolean values on the left- and right-hand side
   */
  public @NotNull GsonDiff<@NotNull Boolean> getBooleanDiff(@NotNull String key) {
    return booleans.get(key);
  }

  /**
   * Retrieve the number of diffs
   *
   * @return the number of diffs
   */
  public int size() {
    return strings.size() + doubles.size() + integers.size() + booleans.size();
  }

  /**
   * Create a builder class to incrementally create a {@link GsonDiffResult}
   *
   * @return a {@link Builder}
   */
  public static @NotNull Builder builder() {
    return new Builder();
  }

  /**
   * This class allows you to incrementally create a {@link GsonDiffResult}
   */
  public static class Builder {
    Map<String, GsonDiff<String>> strings = new HashMap<>();
    Map<String, GsonDiff<Double>> doubles = new HashMap<>();
    Map<String, GsonDiff<Integer>> integers = new HashMap<>();
    Map<String, GsonDiff<Boolean>> booleans = new HashMap<>();

    /**
     * Add a string-based diff.
     *
     * @param key the property's name
     * @param left the left-hand side (before) value
     * @param right the right-hand side (after) value
     * @return the updated {@code Builder}
     */
    public Builder put(@NotNull String key, @Nullable String left, @Nullable String right) {
      GsonDiff<String> value = new GsonDiff<>(key, left, right);
      strings.put(key, value);
      return this;
    }

    /**
     * Add a double-based diff.
     *
     * @param key the property's name
     * @param left the left-hand side (before) value
     * @param right the right-hand side (after) value
     * @return the updated {@code Builder}
     */
    public Builder put(@NotNull String key, @Nullable Double left, @Nullable Double right) {
      GsonDiff<Double> value = new GsonDiff<>(key, left, right);
      doubles.put(key, value);
      return this;
    }

    /**
     * Add an integer-based diff.
     *
     * @param key the property's name
     * @param left the left-hand side (before) value
     * @param right the right-hand side (after) value
     * @return the updated {@code Builder}
     */
    public Builder put(@NotNull String key, @Nullable Integer left, @Nullable Integer right) {
      GsonDiff<Integer> value = new GsonDiff<>(key, left, right);
      integers.put(key, value);
      return this;
    }

    /**
     * Add a boolean-based diff.
     *
     * @param key the property's name
     * @param left the left-hand side (before) value
     * @param right the right-hand side (after) value
     * @return the updated {@code Builder}
     */
    public Builder put(@NotNull String key, @Nullable Boolean left, @Nullable Boolean right) {
      GsonDiff<Boolean> value = new GsonDiff<>(key, left, right);
      booleans.put(key, value);
      return this;
    }

    /**
     * Build a {@link GsonDiffResult}
     *
     * @return a {@link GsonDiffResult}
     */
    public @NotNull GsonDiffResult build() {
      return new GsonDiffResult(this);
    }
  }
}
