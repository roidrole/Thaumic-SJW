package roidrole.thaumicsjw.jei.categories;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IHasResearch extends IRecipeWrapper {
    String getResearch();

    @Override
    default List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch())){
            return Collections.emptyList();
        }
        if(mouseX < getBarrierX() || mouseX > getBarrierX() + 16 || mouseY < getBarrierY() || mouseY > getBarrierY() + 16){
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        list.add(TextFormatting.GOLD + "Missing research:");
        for (String s : getResearch().split("&&")) {
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, s)) {
                ResearchEntry entry = ResearchCategories.getResearch(s.contains("@") ? s.split("@")[0] : s);
                if (entry != null) list.add("- " + TextFormatting.RED + entry.getLocalizedName());
                else list.add("- " + TextFormatting.RED + s);
            }
        }

        return list;
    }

    @Override
    default void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, this.getResearch())) {
            minecraft.getRenderItem().renderItemIntoGUI(new ItemStack(Blocks.BARRIER), getBarrierX(), getBarrierY());
        }
    }

    int getBarrierX();
    int getBarrierY();
}
