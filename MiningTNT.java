package miningexplosives;

// Credits must go to Mekanism for providing their code up on GitHub.  
// I heavily relied on it to figure out the general structure of modding.

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;


@Mod(modid = "MinecraftExplosives", name = "MinecraftExplosives", version = "alpha")
@NetworkMod(clientSideRequired = true)
public class MiningTNT
{
    @Instance(value = "MinecraftExplosives")
    public static MiningTNT instance;
	public static int miningTNTID;

	public static Block miningTNTBlock;
        
	@SidedProxy(clientSide = "miningexplosives.MiningTNTCommonProxy", serverSide = "miningexplosives.MiningTNTCommonProxy")
    public static MiningTNTCommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.loadConfiguration();
    	
    	miningTNTBlock = new MiningTNTBlock(miningTNTID).setUnlocalizedName("MiningTNT");
		GameRegistry.registerBlock(miningTNTBlock, "MiningTNT");
    	  
		//Registrations
		EntityRegistry.registerModEntity(MiningTNTEntity.class, "MiningTNT", EntityRegistry.findGlobalUniqueEntityId(), this, 40, 5, true);

		// Recipes
		CraftingManager.getInstance().addRecipe(new ItemStack(miningTNTBlock, 1), 
			new Object[] { "sgs", "gpg", "sgs", 's', Block.sand, 'g', Item.gunpowder, 'p', Item.paper });
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}