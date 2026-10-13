# BuildCraft Studio M1.3 — QA

Local tests: 22 EditorMath + 26 BuildDocument assertions and compressed autosave roundtrip passed.

Forge CI tests: pending first GitHub workflow run.

In-game tests required:
1. Tab between Steve and Camera. Verify WASD moves player (and sends position to integrated server) and independently pans camera.
2. RMB drag to orbit, MMB drag to pan, zoom and preview remain intact.
3. Check first-person held-item shake disappears and hand hidden only during editing.
4. Benchmark FPS with G (grid) on/off, with 0, 128 and 500 virtual pieces in jungle and flat creative world.
5. Save, reopen and finalize 10–30 blocks; verify no lost project or accidental world edits.

Rendering and frame rate are NOT considered validated solely by passing compilation.
