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
package roidrole.thaumicsjw.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import roidrole.thaumicsjw.jei.categories.AbstractResearchCategory;
import roidrole.thaumicsjw.jei.categories.IHasResearch;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

public class ResearchManager {
    private static int timeToSync = -1;

    public static IJeiRuntime runtime;

    @SubscribeEvent
    public static void onResearch(ResearchEvent.Research event) {
        //Setting a delay to be sure that the research is completed
        timeToSync = 5;
    }

    @SubscribeEvent
    public static void onJoin(WorldEvent.Load event) {
        if (event.getWorld() instanceof WorldClient) {
            timeToSync = 20;
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (timeToSync >= 0 && event.phase == TickEvent.Phase.END) {
            if (timeToSync == 0) {
                sync();
                return;
            }
            timeToSync--;
        }
    }

    private static void sync() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || runtime == null) {
            timeToSync = 20;
            return;
        }
        if (!player.hasCapability(ThaumcraftCapabilities.KNOWLEDGE, null)){
            return;
        }

        for (AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
            for (IRecipeWrapper wrapper : category.recipes) {
                runtime.getRecipeRegistry().hideRecipe(wrapper, category.getUid());
            }
        }

        IPlayerKnowledge knowledge = player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        if (knowledge == null) {
            timeToSync = 100;
            return;
        }
        for (AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
            for(IHasResearch wrapper : category.recipes){
                boolean hasAll = true;
                for (String subResearch : wrapper.getResearch().split("&&")) {
                    if (!knowledge.isResearchComplete(subResearch)) {
                        hasAll = false;
                        break;
                    }
                }
                if (hasAll) {
                    runtime.getRecipeRegistry().unhideRecipe(wrapper, category.getUid());
                }
            }
        }
    }

}
