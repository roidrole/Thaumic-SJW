/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
