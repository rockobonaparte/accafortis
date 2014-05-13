package accafortis;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeDirection;
import static net.minecraftforge.common.ForgeDirection.*;

/**
 * Created by RockoBonaparte on 5/10/14.
 */
public class BlockDrilledHole extends Block {

    protected enum DrilledIntoDirections {
        TOP,
        BOTTOM,
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NOTHING,
        UNKNOWN,
    }

    protected enum DrilledHolePhases
    {
        DRILLED_ONLY,
        GUNPOWDER_ADDED,
        CLAY_PLUGGED,
        UNKNOWN,
    }

    protected DrilledIntoDirections getDirectionFromMetadata(int metadata)
    {
        int directionField = metadata & 0x7;

        switch(directionField)
        {
            case 6:
                return DrilledIntoDirections.TOP;
            case 5:
                return DrilledIntoDirections.BOTTOM;
            case 4:
                return DrilledIntoDirections.NORTH;
            case 3:
                return DrilledIntoDirections.SOUTH;
            case 2:
                return DrilledIntoDirections.EAST;
            case 1:
                return DrilledIntoDirections.WEST;
            case 0:
                return DrilledIntoDirections.NOTHING;
            default:
                return DrilledIntoDirections.UNKNOWN;
        }
    }

    protected int setDirectionInMetadata(DrilledIntoDirections direction, int metadata)
    {
        metadata = metadata & 0xFFFFFF80;
        switch(direction)
        {
            case TOP:
                return metadata | 6;
            case BOTTOM:
                return metadata | 5;
            case NORTH:
                return metadata | 4;
            case SOUTH:
                return metadata | 3;
            case EAST:
                return metadata | 2;
            case WEST:
                return metadata | 1;
            case NOTHING:
                return metadata;
            default:
                return metadata | 7;
        }
    }

    protected DrilledHolePhases getDrilledHolePhase(int metadata)
    {
        int phaseField = (metadata & 0x18) >> 3;
        switch(phaseField)
        {
            // Going for a 3, 2, 1 countdown progression sequence since we're talking about blowing up stuff here. ;)
            case 3:
                return DrilledHolePhases.DRILLED_ONLY;
            case 2:
                return DrilledHolePhases.GUNPOWDER_ADDED;
            case 1:
                return DrilledHolePhases.CLAY_PLUGGED;
            default:
                return DrilledHolePhases.UNKNOWN;
        }
    }

    protected int setDrilledHoleMetadata(DrilledHolePhases phase, int metadata)
    {
        metadata = metadata & 0xFFFFFFE7;
        switch(phase)
        {
            case DRILLED_ONLY:
                return metadata | 3;
            case GUNPOWDER_ADDED:
                return metadata | 2;
            case CLAY_PLUGGED:
                return metadata | 1;
            default:
                return metadata;
        }
    }

    protected BlockDrilledHole(int par1)
    {
        super(par1, Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
                    setDrilledHoleMetadata(DrilledHolePhases.GUNPOWDER_ADDED, metadata);
                    world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                    return true;
                }
            }
                break;
            case GUNPOWDER_ADDED:
                if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.clay.itemID)
                {
                    setDrilledHoleMetadata(DrilledHolePhases.CLAY_PLUGGED, metadata);
                    world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
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
        if(!world.isRemote)
        {
            EntityMiningTNT entity = new EntityMiningTNT(world, x + 0.5F, y + 0.5F, z + 0.5F);
            world.spawnEntityInWorld(entity);
            world.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
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
        return 2;
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
        int j1 = par9;

        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4))
        {
            j1 = 5;
        }

        if (par5 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true))
        {
            j1 = 4;
        }

        if (par5 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true))
        {
            j1 = 3;
        }

        if (par5 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST, true))
        {
            j1 = 2;
        }

        if (par5 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST, true))
        {
            j1 = 1;
        }

        setDrilledHoleMetadata(DrilledHolePhases.DRILLED_ONLY, j1);

        return j1;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        super.updateTick(par1World, par2, par3, par4, par5Random);

        if (par1World.getBlockMetadata(par2, par3, par4) == 0)
        {
            this.onBlockAdded(par1World, par2, par3, par4);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0)
        {
            int metadata = 0;
            if (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST, true))
            {
                metadata = 1;
            }
            else if (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST, true))
            {
                metadata = 2;
            }
            else if (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true))
            {
                metadata = 3;
            }
            else if (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true))
            {
                metadata = 4;
            }
            else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4))
            {
                metadata = 5;
            }

            setDrilledHoleMetadata(DrilledHolePhases.DRILLED_ONLY, metadata);
            par1World.setBlockMetadataWithNotify(par2, par3, par4, metadata, 2);

        }

        this.dropTorchIfCantStay(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int x, int y, int z, int par5)
    {
        this.func_94397_d(par1World, x, y, z, par5);
    }

    protected boolean func_94397_d(World par1World, int x, int y, int z, int par5)
    {
        if (this.dropTorchIfCantStay(par1World, x, y, z))
        {
            int i1 = par1World.getBlockMetadata(x, y, z);
            boolean flag = false;

            if (!par1World.isBlockSolidOnSide(x - 1, y, z, EAST, true) && i1 == 1)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(x + 1, y, z, WEST, true) && i1 == 2)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(x, y, z - 1, SOUTH, true) && i1 == 3)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(x, y, z + 1, NORTH, true) && i1 == 4)
            {
                flag = true;
            }

            if (!this.canPlaceTorchOn(par1World, x, y - 1, z) && i1 == 5)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(par1World, x, y, z, par1World.getBlockMetadata(x, y, z), 0);
                par1World.setBlockToAir(x, y, z);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Tests if the block can remain at its current location and will drop as an item if it is unable to stay. Returns
     * True if it can stay and False if it drops. Args: world, x, y, z
     */
    protected boolean dropTorchIfCantStay(World par1World, int par2, int par3, int par4)
    {
        if (!this.canPlaceBlockAt(par1World, par2, par3, par4))
        {
            if (par1World.getBlockId(par2, par3, par4) == this.blockID)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockToAir(par2, par3, par4);
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4) & 7;
        float f = 0.15F;

        if (l == 1)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        }
        else if (l == 2)
        {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        }
        else if (l == 3)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        }
        else if (l == 4)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
        else
        {
            f = 0.1F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }

        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }
}
