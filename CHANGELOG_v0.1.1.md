# BuildCraft Studio v0.1.1 — Precision, UI & terrain grid

## Changes
- Mouse hit ray now uses the actual 3D projection/modelview captured before GUI rendering, rather than an approximate FOV. Picks the closest visible virtual face, loaded terrain face or active plane; ray traversal stops before unloaded chunks.
- The Creative editor uses the real player's 9-slot hotbar, with a compact 22-pixel slot layout. Press E (or click ITENS E) for the vanilla Creative inventory, including search and every installed item's tab. Close inventory to return to building.
- Only the initial **safe vanilla block list** can be committed through this M1 editor. Non-block items or blocks outside the allowed list cannot be placed; seeing an item in Creative inventory does not imply placement support yet.
- A white transparent grid is drawn on loaded terrain's top surfaces (G toggles); it never edits actual world blocks. Different elevations get independently sampled, without generating new chunks.
- Autosave errors now report their immediate cause and path to the game log instead of giving only a generic disk message.
- Existing M1 limits are unchanged: max 512 preview pieces and 128 pieces per Creative commit.

## Verification
- Existing 48 standalone logic tests passed locally.
- Real ForgeGradle compilation on GitHub Actions and in-game UI/ray/grid checks remain mandatory; until they pass, **this update is experimental**.

## Test plan
1. Empty Creative hotbar -> E -> search for stone -> move to hotbar -> close -> select -> ghost should track pointer as the camera rotates and zooms.
2. Repeat with ordinary block terrain, hills and stacked virtual blocks, varying window aspect and GUI scale.
3. Check G toggle and the grid visually follows terrain and doesn't leak into unavailable chunks.
4. Build a few blocks, wait for autosave or press Ctrl+S, re-open editor; if error, collect `latest.log` and notice.
5. Verify no placing while hovering UI, and Creative confirmation still requires two clicks.
