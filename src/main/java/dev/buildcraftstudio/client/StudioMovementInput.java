package dev.buildcraftstudio.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

/** Reads physical movement keys even while StudioGui owns keyboard events. */
public final class StudioMovementInput extends MovementInput {
    private final Minecraft minecraft;
    public StudioMovementInput(Minecraft minecraft) {this.minecraft=minecraft;}
    private boolean pressed(KeyBinding key) {
        int code=key.getKeyCode();
        return code>=0 && code<Keyboard.KEYBOARD_SIZE && Keyboard.isKeyDown(code);
    }
    @Override public void updatePlayerMoveState() {
        StudioSession s=StudioSession.get();
        if(s==null || !s.walkMode || !(minecraft.currentScreen instanceof StudioGui)) {
            moveStrafe=0;moveForward=0;jump=false;sneak=false;return;
        }
        moveStrafe=(pressed(minecraft.gameSettings.keyBindLeft)?1.0F:0.0F)
                  -(pressed(minecraft.gameSettings.keyBindRight)?1.0F:0.0F);
        moveForward=(pressed(minecraft.gameSettings.keyBindForward)?1.0F:0.0F)
                   -(pressed(minecraft.gameSettings.keyBindBack)?1.0F:0.0F);
        jump=pressed(minecraft.gameSettings.keyBindJump);
        sneak=pressed(minecraft.gameSettings.keyBindSneak);
        if(sneak){moveStrafe*=.3F;moveForward*=.3F;}
    }
}
