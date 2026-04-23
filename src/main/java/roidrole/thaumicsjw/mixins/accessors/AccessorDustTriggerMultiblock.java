package roidrole.thaumicsjw.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;

@Mixin(DustTriggerMultiblock.class)
public interface AccessorDustTriggerMultiblock {
	@Accessor(remap = false)
	String getResearch();
}
