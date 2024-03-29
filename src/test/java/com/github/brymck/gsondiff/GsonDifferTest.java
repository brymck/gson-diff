package com.github.brymck.gsondiff;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GsonDifferTest {
  // By default, Gson doesn't serialize nulls, which is fine, but to ensure we tested code paths
  // with JSON nulls, we turn that on for testing
  private Gson gson = new GsonBuilder().serializeNulls().create();
  private GsonDiffer gsonDiffer = new GsonDiffer();

  @Test
  void createsDiffItemsWithCorrectTypesForChangesInStrings() {
    String beforeJson = "{ \"name\": \"Dane\" }";
    String afterJson = "{ \"name\": \"Bryan\" }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff firstDiff = diffs.getStringDiff("name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals("Bryan", firstDiff.getRight()),
        () -> assertEquals("Dane", firstDiff.getLeft()));
  }

  @Test
  void diffItemsHaveKeys() {
    String beforeJson = "{ \"name\": \"Dane\" }";
    String afterJson = "{ \"name\": \"Bryan\" }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff firstDiff = diffs.getStringDiff("name");
    assertEquals("name", firstDiff.getKey());
  }

  @Test
  void createsDiffItemsWithCorrectTypesForChangesInBooleans() {
    String beforeJson = "{ \"married\": false }";
    String afterJson = "{ \"married\": true }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff firstDiff = diffs.getBooleanDiff("married");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(true, firstDiff.getRight()),
        () -> assertEquals(false, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsWithCorrectTypesForChangesInNumbers() {
    String beforeJson = "{ \"age\": 34 }";
    String afterJson = "{ \"age\": 35 }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Double> firstDiff = diffs.getDoubleDiff("age");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(35.0, firstDiff.getRight()),
        () -> assertEquals(34.0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsWithCountForChangesInLists() {
    String beforeJson = "{ \"countries\": [\"JP\", \"US\", \"GB\", \"HK\"] }";
    String afterJson = "{ \"countries\": [\"JP\", \"US\", \"IN\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(1, firstDiff.getRight()),
        () -> assertEquals(-2, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsWithCountForAdditionsInLists() {
    String beforeJson = "{ \"countries\": [\"JP\", \"US\"] }";
    String afterJson = "{ \"countries\": [\"JP\", \"US\", \"IN\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(1, firstDiff.getRight()),
        () -> assertEquals(0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsWithCountForRemovalsInLists() {
    String beforeJson = "{ \"countries\": [\"JP\", \"US\", \"GB\", \"HK\"] }";
    String afterJson = "{ \"countries\": [\"JP\", \"US\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(0, firstDiff.getRight()),
        () -> assertEquals(-2, firstDiff.getLeft()));
  }

  @Test
  void createsNoDiffItemsTheSameString() {
    String beforeJson = "{ \"name\": \"Bryan\" }";
    String afterJson = "{ \"name\": \"Bryan\" }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    assertEquals(0, diffs.size());
  }

  @Test
  void createsNoDiffItemsForTheSameBoolean() {
    String beforeJson = "{ \"married\": true }";
    String afterJson = "{ \"married\": true }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    assertEquals(0, diffs.size());
  }

  @Test
  void createsNoDiffItemsForTheSameNumber() {
    String beforeJson = "{ \"age\": 35 }";
    String afterJson = "{ \"age\": 35 }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    assertEquals(0, diffs.size());
  }

  @Test
  void createsNoDiffItemsForListsWithTheSameItems() {
    String beforeJson = "{ \"countries\": [\"US\", \"JP\"] }";
    String afterJson = "{ \"countries\": [\"US\", \"JP\", \"US\", \"JP\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs1 = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiffResult diffs2 = gsonDiffer.diff(gson, afterJsonObject, beforeJsonObject);
    assertAll(() -> assertEquals(0, diffs1.size()), () -> assertEquals(0, diffs2.size()));
  }

  @ParameterizedTest
  @CsvSource({
    "{ \"name\": null },{ \"name\": null }",
    "{ \"name\": null },{}",
    "{}, { \"name\": null }",
    "{}, {}"
  })
  void createsNoDiffItemsForMissingValuesAndNulls(String beforeJson, String afterJson) {
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    assertEquals(0, diffs.size());
  }

  @Test
  void createsDiffItemsForStringsInPreviouslyMissingFields() {
    String beforeJson = "{}";
    String afterJson = "{ \"name\": \"Bryan\" }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals("Bryan", firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForBooleansInPreviouslyMissingFields() {
    String beforeJson = "{}";
    String afterJson = "{ \"married\": true }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Boolean> firstDiff = diffs.getBooleanDiff("married");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(true, firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNumbersInPreviouslyMissingFields() {
    String beforeJson = "{}";
    String afterJson = "{ \"age\": 35 }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Double> firstDiff = diffs.getDoubleDiff("age");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(35.0, firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForListsInPreviouslyMissingFields() {
    String beforeJson = "{}";
    String afterJson = "{ \"countries\": [\"US\", \"JP\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(2, firstDiff.getRight()),
        () -> assertEquals(0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForStringsInNowMissingFields() {
    String beforeJson = "{ \"name\": \"Bryan\" }";
    String afterJson = "{}";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals("Bryan", firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForBooleansInNowMissingFields() {
    String beforeJson = "{ \"married\": true }";
    String afterJson = "{}";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Boolean> firstDiff = diffs.getBooleanDiff("married");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals(true, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNumbersInNowMissingFields() {
    String beforeJson = "{ \"age\": 35 }";
    String afterJson = "{}";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Double> firstDiff = diffs.getDoubleDiff("age");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals(35.0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForStringsInPreviouslyNullFields() {
    String beforeJson = "{ \"name\": null }";
    String afterJson = "{ \"name\": \"Bryan\" }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals("Bryan", firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForListsInNowMissingFields() {
    String beforeJson = "{ \"countries\": [\"US\", \"JP\"] }";
    String afterJson = "{}";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(0, firstDiff.getRight()),
        () -> assertEquals(-2, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForListsInPreviouslyNullFields() {
    String beforeJson = "{ \"countries\": null }";
    String afterJson = "{ \"countries\": [\"US\", \"JP\"] }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(2, firstDiff.getRight()),
        () -> assertEquals(0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForBooleansInPreviouslyNullFields() {
    String beforeJson = "{ \"married\": null }";
    String afterJson = "{ \"married\": true }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Boolean> firstDiff = diffs.getBooleanDiff("married");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(true, firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNumbersInPreviouslyNullFields() {
    String beforeJson = "{ \"age\": null }";
    String afterJson = "{ \"age\": 35 }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Double> firstDiff = diffs.getDoubleDiff("age");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(35.0, firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForListsInNowNullFields() {
    String beforeJson = "{ \"countries\": [\"US\", \"JP\"] }";
    String afterJson = "{ \"countries\": null }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Integer> firstDiff = diffs.getIntegerDiff("countries");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals(0, firstDiff.getRight()),
        () -> assertEquals(-2, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForBooleansInNowNullFields() {
    String beforeJson = "{ \"married\": true }";
    String afterJson = "{ \"married\": null }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Boolean> firstDiff = diffs.getBooleanDiff("married");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals(true, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNumbersInNowNullFields() {
    String beforeJson = "{ \"age\": 35 }";
    String afterJson = "{ \"age\": null }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<Double> firstDiff = diffs.getDoubleDiff("age");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals(35.0, firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNestedChanges() {
    String beforeJson = "{ \"person\": { \"name\": \"Dane\" } }";
    String afterJson = "{ \"person\": { \"name\": \"Bryan\" } }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("person.name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals("Bryan", firstDiff.getRight()),
        () -> assertEquals("Dane", firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNestedChangesWithMissingParents() {
    String beforeJson = "{ }";
    String afterJson = "{ \"person\": { \"name\": \"Bryan\" } }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("person.name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertEquals("Bryan", firstDiff.getRight()),
        () -> assertNull(firstDiff.getLeft()));
  }

  @Test
  void createsDiffItemsForNestedChangesWithRemovedParents() {
    String beforeJson = "{ \"person\": { \"name\": \"Bryan\" } }";
    String afterJson = "{ }";
    JsonObject beforeJsonObject = gson.fromJson(beforeJson, JsonObject.class);
    JsonObject afterJsonObject = gson.fromJson(afterJson, JsonObject.class);
    GsonDiffResult diffs = gsonDiffer.diff(gson, beforeJsonObject, afterJsonObject);
    GsonDiff<String> firstDiff = diffs.getStringDiff("person.name");
    assertAll(
        () -> assertEquals(1, diffs.size()),
        () -> assertNull(firstDiff.getRight()),
        () -> assertEquals("Bryan", firstDiff.getLeft()));
  }

  @ParameterizedTest
  @CsvSource({
    "\"zero\",0",
    "\"zero\",false",
    "\"zero\",[]",
    "\"zero\",{}",
    "0,false",
    "0,[]",
    "0,{}",
    "[],{}"
  })
  void throwsAnExceptionWhenTypesConflict(String value1, String value2) {
    String json1 = String.format("{ \"value\": %s }", value1);
    String json2 = String.format("{ \"value\": %s }", value2);
    JsonObject jsonObject1 = gson.fromJson(json1, JsonObject.class);
    JsonObject jsonObject2 = gson.fromJson(json2, JsonObject.class);
    assertAll(
        () ->
            assertThrows(
                IllegalStateException.class, () -> gsonDiffer.diff(gson, jsonObject1, jsonObject2)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> gsonDiffer.diff(gson, jsonObject2, jsonObject1)));
  }

  @Test
  void diffsCanBeSerializedAndDeserialized() {
    GsonDiffResult builder =
        GsonDiffResult.builder()
            .put("string", "a", "b")
            .put("double", 0.0, 1.0)
            .put("integer", 0, 1)
            .put("boolean", false, true)
            .build();
    String json = gson.toJson(builder);
    GsonDiffResult roundTripBuilder = gson.fromJson(json, GsonDiffResult.class);
    assertAll(
        () -> assertEquals("a", roundTripBuilder.getStringDiff("string").getLeft()),
        () -> assertEquals("b", roundTripBuilder.getStringDiff("string").getRight()),
        () -> assertEquals(0.0, roundTripBuilder.getDoubleDiff("double").getLeft()),
        () -> assertEquals(1.0, roundTripBuilder.getDoubleDiff("double").getRight()),
        () -> assertEquals(0, roundTripBuilder.getIntegerDiff("integer").getLeft()),
        () -> assertEquals(1, roundTripBuilder.getIntegerDiff("integer").getRight()),
        () -> assertEquals(false, roundTripBuilder.getBooleanDiff("boolean").getLeft()),
        () -> assertEquals(true, roundTripBuilder.getBooleanDiff("boolean").getRight()));
  }
}
