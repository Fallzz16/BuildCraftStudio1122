#!/usr/bin/env bash
set -euo pipefail
OUT="${TMPDIR:-/tmp}/buildcraft-core-tests"
rm -rf "$OUT"
mkdir -p "$OUT"
javac -encoding UTF-8 -source 8 -target 8 -d "$OUT" \
    src/main/java/dev/buildcraftstudio/core/EditorMath.java tests/EditorMathTest.java \
    tests/stubs/net/minecraft/util/math/BlockPos.java \
    tests/stubs/net/minecraft/block/state/IBlockState.java \
    src/main/java/dev/buildcraftstudio/core/BuildDocument.java tests/BuildDocumentTest.java
java -cp "$OUT" EditorMathTest
java -cp "$OUT" BuildDocumentTest
