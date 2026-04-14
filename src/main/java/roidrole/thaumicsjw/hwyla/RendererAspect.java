package roidrole.thaumicsjw.hwyla;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nonnull;
import java.awt.*;

public class RendererAspect implements IWailaTooltipRenderer {
	@Nonnull
	@Override
	public Dimension getSize(String[] args, IWailaCommonAccessor accessor) {
		return new Dimension(8, 8);
	}

	@Override
	public void draw(String[] args, IWailaCommonAccessor accessor) {
		Aspect aspect = Aspect.getAspect(args[0]);
		drawAspect(aspect);
	}

	private void drawAspect(Aspect aspect) {
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(aspect.getImage());
		GlStateManager.enableBlend();

		GlStateManager.color(
			(float) ((aspect.getColor() >> 16) & 0xFF) / 255.0F,
			(float) ((aspect.getColor() >> 8) & 0xFF) / 255.0F,
			(float) ((aspect.getColor()) & 0xFF) / 255.0F
		);
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 8, 8, 8, 8);
		GlStateManager.popMatrix();
	}
}
