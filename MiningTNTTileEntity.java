package miningexplosives;

// Credits must go to Mekanism for providing their code up on GitHub.  
// I heavily relied on it to figure out the general structure of modding.
// Their obsidian TNT source was particularly useful, however, I don't 
// understand tile entities yet.

import net.minecraft.tileentity.TileEntity;

//For a TESR
public class MiningTNTTileEntity extends TileEntity
{
	@Override
	public boolean canUpdate()
	{
		return false;
	}
}
