package dev.buildcraftstudio.core;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/** Deliberately small, reversible v0.1 creative palette. Third-party blocks are not enabled in M1. */
public final class BuildPalette {
    public static final List<Block> SAFE = Arrays.asList(
        Blocks.STONE, Blocks.COBBLESTONE, Blocks.PLANKS, Blocks.BRICK_BLOCK,
        Blocks.GLASS, Blocks.WOOL, Blocks.STONEBRICK, Blocks.SANDSTONE,
        Blocks.QUARTZ_BLOCK, Blocks.HARDENED_CLAY, Blocks.CONCRETE,
        Blocks.LOG, Blocks.LOG2, Blocks.GLOWSTONE, Blocks.IRON_BLOCK,
        Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.NETHER_BRICK,
        Blocks.RED_NETHER_BRICK, Blocks.PRISMARINE
    );
    private BuildPalette() { }
    public static boolean isSafe(Block b) { return SAFE.contains(b); }
}
