package miningexplosives;

// Credits must go to Mekanism for providing their code up on GitHub.  
// I heavily relied on it to figure out the general structure of modding.
// ...although I don't really understand the proxy thing quite yet.
// Most of this is probably spurious now; please don't hate me while I figure out what I'm doing.


import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;

/**
 * Common proxy for the Mekanism mod.
 * 
 * Whoops ripped that from Mekanism too hastily!  Making this the MiningTNTCommonProxy while I figure out WTF.
 *
 */
public class MiningTNTCommonProxy {

	/**
	 * Register tile entities that have special models. Overwritten in client to register TESRs.
	 */
	public void registerSpecialTileEntities()
	{
		GameRegistry.registerTileEntity(MiningTNTTileEntity.class, "MiningTNT");
	}

	/**
	 * Registers a client-side sound, assigned to a TileEntity.
	 * @param obj - TileEntity who is registering the sound
	 */
	public void registerSound(Object obj) {}

	/**
	 * Unregisters a client-side sound, assigned to a TileEntity;
	 * @param tileEntity - TileEntity who is unregistering the sound
	 */
	public void unregisterSound(TileEntity tileEntity) {}

	/**
	 * Handles an ELECTRIC_CHEST_CLIENT_OPEN packet via the proxy, not handled on the server-side.
	 * @param entityplayer - player the packet was sent from
	 * @param id - the electric chest gui ID to open
	 * @param windowId - the container-specific window ID
	 * @param isBlock - if the chest is a block
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param z - z coordinate
	 */
	public void openElectricChest(EntityPlayer entityplayer, int id, int windowId, boolean isBlock, int x, int y, int z) {}

	/**
	 * Register and load client-only render information.
	 */
	public void registerRenderInformation() {}

	/**
	 * Gets the armor index number from ClientProxy.
	 * @param string - armor indicator
	 * @return armor index number
	 */
	public int getArmorIndex(String string)
	{
		return 0;
	}

	/**
	 * Set and load the mod's common configuration properties.
	 */
	public void loadConfiguration()
	{
		MiningTNT.miningTNTID = 1337;
	}

	/**
	 * Set up and load the utilities this mod uses.
	 */
	public void loadUtilities()
	{
	}

	/**
	 * Set up and load the sound handler.
	 */
	public void loadSoundHandler() {}

	/**
	 * Unload the sound handler.
	 */
	public void unloadSoundHandler() {}

	/**
	 * Whether or not the game is paused.
	 */
	public boolean isPaused()
	{
		return false;
	}

	/**
	 * Get the actual interface for a GUI. Client-only.
	 * @param ID - gui ID
	 * @param player - player that opened the GUI
	 * @param world - world the GUI was opened in
	 * @param x - gui's x position
	 * @param y - gui's y position
	 * @param z - gui's z position
	 * @return the GuiScreen of the GUI
	 */
	public Object getClientGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	/**
	 * Get the container for a GUI. Common.
	 * @param ID - gui ID
	 * @param player - player that opened the GUI
	 * @param world - world the GUI was opened in
	 * @param x - gui's x position
	 * @param y - gui's y position
	 * @param z - gui's z position
	 * @return the Container of the GUI
	 */
	public Container getServerGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		return null;
	}

	public void preInit() {}

	public double getReach(EntityPlayer player)
	{
		if(player instanceof EntityPlayerMP)
		{
			return ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
		}

		return 0;
	}

	/**
	 * Gets the Minecraft base directory.
	 * @return base directory
	 */
	public File getMinecraftDir()
	{
		return (File)FMLInjectionData.data()[6];
	}

	public void onConfigSync()
	{
	}
}
