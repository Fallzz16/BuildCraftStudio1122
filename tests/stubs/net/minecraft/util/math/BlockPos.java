package net.minecraft.util.math;
/** Test-only stub, excluded from the mod JAR. */
public final class BlockPos {
    private final int x,y,z;
    public BlockPos(int x,int y,int z) {this.x=x;this.y=y;this.z=z;}
    public int getX(){return x;}
    public int getY(){return y;}
    public int getZ(){return z;}
    @Override public boolean equals(Object o){
        if(!(o instanceof BlockPos))return false;
        BlockPos p=(BlockPos)o;return x==p.x&&y==p.y&&z==p.z;
    }
    @Override public int hashCode(){return ((x*31)+y)*31+z;}
}
