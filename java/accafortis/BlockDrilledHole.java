package accafortis;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeDirection;
import static net.minecraftforge.common.ForgeDirection.*;

/**
 * Created by RockoBonaparte on 5/10/14.
 */
public class BlockDrilledHole extends Block {

    @SideOnly(Side.CLIENT)
    private Icon drilledIcon;

    @SideOnly(Side.CLIENT)
    private Icon filledIcon;

    @SideOnly(Side.CLIENT)
    private Icon pluggedIcon;

    protected enum DrilledIntoDirections {
        TOP,
        BOTTOM,
        NORTH,
        SOUTH,
        EAST,
        WEST,
        UNKNOWN,
    }

    protected enum DrilledHolePhases
    {
        DRILLED_ONLY,
        GUNPOWDER_ADDED,
        CLAY_PLUGGED,
    }

    // These metadata helpers have to be public because things like external block renderers need to know what they are.
    public static DrilledIntoDirections getDirectionFromMetadata(int metadata)
    {
        switch(metadata / 3)
        {
            case 5:
                return DrilledIntoDirections.BOTTOM;
            case 4:
                return DrilledIntoDirections.TOP;
            case 3:
                return DrilledIntoDirections.NORTH;
            case 2:
                return DrilledIntoDirections.SOUTH;
            case 1:
                return DrilledIntoDirections.EAST;
            case 0:
                return DrilledIntoDirections.WEST;
            default:
                return DrilledIntoDirections.UNKNOWN;       // Shouldn't happen, given metadata's highest value is 15.
        }
    }

    public static int setDirectionInMetadata(DrilledIntoDirections direction, int metadata)
    {
        // Need to eliminate current direction
        metadata = metadata % 3;

        switch(direction)
        {
            case BOTTOM:
                return metadata + 15;
            case TOP:
                return metadata + 12;
            case NORTH:
                return metadata + 9;
            case SOUTH:
                return metadata + 6;
            case EAST:
                return metadata + 3;
            case WEST:
                return metadata;
            default:
                return metadata;
        }
    }

    public static DrilledHolePhases getDrilledHolePhase(int metadata)
    {

        switch(metadata % 3)
        {
            case 0:
                return DrilledHolePhases.DRILLED_ONLY;
            case 1:
                return DrilledHolePhases.GUNPOWDER_ADDED;
            case 2:
                return DrilledHolePhases.CLAY_PLUGGED;
            default:
                return DrilledHolePhases.DRILLED_ONLY;
        }
    }

    public static int setDrilledHoleMetadata(DrilledHolePhases phase, int metadata)
    {
        // Need to eliminate current phase before applying new one
        metadata -= metadata % 3;

        switch(phase)
        {
            case GUNPOWDER_ADDED:
                return metadata + 1;
            case CLAY_PLUGGED:
                return metadata + 2;
            default:
                return metadata;
        }
    }

    protected BlockDrilledHole(int par1)
    {
        super(par1, Material.circuits);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        DrilledIntoDirections direction = getDirectionFromMetadata(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
        float f = 0.125F;
        switch(direction)
        {
            case TOP:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
                break;
            case BOTTOM:
                this.setBlockBounds(0.0F, 1.0F, 0.0F, 1.0F, 1.0F - f, 1.0F);
                break;
            case NORTH:
                this.setBlockBounds(0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F - f);
                break;
            case SOUTH:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0f, 1.0F, f);
                break;
            case EAST:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                break;
            case WEST:
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
        }
    }




    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int i1, float f1, float f2, float f3)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        switch(getDrilledHolePhase(metadata))
        {
            case DRILLED_ONLY:
            {
                if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.gunpowder.itemID)
                {
                    metadata = setDrilledHoleMetadata(DrilledHolePhases.GUNPOWDER_ADDED, metadata);
                    world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                    this.blockIcon = this.filledIcon;
                    world.markBlockForRenderUpdate(x, y, z);
                    return true;
                }
            }
                break;
            case GUNPOWDER_ADDED:
                if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.clay.itemID)
                {
                    metadata = setDrilledHoleMetadata(DrilledHolePhases.CLAY_PLUGGED, metadata);
                    world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                    this.blockIcon = this.pluggedIcon;
                    Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(x, y, z);
                    world.markBlockForRenderUpdate(x, y, z);
                    return true;
                }
                break;
            case CLAY_PLUGGED:
                if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID)
                {
                    explode(world, x, y, z);
                    world.setBlockToAir(x, y, z);
                    return true;
                }
                break;
            default:
                break;
        }

        return super.onBlockActivated(world, x, y, z, entityplayer, i1, f1, f2, f3);
    }

    // TODO: Switch an entity representing the block about to explode.
    public void explode(World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        DrilledIntoDirections direction = getDirectionFromMetadata(metadata);

        int attachedBlock = 0;

        switch(direction) {
            case TOP:
                attachedBlock = world.getBlockId(x, y - 1, z);
                break;
            case BOTTOM:
                attachedBlock = world.getBlockId(x, y + 1, z);
                break;
            case NORTH:
                attachedBlock = world.getBlockId(x, y, z + 1);
                break;
            case SOUTH:
                attachedBlock = world.getBlockId(x, y, z - 1);
                break;
            case EAST:
                attachedBlock = world.getBlockId(x - 1, y, z);
                break;
            case WEST:
                attachedBlock = world.getBlockId(x + 1, y, z);
                break;
        }

        if(attachedBlock != 0) {
            if(!world.isRemote) {
                EntityDrilledHoleExploding entity = new EntityDrilledHoleExploding(world, x, y, z, attachedBlock);
                world.spawnEntityInWorld(entity);
                world.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
            }
        }
    }



    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        // return 8;
        return AccaFortis.drilledHoleRendererID;
    }

    /**
     * Gets if we can place a torch on a block.
     */
    private boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4)
    {
        if (par1World.doesBlockHaveSolidTopSurface(par2, par3, par4))
        {
            return true;
        }
        else
        {
            int l = par1World.getBlockId(par2, par3, par4);
            return (Block.blocksList[l] != null && Block.blocksList[l].canPlaceTorchOnTop(par1World, par2, par3, par4));
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST,  true) ||
                par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST,  true) ||
                par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true) ||
                par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true) ||
                canPlaceTorchOn(par1World, par2, par3 - 1, par4);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        int metadata = par9;

        // TODO: Figure out the logic for placing DOWN

        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4))
        {
            metadata = 12;
        }

        if (par5 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true))
        {
            metadata = 9;
        }

        if (par5 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true))
        {
            metadata = 6;
        }

        if (par5 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST, true))
        {
            metadata = 3;
        }

        if (par5 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST, true))
        {
            metadata = 0;
        }


        metadata = setDrilledHoleMetadata(DrilledHolePhases.DRILLED_ONLY, metadata);

        return metadata;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int x, int y, int z, int par5)
    {
        int metadata = par1World.getBlockMetadata(x, y, z);
        DrilledIntoDirections direction = getDirectionFromMetadata(metadata);
        boolean drop = false;

        switch(direction)
        {
            case TOP:
                drop = !par1World.isBlockSolidOnSide(x, y + 1, z, UP);
                break;
            case BOTTOM:
                drop = !par1World.isBlockSolidOnSide(x, y - 1, z, DOWN);
                break;
            case NORTH:
                drop = !par1World.isBlockSolidOnSide(x, y, z + 1, NORTH);
                break;
            case SOUTH:
                drop = !par1World.isBlockSolidOnSide(x, y, z - 1, SOUTH);
                break;
            case EAST:
                drop = !par1World.isBlockSolidOnSide(x - 1, y, z, EAST);
                break;
            case WEST:
                drop = !par1World.isBlockSolidOnSide(x + 1, y, z, WEST);
                break;
        }

        if(drop)
        {
            par1World.setBlockToAir(x, y, z);
        }

        super.onNeighborBlockChange(par1World, x, y, z, par5);
    }

    @Override
    public Icon getIcon(int side, int metadata)
    {
        DrilledHolePhases phase = getDrilledHolePhase(metadata);
        switch(phase)
        {
            case DRILLED_ONLY:
                return this.drilledIcon;
            case GUNPOWDER_ADDED:
                return this.filledIcon;
            case CLAY_PLUGGED:
                return this.pluggedIcon;
            default:
                return this.drilledIcon;        // Really here just to make javac happy that this method will return...
        }
    }

    public void registerIcons(IconRegister par1IconRegister)
    {
        this.drilledIcon = par1IconRegister.registerIcon("accafortis:drilledhole");
        this.filledIcon = par1IconRegister.registerIcon("accafortis:filledhole");
        this.pluggedIcon = par1IconRegister.registerIcon("accafortis:pluggedhole");
    }

}
