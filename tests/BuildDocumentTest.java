import dev.buildcraftstudio.core.BuildDocument;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
public final class BuildDocumentTest {
    static int passed;
    static final IBlockState STONE=new IBlockState(){};
    static final IBlockState WOOD=new IBlockState(){};
    static void t(boolean v){if(!v)throw new AssertionError("failure #"+passed);passed++;}
    static BlockPos p(int x,int y,int z){return new BlockPos(x,y,z);}
    public static void main(String[] args){
        BuildDocument b=new BuildDocument(p(0,60,0));
        t(b.blocks().isEmpty());
        t(!b.isDirty());
        t(!b.edit(p(32,0,0),STONE));
        t(!b.edit(p(0,32,0),STONE));
        t(!b.edit(p(0,0,-33),STONE));
        t(b.edit(p(-32,-32,-32),STONE));
        t(b.edit(p(31,31,31),WOOD));
        t(b.blocks().size()==2);
        t(!b.edit(p(31,31,31),WOOD));
        t(b.edit(p(31,31,31),STONE));
        t(b.blocks().get(p(31,31,31))==STONE);
        t(b.undo());t(b.blocks().get(p(31,31,31))==WOOD);
        t(b.undo());t(!b.blocks().containsKey(p(31,31,31)));
        t(b.redo());t(b.blocks().get(p(31,31,31))==WOOD);
        t(b.edit(p(0,0,0),STONE));
        t(!b.redo());
        t(b.isDirty());
        b.saved();t(!b.isDirty());
        b.loadBlock(p(5,5,5),WOOD);t(b.blocks().get(p(5,5,5))==WOOD);
        for(int x=-32;x<32;x++)for(int z=-32;z<32 && b.blocks().size()<BuildDocument.MAX_PREVIEW_BLOCKS;z++) b.edit(p(x,1,z),STONE);
        t(b.blocks().size()==BuildDocument.MAX_PREVIEW_BLOCKS);
        t(!b.edit(p(0,2,0),STONE));
        t(b.edit(p(0,0,0),null));
        t(b.edit(p(0,2,0),STONE));
        System.out.println("PASS: "+passed+" BuildDocument assertions");
    }
}
