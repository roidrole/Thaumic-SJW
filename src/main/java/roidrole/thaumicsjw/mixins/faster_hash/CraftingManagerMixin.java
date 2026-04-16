package roidrole.thaumicsjw.mixins.faster_hash;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roidrole.thaumicsjw.CacheManager;
import roidrole.thaumicsjw.ThaumicSJW;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.util.ArrayList;

@Mixin(ThaumcraftCraftingManager.class)
public abstract class CraftingManagerMixin {
	@Inject(
		method = "generateTags(Lnet/minecraft/item/ItemStack;Ljava/util/ArrayList;)Lthaumcraft/api/aspects/AspectList;",
		at = @At("HEAD"),
		remap = false
	)
	private static void logCacheMiss(ItemStack is, ArrayList<String> history, CallbackInfoReturnable<AspectList> cir){
		ThaumicSJW.LOGGER.warn("A non-cached ItemStack has its aspects computed. The stack: {}", CacheManager.writeItemStack(is, is.getCount()));
	}
}
