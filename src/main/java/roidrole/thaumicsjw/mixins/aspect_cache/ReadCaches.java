package roidrole.thaumicsjw.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.thaumicsjw.CacheManager;
import roidrole.thaumicsjw.ThaumicSJW;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.ConfigAspects;

@Mixin(ConfigAspects.class)
public abstract class ReadCaches {
	@Inject(
		method = "postInit",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void thaumicsjw_readCaches(CallbackInfo ci){
		if(CacheManager.canRunCaches()){
			ThaumicSJW.LOGGER.info("Loading caches");
			CacheManager.parseAspectCache();
			CacheManager.parseEntityCache();
			ThaumicSJW.LOGGER.info("Loaded {} aspect entries from cache", CommonInternals.objectTags.size());
			ci.cancel();
		}
	}
}
