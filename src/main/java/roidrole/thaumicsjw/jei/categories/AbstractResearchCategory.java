package roidrole.thaumicsjw.jei.categories;

import mezz.jei.api.recipe.IRecipeCategory;
import roidrole.thaumicsjw.Tags;
import roidrole.thaumicsjw.ThaumicSJWConfig;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractResearchCategory<T extends IHasResearch> implements IRecipeCategory<T> {

	public static ArrayList<AbstractResearchCategory<?>> categories;

	public Collection<T> recipes;

	public abstract void populateRecipes();

	public AbstractResearchCategory() {
		if(ThaumicSJWConfig.jeiConfig.hideRecipesIfMissingResearch){
			categories.add(this);
		}
	}

	@Override
	public String getModName() {
		return Tags.MOD_NAME;
	}
}
