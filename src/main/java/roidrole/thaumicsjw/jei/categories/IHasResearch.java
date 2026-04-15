package roidrole.thaumicsjw.jei.categories;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
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
        if (mouseX > 92 && mouseX < 108 && mouseY > 9 && mouseY < 25) {
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
        return Collections.emptyList();
    }
}
