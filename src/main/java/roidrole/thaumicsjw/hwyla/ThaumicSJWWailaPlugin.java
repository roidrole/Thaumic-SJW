package roidrole.thaumicsjw.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import roidrole.thaumicsjw.Tags;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.blocks.devices.BlockVisBattery;

@WailaPlugin
public class ThaumicSJWWailaPlugin implements IWailaPlugin {
	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
		registrar.registerNBTProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);

		registrar.registerBodyProvider(ProviderBlockVisBattery.INSTANCE, BlockVisBattery.class);

		registrar.registerBodyProvider(ProviderGogglesDisplay.INSTANCE, IGogglesDisplayExtended.class);

		registrar.registerTooltipRenderer("thaumicwaila.aspect", new RendererAspect());

		registrar.addConfig(Tags.MOD_NAME, Tags.MOD_ID+".aspects_as_text", "Show Aspects as Text", false);
		registrar.addConfig(Tags.MOD_NAME, Tags.MOD_ID+".require_goggles", "Require Goggles of Revealing",  true);
	}

}
