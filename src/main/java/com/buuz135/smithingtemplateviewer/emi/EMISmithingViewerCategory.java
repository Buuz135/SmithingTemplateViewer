package com.buuz135.smithingtemplateviewer.emi;

import com.buuz135.smithingtemplateviewer.SmithingTemplateViewer;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class EMISmithingViewerCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SmithingTemplateViewer.MODID, "trim_viewer");

    public EMISmithingViewerCategory() {
        super(ID, EmiStack.of(Items.SMITHING_TABLE));
    }

    @Override
    public Component getName() {
        return Component.translatable("smithingviewer.category.name");
    }
}
