# BuildCraft Studio v0.1.3 — Movement and performance repair

- Fix for Minecraft 1.12.2 client movement/network requiring Steve as current render view entity during the world tick; detached camera restored for rendering.
- Raw movement input through `StudioMovementInput` for non-pausing GuiScreen.
- Hide first-person item/hand while editing with RenderHandEvent.
- Cache OpenGL projection reads for stationary camera, and cache cursor hit when cursor and editor state did not change.
- Projected grid reduced from 19×19 to 13×13.
- Creative camera focus limit corrected to 64 blocks instead of 48.
- Preserve v0.1.2 in PR #3; this is an experimental isolated branch.
- Actual FPS and movement must be validated in Minecraft, independent of successful Forge compilation.
