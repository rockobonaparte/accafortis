package accafortis;

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


@Mod(modid = "accafortis", name = "accafortis", version = "alpha")
@NetworkMod(clientSideRequired = true)
public class AccaFortis
{
    @Instance(value = "accafortis")
    public static AccaFortis instance;
	public static int miningTNTID;
    public static int drilledHoleID;

	public static Block blockMiningTnt;
    public static Block blockDrilledHole;
        
	@SidedProxy(clientSide = "accafortis.AccaFortisCommonProxy", serverSide = "accafortis.AccaFortisCommonProxy")
    public static AccaFortisCommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.loadConfiguration();
    	
    	blockMiningTnt = new BlockMiningTnt(miningTNTID).setUnlocalizedName("MiningTNT");
        blockDrilledHole = new BlockDrilledHole(drilledHoleID).setUnlocalizedName("DrilledHole");
		GameRegistry.registerBlock(blockMiningTnt, "MiningTNT");
        GameRegistry.registerBlock(blockDrilledHole, "DrilledHole");

		//Registrations
		EntityRegistry.registerModEntity(EntityMiningTNT.class, "MiningTNT", EntityRegistry.findGlobalUniqueEntityId(), this, 40, 5, true);

		// Recipes
		CraftingManager.getInstance().addRecipe(new ItemStack(blockMiningTnt, 1),
			new Object[] { "sgs", "gpg", "sgs", 's', Block.sand, 'g', Item.gunpowder, 'p', Item.paper });
		
		proxy.registerRenderInformation();
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