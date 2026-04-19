package roidrole.thaumicsjw;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import roidrole.thaumicsjw.jei.AspectListIngredient;
import roidrole.thaumicsjw.jei.ResearchManager;
import roidrole.thaumicsjw.jei.categories.*;
import roidrole.thaumicsjw.jei.gui.FocalManipulatorAdvancedGuiHandler;
import roidrole.thaumicsjw.jei.gui.ResearchTableAdvancedGuiHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.common.container.ContainerArcaneWorkbench;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@JEIPlugin
public class HEIPlugin implements IModPlugin {

	public static final IIngredientType<AspectList> ASPECT_LIST = () -> AspectList.class;

	@Override
	public void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(Item.getByNameOrId("thaumcraft:crystal_essence"));
		subtypeRegistry.useNbtForSubtypes(Item.getByNameOrId("thaumcraft:phial"));
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		List<AspectList> aspects = Aspect.aspects.values()
			.stream()
			.map(aspect -> new AspectList().add(aspect, 1))
			.collect(Collectors.toList());

		registry.register(
			ASPECT_LIST,
			aspects,
			new AspectListIngredient.Helper(),
			new AspectListIngredient.Renderer()
		);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		AbstractResearchCategory.categories = new ArrayList<>(4);
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.arcaneWorkbench){
			registry.addRecipeCategories(new ArcaneWorkbenchCategory());
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.crucible){
			registry.addRecipeCategories(new CrucibleCategory());
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.infusion){
			registry.addRecipeCategories(new InfusionCategory());
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.aspectFromItemStack){
			registry.addRecipeCategories(new AspectFromItemStackCategory());
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.aspectCompound){
			registry.addRecipeCategories(new AspectCompoundCategory(registry.getJeiHelpers().getGuiHelper()));
		}
		AbstractResearchCategory.categories.trimToSize();
	}

	@Override
	public void register(@Nonnull IModRegistry registry){
		CacheManager.jeiRegistry = registry;
		//Since they are added to the list during the constructor, these already encompass their config option
		for(AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
			category.populateRecipes();
			registry.addRecipes(category.recipes, category.getUid());
		}
		//We still have to set non-recipes separately
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.arcaneWorkbench){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.arcaneWorkbench), ArcaneWorkbenchCategory.UUID);
			registry.addRecipeClickArea(GuiArcaneWorkbench.class, 108, 56, 32, 32, ArcaneWorkbenchCategory.UUID);
			registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerArcaneWorkbench.class, ArcaneWorkbenchCategory.UUID, 1, 9, 16, 36);
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.crucible){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.crucible), CrucibleCategory.UUID);
		}
		if(ThaumicSJWConfig.jeiConfig.categoryToggle.infusion){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.infusionMatrix), InfusionCategory.UUID);
		}

		if(ThaumicSJWConfig.jeiConfig.categoryToggle.aspectFromItemStack){
			CacheManager.parseJeiCache(registry);

			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterBasic), AspectFromItemStackCategory.UUID);
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterThaumium), AspectFromItemStackCategory.UUID);
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterVoid), AspectFromItemStackCategory.UUID);
		}

		if(ThaumicSJWConfig.jeiConfig.categoryToggle.aspectCompound){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.centrifuge), AspectCompoundCategory.UUID);

			List<AspectCompoundCategory.AspectCompoundWrapper> compoundWrappers = new ArrayList<>();
			for (Aspect aspect : Aspect.getCompoundAspects()) {
				compoundWrappers.add(new AspectCompoundCategory.AspectCompoundWrapper(aspect));
			}
			registry.addRecipes(compoundWrappers, AspectCompoundCategory.UUID);
		}

		if(ThaumicSJWConfig.jeiConfig.showSpecialRecipes){
			registry.addRecipes(
				Arrays.asList(
					new ShapelessRecipeWrapper<>(registry.getJeiHelpers(), (ShapelessOreRecipe) ThaumcraftApi.getCraftingRecipesFake().get(new ResourceLocation("thaumcraft:triplemeattreatfake"))),
					new ShapelessRecipeWrapper<>(registry.getJeiHelpers(), (ShapelessOreRecipe) ThaumcraftApi.getCraftingRecipesFake().get(new ResourceLocation("thaumcraft:salismundusfake")))
				),
				VanillaRecipeCategoryUid.CRAFTING
			);
		}


		registry.addAdvancedGuiHandlers(new ResearchTableAdvancedGuiHandler());
		registry.addAdvancedGuiHandlers(new FocalManipulatorAdvancedGuiHandler());
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		if(!ThaumicSJWConfig.jeiConfig.hideRecipesIfMissingResearch){
			return;
		}

		ResearchManager.runtime = jeiRuntime;
	}

}