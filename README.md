gson-diff
===============

[![CircleCI](https://circleci.com/gh/brymck/gson-diff.svg?style=svg)](https://circleci.com/gh/brymck/gson-diff)
[![codecov](https://codecov.io/gh/brymck/gson-diff/branch/master/graph/badge.svg)](https://codecov.io/gh/brymck/gson-diff)

`gson-diff` is a library that allows diffing of objects with Gson.

Usage
-----

Include this in your POM:

```xml
<dependency>
  <groupId>com.github.brymck</groupId>
  <artifactId>gson-diff</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

And use it as so, given two instances `before` and `after` of class `Example`:

```java
Gson gson = new Gson();
GsonDiffer gsonDiffer = new GsonDiffer();

DiffResult diffResult = gsonDiffer.diff(
  gson,
  before,
  after
);
```
