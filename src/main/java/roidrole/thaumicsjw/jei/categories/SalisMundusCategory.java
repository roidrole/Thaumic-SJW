package roidrole.thaumicsjw.jei.categories;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.gui.elements.DrawableBlank;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import roidrole.thaumicsjw.HEIPlugin;
import roidrole.thaumicsjw.Tags;
import roidrole.thaumicsjw.mixins.accessors.AccessorDustTriggerMultiblock;
import roidrole.thaumicsjw.mixins.accessors.AccessorDustTriggerOre;
import roidrole.thaumicsjw.mixins.accessors.AccessorDustTriggerSimple;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SalisMundusCategory extends AbstractResearchCategory<SalisMundusCategory.SalisMundusRecipeWrapper> {

	public static final String UUID = Tags.MOD_ID + ".salis_mundus";
	public static final String title = new ItemStack(ItemsTC.salisMundus).getDisplayName();
	public final IDrawable slot;
	public final IJeiHelpers helpers;

	//Shared ItemStack for all wrappers
	public static final List<ItemStack> salisMundus = Collections.singletonList(new ItemStack(ItemsTC.salisMundus));

	public SalisMundusCategory(IJeiHelpers helper){
		super();
		this.slot = helper.getGuiHelper().getSlotDrawable();
		this.helpers = helper;
	}

	@Override
	public String getUid() {
		return UUID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void populateRecipes() {
		this.recipes = new ArrayList<>();
		for (IDustTrigger trigger : IDustTrigger.triggers) {
			SalisMundusRecipeWrapper wrapper = new SalisMundusRecipeWrapper(trigger);
			if(wrapper.isValid()) {
				this.recipes.add(wrapper);
			}
		}
	}

	@Override
	public String getModName() {
		return Tags.MOD_NAME;
	}

	@Override
	public IDrawable getBackground() {
		return new DrawableBlank(103, 36);
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		slot.draw(minecraft, 2, 9);
		slot.draw(minecraft, 30, 9);
		slot.draw(minecraft, 58, 9);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, SalisMundusRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiIngredientGroup<ItemStack> group = layout.getIngredientsGroup(VanillaTypes.ITEM);
		//Salis Mundus
		group.init(0, true,  2, 9);

		//Block to click
		group.init(1, true,  30, 9);

		//Output
		group.init(2, false,  58, 9);

		group.set(ingredients);
	}

	//TODO: remove invalid recipes
	public static class SalisMundusRecipeWrapper implements IHasResearch {

		List<List<ItemStack>> input;
		List<List<ItemStack>> output;
		String research;
		public SalisMundusRecipeWrapper(IDustTrigger trigger){
			if(trigger instanceof DustTriggerOre){
				AccessorDustTriggerOre oreTrigger = (AccessorDustTriggerOre) trigger;
				this.input = Arrays.asList(salisMundus, OreDictionary.getOres(oreTrigger.getTarget()));
				this.output = HEIPlugin.nestedSingletonList(oreTrigger.getResult());
				this.research = oreTrigger.getResearch();
			} else if(trigger instanceof DustTriggerSimple){
				AccessorDustTriggerSimple simpleTrigger = (AccessorDustTriggerSimple) trigger;
				Block target = simpleTrigger.getTarget();
				List<ItemStack> inputs;
				if(target == Blocks.CAULDRON){
					inputs = Collections.singletonList(new ItemStack(Items.CAULDRON));
				} else {
					inputs = NonNullList.create();
					target.getSubBlocks(target.getCreativeTab(), (NonNullList<ItemStack>) inputs);
				}
				this.input = Arrays.asList(salisMundus, inputs);
				this.output = HEIPlugin.nestedSingletonList(simpleTrigger.getResult());
				this.research = simpleTrigger.getResearch();
			} else if(trigger instanceof DustTriggerMultiblock){
				AccessorDustTriggerMultiblock multiblockTrigger = (AccessorDustTriggerMultiblock) trigger;
				//TODO
				this.research = multiblockTrigger.getResearch();
				this.input = Arrays.asList(salisMundus, Collections.emptyList());
				this.output = HEIPlugin.nestedSingletonList(ItemStack.EMPTY);
			}
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM, this.input);
			ingredients.setOutputLists(VanillaTypes.ITEM, this.output);
		}

		@Override
		public String getResearch() {
			return this.research;
		}

		@Override
		public int getBarrierX() {
			return 0;
		}

		@Override
		public int getBarrierY() {
			return 0;
		}

		public boolean isValid(){
			return !this.input.get(1).isEmpty();
		}
	}
}
