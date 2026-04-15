package roidrole.thaumicsjw;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import roidrole.thaumicsjw.jei.ResearchManager;


@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies =
        "required-after:jei@[1.12.2-4.15.0.275,);" +
        "required-after:thaumcraft@[6.1.BETA20,);"
)
public class ThaumicSJW {
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        if(event.getSide() == Side.CLIENT && ThaumicSJWConfig.jeiConfig.hideRecipesIfMissingResearch){
            MinecraftForge.EVENT_BUS.register(ResearchManager.class);
        }
    }
}