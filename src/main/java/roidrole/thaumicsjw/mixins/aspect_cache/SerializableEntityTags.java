package roidrole.thaumicsjw.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@Mixin(ThaumcraftApi.EntityTags.class)
public abstract class SerializableEntityTags implements Externalizable {
	@Shadow(remap = false)
	public String entityName;

	@Shadow(remap = false)
	public AspectList aspects;

	@Shadow(remap = false)
	public ThaumcraftApi.EntityTagsNBT[] nbts;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.entityName);
		out.writeObject(this.aspects);
		out.write(this.nbts.length);
		for (ThaumcraftApi.EntityTagsNBT currentTag : this.nbts) {
			out.writeObject(currentTag.name);
			out.writeObject(currentTag.value);
		}
		out.writeObject(this.nbts);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.entityName = (String) in.readObject();
		this.aspects = (AspectList) in.readObject();
		this.nbts = new ThaumcraftApi.EntityTagsNBT[in.read()];
		for (int i = 0; i < this.nbts.length; i++) {
			this.nbts[i] = new ThaumcraftApi.EntityTagsNBT((String)in.readObject(), in.readObject());
		}
	}
}
