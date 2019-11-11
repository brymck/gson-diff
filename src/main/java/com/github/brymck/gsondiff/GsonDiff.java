package com.github.brymck.gsondiff;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a diff between the {@code left} (before) and {@code right} (after) view for
 * a property of the provided {@code key}.
 *
 * @param <T> the type of the diff to be stored
 */
public class GsonDiff<@NotNull T> {
  private String key;
  private T left;
  private T right;

  /**
   * Create an instance of {@link GsonDiff}.
   *
   * @param key the property's name
   * @param left the left-hand side (before) value
   * @param right the right-hand side (after) value
   */
  public GsonDiff(@NotNull String key, @Nullable T left, @Nullable T right) {
    this.key = key;
    this.left = left;
    this.right = right;
  }

  /**
   * Retrieve the property's name
   *
   * @return the property's name
   */
  public @NotNull String getKey() {
    return key;
  }

  /**
   * Retrieve the left-hand side (before) value
   *
   * @return the left-hand side (before) value
   */
  public @Nullable T getLeft() {
    return left;
  }

  /**
   * Retrieve the right-hand side (after) value
   *
   * @return the right-hand side (after) value
   */
  public @Nullable T getRight() {
    return right;
  }
}
