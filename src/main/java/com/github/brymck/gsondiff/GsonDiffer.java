package com.github.brymck.gsondiff;

import com.google.gson.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class allows performing diffs on two objects of the same class using {@link Gson}.
 *
 * <p>For example, given two objects {@code before} and {@code after} of type {@code Example}, you
 * can diff them with the following code snippet:
 *
 * <pre>{@code
 * Gson gson = new Gson();
 * GsonDiffer gsonDiffer = new GsonDiffer();
 *
 * DiffResult diffResult = gsonDiffer.diff(
 *   gson,
 *   before,
 *   after
 * );
 * }</pre>
 *
 * @see <a href="https://github.com/google/gson">https://github.com/google/gson</a>
 * @author Bryan McKelvey
 */
public class GsonDiffer {
  /**
   * Recursively diff one object with another, producing a {@link DiffResult} containing a list of
   * differing keys and their values.
   *
   * @param gson a {@link Gson} instance
   * @param before the original object
   * @param after the modified object
   * @param <T> the class of the input and output objects
   * @return diffs between the two
   * @throws IllegalStateException when there is a type conflict
   */
  public <T> DiffResult diff(@NotNull Gson gson, @NotNull T before, @NotNull T after)
      throws IllegalStateException {
    DiffBuilder diffBuilder = new DiffBuilder(before, after, ToStringStyle.JSON_STYLE);
    JsonObject beforeElement = gson.toJsonTree(before).getAsJsonObject();
    JsonObject afterElement = gson.toJsonTree(after).getAsJsonObject();
    return diff(diffBuilder, beforeElement, afterElement, "");
  }

  /**
   * Recursively diff one object with another, producing a {@link DiffResult} containing a list of
   * differing keys and their values.
   *
   * @param diffBuilder a {@link DiffBuilder}
   * @param before the original object
   * @param after the modified object
   * @param prefix the key prefix
   * @return diffs between the two
   * @throws IllegalStateException when there is a type conflict
   */
  private @NotNull DiffResult diff(
      @NotNull DiffBuilder diffBuilder,
      @NotNull JsonObject before,
      @NotNull JsonObject after,
      @NotNull String prefix) {
    Set<String> seenKeys = new HashSet<>();
    for (Map.Entry<String, JsonElement> entry : after.entrySet()) {
      String afterKey = entry.getKey();
      JsonElement afterElement = entry.getValue();
      seenKeys.add(afterKey);
      String fullKey = prefix + afterKey;
      if (before.has(afterKey)) {
        // Handle conflicts with different logic for arrays, objects and primitives
        JsonElement beforeElement = before.get(afterKey);
        if (typesConflict(beforeElement, afterElement)) {
          String message = String.format("Type of %s and %s conflict", beforeElement, afterElement);
          throw new IllegalStateException(message);
        } else if (beforeElement.isJsonNull()) {
          createAddedDiffItems(diffBuilder, fullKey, afterElement);
        } else if (afterElement.isJsonNull()) {
          createRemovedDiffItems(diffBuilder, fullKey, beforeElement);
        } else if (afterElement.isJsonArray()) {
          // Extend arrays
          JsonArray beforeArray = beforeElement.getAsJsonArray();
          JsonArray afterArray = afterElement.getAsJsonArray();
          Set<JsonElement> beforeArraySet = new HashSet<>();
          Set<JsonElement> afterArraySet = new HashSet<>();
          beforeArray.forEach(beforeArraySet::add);
          afterArray.forEach(afterArraySet::add);
          int addedCount = 0;
          for (JsonElement element : afterArray) {
            if (!beforeArraySet.contains(element)) {
              addedCount++;
            }
          }
          int removedCount = 0;
          for (JsonElement element : beforeArray) {
            if (!afterArraySet.contains(element)) {
              removedCount++;
            }
          }
          if (addedCount != 0 || removedCount != 0) {
            diffBuilder.append(fullKey, -removedCount, addedCount);
          }
        } else if (afterElement.isJsonObject()) {
          // Update objects, preferring the update value
          diff(
              diffBuilder,
              beforeElement.getAsJsonObject(),
              afterElement.getAsJsonObject(),
              fullKey + ".");
        } else {
          createPrimitiveDiffItem(diffBuilder, fullKey, beforeElement, afterElement);
        }
      } else {
        createAddedDiffItems(diffBuilder, fullKey, afterElement);
      }
    }
    for (Map.Entry<String, JsonElement> entry : before.entrySet()) {
      String beforeKey = entry.getKey();
      JsonElement beforeValue = entry.getValue();
      String fullKey = prefix + beforeKey;
      if (!seenKeys.contains(beforeKey)) {
        createRemovedDiffItems(diffBuilder, fullKey, beforeValue);
      }
    }
    return diffBuilder.build();
  }

  /**
   * Compare two {@link JsonElement}s to see if their types conflict
   *
   * @param element1 a {@link JsonElement}
   * @param element2 a {@link JsonElement}
   * @return {@code true} if the types conflict, {@code false otherwise}
   */
  private boolean typesConflict(@NotNull JsonElement element1, @NotNull JsonElement element2) {
    if (element1.isJsonNull()) {
      return false;
    } else if (element2.isJsonNull()) {
      return false;
    } else if (element1.isJsonObject()) {
      return !element2.isJsonObject();
    } else if (element1.isJsonArray()) {
      return !element2.isJsonArray();
    } else {
      // We can assume the first element is a primitive
      if (element2.isJsonPrimitive()) {
        JsonPrimitive primitive1 = element1.getAsJsonPrimitive();
        JsonPrimitive primitive2 = element2.getAsJsonPrimitive();
        if (primitive1.isBoolean()) {
          return !primitive2.isBoolean();
        } else if (primitive1.isNumber()) {
          return !primitive2.isNumber();
        } else {
          // We can assume the first element is a string
          return !primitive2.isString();
        }
      } else {
        return true;
      }
    }
  }

  private @Nullable JsonPrimitive getPrimitiveOrNull(@Nullable JsonElement element) {
    if (element == null || element.isJsonNull()) {
      return null;
    } else {
      return element.getAsJsonPrimitive();
    }
  }

  private void createPrimitiveDiffItem(
      @NotNull DiffBuilder diffBuilder,
      @NotNull String key,
      @Nullable JsonElement beforeElement,
      @Nullable JsonElement afterElement) {
    JsonPrimitive beforePrimitive = getPrimitiveOrNull(beforeElement);
    JsonPrimitive afterPrimitive = getPrimitiveOrNull(afterElement);
    JsonPrimitive checkedPrimitive = (beforePrimitive == null) ? afterPrimitive : beforePrimitive;
    if (checkedPrimitive == null) {
      return;
    }
    if (checkedPrimitive.isBoolean()) {
      Boolean beforeValue = (beforePrimitive == null) ? null : beforePrimitive.getAsBoolean();
      Boolean afterValue = (afterPrimitive == null) ? null : afterPrimitive.getAsBoolean();
      if (beforeValue != afterValue) {
        diffBuilder.append(key, beforeValue, afterValue);
      }
    } else if (checkedPrimitive.isNumber()) {
      Double beforeValue = (beforePrimitive == null) ? null : beforePrimitive.getAsDouble();
      Double afterValue = (afterPrimitive == null) ? null : afterPrimitive.getAsDouble();
      if (beforeValue == null || !beforeValue.equals(afterValue)) {
        diffBuilder.append(key, beforeValue, afterValue);
      }
    } else {
      String beforeValue = (beforePrimitive == null) ? null : beforePrimitive.getAsString();
      String afterValue = (afterPrimitive == null) ? null : afterPrimitive.getAsString();
      if (beforeValue == null || !beforeValue.equals(afterValue)) {
        diffBuilder.append(key, beforeValue, afterValue);
      }
    }
  }

  private void createAddedDiffItems(
      @NotNull DiffBuilder diffBuilder, @NotNull String key, @NotNull JsonElement element) {
    if (element.isJsonArray()) {
      int count = element.getAsJsonArray().size();
      diffBuilder.append(key, 0, count);
    } else if (element.isJsonObject()) {
      for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
        String subKey = entry.getKey();
        JsonElement subElement = entry.getValue();
        createAddedDiffItems(diffBuilder, key + "." + subKey, subElement);
      }
    } else {
      createPrimitiveDiffItem(diffBuilder, key, null, element);
    }
  }

  private void createRemovedDiffItems(
      @NotNull DiffBuilder diffBuilder, @NotNull String key, @NotNull JsonElement element) {
    if (element.isJsonArray()) {
      int count = element.getAsJsonArray().size();
      diffBuilder.append(key, -count, 0);
    } else if (element.isJsonObject()) {
      for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
        String subKey = entry.getKey();
        JsonElement subElement = entry.getValue();
        createRemovedDiffItems(diffBuilder, key + "." + subKey, subElement);
      }
    } else {
      createPrimitiveDiffItem(diffBuilder, key, element, null);
    }
  }
}
