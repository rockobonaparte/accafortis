package miningexplosives;

// Credits must go to Mekanism for providing their code up on GitHub.  
// I heavily relied on it to figure out the general structure of modding.
// Their obsidian TNT source was particularly useful. 

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiningTNTBlock extends Block
{
	@SideOnly(Side.CLIENT)
	private Icon topIcon;
	
	@SideOnly(Side.CLIENT)
	private Icon bottomIcon;
	
	public MiningTNTBlock(int id)
	{
		super(id, Material.tnt);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        
//		this.textureName = "tnt";
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		if(world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			explode(world, x, y, z);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if(world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			explode(world, x, y, z);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		if(!world.isRemote)
		{
			MiningTNTEntity entity = new MiningTNTEntity(world, x + 0.5F, y + 0.5F, z + 0.5F);
			entity.fuse = world.rand.nextInt(entity.fuse / 4) + entity.fuse / 8;
			world.spawnEntityInWorld(entity);
		}
	}

	public void explode(World world, int x, int y, int z)
	{
		if(!world.isRemote)
		{
			MiningTNTEntity entity = new MiningTNTEntity(world, x + 0.5F, y + 0.5F, z + 0.5F);
			world.spawnEntityInWorld(entity);
			world.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int i1, float f1, float f2, float f3)
	{
		if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID)
		{
			explode(world, x, y, z);
			world.setBlockToAir(x, y, z);
			return true;
		}
		else {
			return super.onBlockActivated(world, x, y, z, entityplayer, i1, f1, f2, f3);
		}
	}

	@Override
	public boolean canDropFromExplosion(Explosion explosion)
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return false;
	}

//	@Override
//	public TileEntity createTileEntity(World world, int metadata)
//	{
//		return new MiningTNTTileEntity();
//	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}
	
	@Override
    public Icon getIcon(int par1, int par2)
    {
        return par1 == 0 ? this.bottomIcon : (par1 == 1 ? this.topIcon : this.blockIcon);
        
    }

    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
	
	
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tnt_side");
        this.topIcon = par1IconRegister.registerIcon("tnt_top");
        this.bottomIcon = par1IconRegister.registerIcon("tnt_bottom");
    }	
	
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if(entity instanceof EntityArrow && !world.isRemote)
		{
			EntityArrow entityarrow = (EntityArrow)entity;

			if(entityarrow.isBurning())
			{
				explode(world, x, y, z);
				world.setBlockToAir(x, y, z);
			}
		}
	}
}
