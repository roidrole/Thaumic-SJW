package roidrole.thaumicsjw.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.ThaumcraftApi;

import java.io.*;

@Mixin(ThaumcraftApi.EntityTags.class)
public abstract class SerializableEntityTags implements Serializable {
	//Because there is no no-arg constructor
	private Object writeReplace() throws ObjectStreamException {
		return new roidrole.thaumicsjw.utils.SerializableEntityTags();
	}
}
