package com.buuz135.smithingtemplateviewer.emi;

import com.buuz135.smithingtemplateviewer.Config;
import com.buuz135.smithingtemplateviewer.SmithingTemplateViewer;
import com.buuz135.smithingtemplateviewer.SmithingTrimWrapper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.fixes.ItemCustomNameToComponentFix;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class EMISmithingViewerRecipe implements EmiRecipe {

    public final ResourceLocation id;
    public final SmithingTrimWrapper wrapper;

    public EMISmithingViewerRecipe(ResourceLocation id, SmithingTrimWrapper wrapper) {
        this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "/" + id.getPath() + "_viewer");
        this.wrapper = wrapper;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return STVEmiPlugin.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        if(Config.CONFIG.displayMaterials.get()) {
            return List.of(EmiIngredient.of(wrapper.getRecipe().template), EmiIngredient.of(wrapper.getRecipe().base), EmiIngredient.of(wrapper.getRecipe().addition));
        }
        ItemStack itemStack = Items.BARRIER.getDefaultInstance();
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Use any item!"));
        return List.of(EmiIngredient.of(wrapper.getRecipe().template), EmiIngredient.of(wrapper.getRecipe().base), EmiIngredient.of(Ingredient.of(itemStack)));
        
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 120;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getInputs().get(0), 0,0);
        widgets.addSlot(getInputs().get(1), 0,18);
        widgets.addSlot(getInputs().get(2), 0,18*2);
        var armorStandX = 75;
        var armorStandY = 75;
        widgets.addDrawable(0, 0, 0, 0, (guiGraphics, mouseX, mouseY, delta) -> {

            Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
            Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);

            var template = wrapper.getRecipe().template.getItems()[0];
            if (template.getItem() instanceof SmithingTemplateItem templateItem){
                guiGraphics.drawString(Minecraft.getInstance().font, templateItem.upgradeDescription.copy().withStyle(ChatFormatting.DARK_GRAY), 20,5, 0xFFFFFF, false);
            }

            var inventory = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
            var buttons = ResourceLocation.fromNamespaceAndPath(SmithingTemplateViewer.MODID, "textures/gui/buttons.png");
            guiGraphics.blit(inventory, armorStandX - 25, armorStandY - 57, 25,7,51,72);


            InventoryScreen.renderEntityInInventory(
                    guiGraphics, armorStandX, armorStandY, 25.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, wrapper.getArmorStand()
            );

            guiGraphics.renderItem(wrapper.getRecipe().addition.getItems()[wrapper.getColorIndex()], armorStandX - 24 + 17, armorStandY + 20);
        });
        for (int i = 0; i < 4; i++) {
            //LEFT
            int finalI = i;
            widgets.addButton(armorStandX - 25 - 16, armorStandY - 57 + 4 + 18*i, 12, 12, 0, 0, () -> wrapper.getArmorIndex()[finalI] > 0, (mouseX, mouseY, button) -> {
                if (wrapper.getArmorIndex()[finalI] > 0){
                    wrapper.getArmorIndex()[finalI]--;
                    wrapper.updateArmorStand(Minecraft.getInstance().level);
                }
            });
            //RIGHT
            widgets.addButton(armorStandX + 25 + 4, armorStandY - 57 + 4 + 18*i, 12, 12, 12, 0, () -> wrapper.getArmorIndex()[finalI] < wrapper.getArmors().get(finalI).size(), (mouseX, mouseY, button) -> {
                if (wrapper.getArmorIndex()[finalI] < wrapper.getArmors().get(finalI).size()){
                    wrapper.getArmorIndex()[finalI]++;
                    wrapper.updateArmorStand(Minecraft.getInstance().level);
                }
            });
        }

        widgets.addButton(armorStandX - 22, armorStandY + 22, 12, 12, 0, 0, () -> wrapper.getColorIndex() > 0, (mouseX, mouseY, button) -> {
            if (wrapper.getColorIndex() > 0){
                wrapper.setColorIndex(wrapper.getColorIndex() - 1);
                wrapper.updateArmorStand(Minecraft.getInstance().level);
            }
        });
        //RIGHT
        widgets.addButton(armorStandX + 12 , armorStandY + 22, 12, 12, 12, 0, () -> wrapper.getColorIndex() < wrapper.getRecipe().addition.getItems().length - 1, (mouseX, mouseY, button) -> {
            if (wrapper.getColorIndex() < wrapper.getRecipe().addition.getItems().length -1){
                wrapper.setColorIndex(wrapper.getColorIndex() + 1);
                wrapper.updateArmorStand(Minecraft.getInstance().level);
            }
        });

    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
