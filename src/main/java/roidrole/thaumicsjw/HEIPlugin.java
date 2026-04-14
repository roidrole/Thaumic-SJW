package roidrole.thaumicsjw;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import roidrole.thaumicsjw.jei.AspectListIngredient;
import roidrole.thaumicsjw.jei.ResearchManager;
import roidrole.thaumicsjw.jei.categories.*;
import roidrole.thaumicsjw.jei.gui.FocalManipulatorAdvancedGuiHandler;
import roidrole.thaumicsjw.jei.gui.ResearchTableAdvancedGuiHandler;
import roidrole.thaumicsjw.utils.ArrayMap;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.common.container.ContainerArcaneWorkbench;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
			File aspectFile = new File(ThaumicSJW.CACHE_FOLDER, "itemstack_aspects.json");

			if (!aspectFile.exists()) {
				createAspectsFile(aspectFile, registry);
			}
			parseAspectsFile(aspectFile, registry);

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

	public void createAspectsFile(File aspectFile, IModRegistry registry){
		long time = System.currentTimeMillis();
		Collection<ItemStack> items = registry.getIngredientRegistry().getAllIngredients(VanillaTypes.ITEM);
		ThaumicSJW.LOGGER.info("Caching ItemStack Aspects.");
		ThaumicSJW.LOGGER.info("Trying to cache {} aspects.", items.size());
		//Filter out blacklisted items
		Set<ResourceLocation> blacklist = new HashSet<>();
		for (String string : ThaumicSJWConfig.jeiConfig.blacklistedFromAspectChecking){
			blacklist.add(new ResourceLocation(string));
		}
		blacklist.add(null);
		blacklist.add(Items.AIR.getRegistryName());

		Map<Aspect, ArrayMap<List<String>>> cache = new ConcurrentHashMap<>();
		for(Aspect aspect : Aspect.aspects.values()){
			cache.put(aspect, new ArrayMap<>());
		}

		//Because concurrency
		final int[] cachedAmount = {0};
		final long[] lastTimeChecked = {System.currentTimeMillis()};

		items
			.parallelStream()
			.filter(stack -> !blacklist.contains(Item.REGISTRY.getNameForObject(stack.getItem())))
			//Since Thaumcraft caches ItemStack aspects itself, filtering for empty AspectList is fine
			.filter(stack -> {
				AspectList list = AspectHelper.getObjectAspects(stack);
				if (list == null || list.size() == 0){
					cachedAmount[0]++;
					return false;
				}
				return true;
			})
			.forEach(stack -> {
				AspectList list = AspectHelper.getObjectAspects(stack);
				list.aspects.forEach((aspect, count) -> cache
					.get(aspect)
					.computeIfAbsent(count, ArrayList::new)
					.add(writeItemStack(stack, count)));

				cachedAmount[0]++;
				if (lastTimeChecked[0] + 5000 < System.currentTimeMillis()) {
					lastTimeChecked[0] = System.currentTimeMillis();
					ThaumicSJW.LOGGER.info("ItemStack Aspect checking at {}%", 100 * cachedAmount[0] / items.size());
				}
			})
		;

		ThaumicSJW.LOGGER.info("ItemStack Aspect checking at 100%");
		try (JsonWriter writer = new JsonWriter(new FileWriter(aspectFile))){
			writer.setIndent("\t");
			//Write the JSON by hand. Less annoying
			writer.beginObject();
			cache
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparing(Aspect::getTag)))
				.forEach(entry -> {
					try {
						writer.name(entry.getKey().getTag());
						writer.beginArray();
						entry.getValue().forEach(
							(count, list) -> list.forEach(stack -> {
								try {
									writer.jsonValue(stack);
								} catch (IOException ignored) { }
							})
						);
						writer.endArray();
					} catch (IOException ignored) { }
				});
			writer.endObject();
		} catch (IOException e) {
			ThaumicSJW.LOGGER.error("Can't write aspect file!", e);
		}
		ThaumicSJW.LOGGER.info("Wrote aspect file in {} ms", System.currentTimeMillis() - time);
	}

	public void parseAspectsFile(File aspectFile, IModRegistry registry){
		long time = System.currentTimeMillis();
		List<AspectFromItemStackCategory.AspectFromItemStackWrapper> wrappers = new ArrayList<>();
		Map<Aspect, List<ItemStack>> cache = new LinkedHashMap<>(Aspect.aspects.size());

		try (JsonReader reader = new JsonReader(new FileReader(aspectFile))){
			reader.beginObject();

			do {
				Aspect aspect = Aspect.getAspect(reader.nextName());
				List<ItemStack> list = new ArrayList<>();
				cache.put(aspect, list);
				reader.beginArray();
				while(reader.peek() != JsonToken.END_ARRAY){
					list.add(readItemStack(reader));
				}
				reader.endArray();

			} while (reader.peek() != JsonToken.END_OBJECT);
			reader.endObject();


			cache.forEach((aspect, stacks) -> {
				AspectList aspectList = new AspectList();
				aspectList.add(aspect, 0);
				int start = 0;
				while (start < stacks.size() - 36) {
					List<ItemStack> subList = stacks.subList(start, start + 36);
					wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList));
					start += 36;
				}
				List<ItemStack> subList = stacks.subList(start, stacks.size());
				wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList));
			});
		} catch (FileNotFoundException e) {
			ThaumicSJW.LOGGER.error("Can't read aspect file!", e);
			return;
		} catch (NBTException e) {
			ThaumicSJW.LOGGER.error("Malformed aspect file. Please regenerate", e);
		} catch (IOException e) {
			ThaumicSJW.LOGGER.error("Can't read aspect file. Please regenerate", e);
		}
		ThaumicSJW.LOGGER.info("Parsed aspect file in {} ms", System.currentTimeMillis() - time);

		registry.addRecipes(wrappers, AspectFromItemStackCategory.UUID);
	}

	//Writes ItemStack to format: [resourceLocation, count, damage, tag]
	public static String writeItemStack(ItemStack stack, int count){
		StringBuilder itemNbt = new StringBuilder(64);
		itemNbt.append("[\"");
		itemNbt.append(Item.REGISTRY.getNameForObject(stack.getItem()));
		itemNbt.append("\",");
		itemNbt.append(count);
		if(stack.getItemDamage() != 0){
			itemNbt.append(',');
			itemNbt.append(stack.getItemDamage());
		}
		if (stack.getTagCompound() != null){
			itemNbt.append(',');
			itemNbt.append(NBTTagString.quoteAndEscape(stack.getTagCompound().toString()));
		}
		itemNbt.append(']');
		return itemNbt.toString();
	}

	public static ItemStack readItemStack(JsonReader reader) throws IOException, NBTException {
		reader.setLenient(true);
		reader.beginArray();
		ItemStack stack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(reader.nextString())), reader.nextInt());
		if(reader.peek() == JsonToken.NUMBER){
			stack.setItemDamage(reader.nextInt());
		}
		if(reader.peek() == JsonToken.STRING){
			stack.setTagCompound(JsonToNBT.getTagFromJson(reader.nextString()));
		}
		reader.endArray();
		return stack;
	}
}