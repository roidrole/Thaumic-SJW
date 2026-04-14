package roidrole.thaumicsjw.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.blocks.devices.BlockVisBattery;

@WailaPlugin
public class TFUtilsWailaPlugin implements IWailaPlugin {
	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
		registrar.registerNBTProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);

		registrar.registerBodyProvider(ProviderBlockVisBattery.INSTANCE, BlockVisBattery.class);

		registrar.registerBodyProvider(ProviderGogglesDisplay.INSTANCE, IGogglesDisplayExtended.class);

		registrar.registerTooltipRenderer("thaumicwaila.aspect", new RendererAspect());
	}

}
