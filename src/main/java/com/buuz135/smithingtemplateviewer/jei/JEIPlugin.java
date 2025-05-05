package com.buuz135.smithingtemplateviewer.jei;

import com.buuz135.smithingtemplateviewer.SmithingTemplateViewer;
import com.buuz135.smithingtemplateviewer.SmithingTrimWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static RecipeType<SmithingTrimWrapper> RECIPE_TYPE = RecipeType.create(SmithingTemplateViewer.MODID, "smithing_trim", SmithingTrimWrapper.class);
    public static JEISmithingViewerCategory SMITHING_VIEWER_CATEGORY = new JEISmithingViewerCategory();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SmithingTemplateViewer.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
        registration.addRecipeCategories(SMITHING_VIEWER_CATEGORY);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        SmithingTrimWrapper.INSTANCES.clear();
        IModPlugin.super.registerRecipes(registration);
        var recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMITHING).stream().map(smithingRecipeRecipeHolder -> smithingRecipeRecipeHolder.value())
                .filter(smithingRecipe -> smithingRecipe instanceof SmithingTrimRecipe).map(smithingRecipe -> new SmithingTrimWrapper((SmithingTrimRecipe) smithingRecipe)).toList();
        registration.addRecipes(RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(Items.SMITHING_TABLE, RECIPE_TYPE);
    }
}
