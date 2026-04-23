package roidrole.thaumicsjw.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AspectListIngredient {

	public static class Renderer implements IIngredientRenderer<AspectList> {
		@Override
		public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable AspectList ingredient) {
			if (ingredient != null && ingredient.size() > 0) {
				GL11.glPushMatrix();
				minecraft.renderEngine.bindTexture(ingredient.getAspects()[0].getImage());
				GL11.glEnable(3042);
				Color c = new Color(ingredient.getAspects()[0].getColor());
				GL11.glColor4f((float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 1.0F);
				Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, 16, 16, 16, 16);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glScaled(0.5, 0.5, 0.5);
				if(ingredient.getAmount(ingredient.getAspects()[0]) != 0) {
					minecraft.currentScreen.drawCenteredString(minecraft.fontRenderer, TextFormatting.WHITE + "" + ingredient.getAmount(ingredient.getAspects()[0]), (xPosition + 16) * 2, (yPosition + 12) * 2, 0);
				}
				GL11.glPopMatrix();
			}
		}

		@Override
		public List<String> getTooltip(Minecraft minecraft, AspectList ingredient, ITooltipFlag tooltipFlag) {
			return ingredient.size() > 0 ?
				Arrays.asList(
					TextFormatting.AQUA + ingredient.getAspects()[0].getName(),
					TextFormatting.GRAY + ingredient.getAspects()[0].getLocalizedDescription()
				) :
				Collections.emptyList();
		}

		@Override
		public FontRenderer getFontRenderer(Minecraft minecraft, AspectList ingredient) {
			return minecraft.fontRenderer;
		}
	}


	public static class Helper implements IIngredientHelper<AspectList> {
		@Override
		public List<AspectList> expandSubtypes(List<AspectList> ingredients) {
			return ingredients;
		}

		@Nullable
		@Override
		public AspectList getMatch(Iterable<AspectList> ingredients, AspectList ingredientToMatch) {
			for (AspectList list : ingredients) {
				if (list.getAspects()[0].getName().equalsIgnoreCase(ingredientToMatch.getAspects()[0].getName()))
					return list;
			}
			return null;
		}

		@Override
		public String getDisplayName(AspectList ingredient) {
			return ingredient.getAspects()[0].getName();
		}

		@Override
		public String getUniqueId(AspectList ingredient) {
			return ingredient.getAspects()[0].getName();
		}

		@Override
		public String getWildcardId(AspectList ingredient) {
			return "/";
		}

		@Override
		public String getModId(AspectList ingredient) {
			return "thaumcraft";
		}

		@Override
		public Iterable<Color> getColors(AspectList ingredient) {
			return Collections.singletonList(new Color(ingredient.getAspects()[0].getColor()));
		}

		@Override
		public String getResourceId(AspectList ingredient) {
			return ingredient.getAspects()[0].getName();
		}

		@Override
		public AspectList copyIngredient(AspectList ingredient) {
			return ingredient;
		}

		@Override
		public String getErrorInfo(AspectList ingredient) {
			return "";
		}

		@Override
		public ItemStack getCheatItemStack(AspectList ingredient) {
			ItemStack jar = new ItemStack(BlocksTC.jarNormal);
			NBTTagCompound tag = new NBTTagCompound();
			jar.setTagCompound(tag);
			NBTTagList list = new NBTTagList();
			tag.setTag("Aspects", list);

			NBTTagCompound aspectNBT = new NBTTagCompound();
			aspectNBT.setString("key", ingredient.aspects.keySet().iterator().next().getTag());
			aspectNBT.setInteger("amount", TileJarFillable.CAPACITY);
			list.appendTag(aspectNBT);

			return jar;
		}
	}

}
