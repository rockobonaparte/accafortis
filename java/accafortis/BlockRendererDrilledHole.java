package accafortis;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.Icon;

/**
 * Created by Adam on 5/16/14.
 */
public class BlockRendererDrilledHole implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        int l = world.getBlockMetadata(x, y, z);
        Icon icon = block.getIcon(0, l);

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        float f = 1.0F;
        tessellator.setColorOpaque_F(f, f, f);
        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getMinV();
        double d2 = (double)icon.getMaxU();
        double d3 = (double)icon.getMaxV();
        double d4 = 0.0D;                   // WTF zero?
        double d5 = 0.05000000074505806D;   // magic number taken from ladder block renderer.  Don't ask me I just work here.

        BlockDrilledHole.DrilledIntoDirections direction = BlockDrilledHole.getDirectionFromMetadata(l);

        switch(direction) {
            case NORTH:
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 1) + d4, (double)(z + 1) - d5, d0, d1);
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 0) - d4, (double)(z + 1) - d5, d0, d3);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 0) - d4, (double)(z + 1) - d5, d2, d3);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 1) + d4, (double)(z + 1) - d5, d2, d1);
                break;

            case SOUTH:
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 0) - d4, (double)z + d5, d2, d3);
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 1) + d4, (double)z + d5, d2, d1);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 1) + d4, (double)z + d5, d0, d1);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 0) - d4, (double)z + d5, d0, d3);
                break;

            case WEST:
                tessellator.addVertexWithUV((double)(x + 1) - d5, (double)(y + 0) - d4, (double)(z + 1) + d4, d2, d3);
                tessellator.addVertexWithUV((double)(x + 1) - d5, (double)(y + 1) + d4, (double)(z + 1) + d4, d2, d1);
                tessellator.addVertexWithUV((double)(x + 1) - d5, (double)(y + 1) + d4, (double)(z + 0) - d4, d0, d1);
                tessellator.addVertexWithUV((double)(x + 1) - d5, (double)(y + 0) - d4, (double)(z + 0) - d4, d0, d3);
                break;

            case EAST:
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 1) + d4, (double)(z + 1) + d4, d0, d1);
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 0) - d4, (double)(z + 1) + d4, d0, d3);
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 0) - d4, (double)(z + 0) - d4, d2, d3);
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 1) + d4, (double)(z + 0) - d4, d2, d1);
                break;

            case TOP:
                tessellator.addVertexWithUV((double)(x + 0) + d4, (double)(y + 0) + d5, (double)(z + 1) - d4, d0, d3);
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 0) + d5, (double)(z + 1) - d4, d0, d1);
                tessellator.addVertexWithUV((double)(x + 1) - d4, (double)(y + 0) + d5, (double)(z + 0) - d4, d2, d1);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 0) + d5, (double)(z + 0) - d4, d2, d3);
                break;

            case BOTTOM:
                tessellator.addVertexWithUV((double)(x + 1) - d4, (double)(y + 0) + d5, (double)(z + 0) - d4, d2, d1);
                tessellator.addVertexWithUV((double)(x + 1) + d4, (double)(y + 0) + d5, (double)(z + 1) - d4, d0, d1);
                tessellator.addVertexWithUV((double)(x + 0) + d4, (double)(y + 0) + d5, (double)(z + 1) - d4, d0, d3);
                tessellator.addVertexWithUV((double)(x + 0) - d4, (double)(y + 0) + d5, (double)(z + 0) - d4, d2, d3);
                break;

            default:
                // Fall-through: Let's do something absolutely gross so somebody will report it, but it won't crash
                // anything!  Diagonals!
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 1) + d4, (double)(z + 1) + d4, d0, d1);
                tessellator.addVertexWithUV((double)x + d5, (double)(y + 0) - d4, (double)(z + 1) + d4, d0, d3);
                tessellator.addVertexWithUV((double)x, (double)(y + 0) - d4, (double)(z + 0) - d4, d2, d3);
                tessellator.addVertexWithUV((double)x, (double)(y + 1) + d4, (double)(z + 0) - d4, d2, d1);
                break;
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }

    @Override
    public int getRenderId() {
        return AccaFortis.drilledHoleRendererID;
    }
}
