package dev.buildcraftstudio.client;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Purely local render-view entity. Never spawned or added to the world. */
public final class StudioCameraEntity extends Entity {
    public StudioCameraEntity(World world) {
        super(world);
        setSize(0.0F,0.0F);
        noClip = true;
        ignoreFrustumCheck = true;
    }
    @Override protected void entityInit() { }
    @Override protected void readEntityFromNBT(NBTTagCompound tag) { }
    @Override protected void writeEntityToNBT(NBTTagCompound tag) { }
}
