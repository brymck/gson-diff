package com.github.brymck.gsondiff;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GsonDiff<@NotNull T> {
  private String key;
  private T left;
  private T right;

  public GsonDiff(@NotNull String key, @Nullable T left, @Nullable T right) {
    this.key = key;
    this.left = left;
    this.right = right;
  }

  public @NotNull String getKey() {
    return key;
  }

  public @Nullable T getLeft() {
    return left;
  }

  public @Nullable T getRight() {
    return right;
  }
}
