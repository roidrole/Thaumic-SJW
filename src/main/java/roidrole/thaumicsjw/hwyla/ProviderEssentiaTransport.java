package roidrole.thaumicsjw.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderEssentiaTransport implements IWailaDataProvider {
	public static ProviderEssentiaTransport INSTANCE = new ProviderEssentiaTransport();

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (!(te instanceof IEssentiaTransport)) {
			return tag;
		}
		if (!EntityUtils.hasGoggles(player)) {
			return tag;
		}
		IEssentiaTransport transporter = (IEssentiaTransport) te;
		if (transporter.getEssentiaType(null) != null) {
			NBTTagCompound essentia = new NBTTagCompound();
			essentia.setString("tag", transporter.getEssentiaType(null).getTag());
			essentia.setInteger("amount", transporter.getEssentiaAmount(null));
			tag.setTag("essentia", essentia);
		}
		NBTTagCompound suction = new NBTTagCompound();
		if (transporter.getSuctionType(null) != null) {
			suction.setString("tag", transporter.getSuctionType(null).getTag());
		}
		suction.setInteger("amount", transporter.getSuctionAmount(null));
		tag.setTag("suction", suction);
		return tag;
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound data = accessor.getNBTData();
		if (data.isEmpty() || !data.hasKey("suction")) {
			return tooltip;
		}

		NBTTagCompound essentia = data.getCompoundTag("essentia");
		String essentiaTag = essentia.getString("tag");
		if (!essentiaTag.isEmpty()) {
			tooltip.add(
				I18n.format("tc.resonator1",
					SpecialChars.WailaSplitter +essentia.getInteger("amount"),
					SpecialChars.WailaSplitter + SpecialChars.getRenderString("thaumicwaila.aspect", essentiaTag)
				)
			);
		}

		NBTTagCompound suction = data.getCompoundTag("suction");
		String suctionTag = suction.getString("tag");
		String essentiaString;
		if (!suctionTag.isEmpty()) {
			essentiaString = SpecialChars.WailaSplitter + SpecialChars.getRenderString("thaumicwaila.aspect", suctionTag);
		} else {
			essentiaString = I18n.format("tc.resonator3");
		}
		tooltip.add(
			I18n.format("tc.resonator2",
				SpecialChars.WailaSplitter + suction.getInteger("amount"),
				essentiaString
			)
		);

		return tooltip;
	}
}
