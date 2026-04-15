package roidrole.thaumicsjw.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.thaumicsjw.CacheManager;
import thaumcraft.common.config.ConfigAspects;

@Mixin(ConfigAspects.class)
public abstract class DisableNormalLoading {
	@Inject(
		method = "postInit",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void thaumicsjw_readCaches(CallbackInfo ci){
		if(CacheManager.canRunCaches()){
			CacheManager.parseAspectCache();
			CacheManager.parseEntityCache();
			ci.cancel();
		}
	}
}
