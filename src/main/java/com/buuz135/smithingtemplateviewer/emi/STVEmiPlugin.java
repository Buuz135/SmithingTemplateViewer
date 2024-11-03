package com.buuz135.smithingtemplateviewer.emi;

import com.buuz135.smithingtemplateviewer.SmithingTrimWrapper;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

@EmiEntrypoint
public class STVEmiPlugin implements EmiPlugin {

    public static final EMISmithingViewerCategory CATEGORY = new EMISmithingViewerCategory();

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        registry.addWorkstation(CATEGORY, EmiIngredient.of(Ingredient.of(Items.SMITHING_TABLE)));
        for (RecipeHolder<?> recipe : registry.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {
            if (recipe.value() instanceof SmithingTrimRecipe trimRecipe){
                registry.addRecipe(new EMISmithingViewerRecipe(recipe.id(), new SmithingTrimWrapper(trimRecipe)));
            }
        }
    }
}
