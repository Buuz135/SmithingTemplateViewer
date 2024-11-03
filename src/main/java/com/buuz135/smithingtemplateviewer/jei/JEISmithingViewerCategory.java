package com.buuz135.smithingtemplateviewer.jei;


import com.buuz135.smithingtemplateviewer.SmithingTemplateViewer;
import com.buuz135.smithingtemplateviewer.SmithingTrimWrapper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.neoforge.common.util.TriPredicate;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JEISmithingViewerCategory implements IRecipeCategory<SmithingTrimWrapper> {
    @Override
    public RecipeType<SmithingTrimWrapper> getRecipeType() {
        return JEIPlugin.RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("smithingviewer.category.name");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 120;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SmithingTrimWrapper recipe, IFocusGroup focuses) {
        builder.addInputSlot(1,1).addIngredients(recipe.getRecipe().template);
        builder.addInputSlot(1,1+18).addIngredients(recipe.getRecipe().base);
        builder.addInputSlot(1,1+18*2).addIngredients(recipe.getRecipe().addition);
    }

    @Override
    public void draw(SmithingTrimWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var armorStandX = 75;
        var armorStandY = 75;
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
        Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);

        var template = recipe.getRecipe().template.getItems()[0];
        if (template.getItem() instanceof SmithingTemplateItem templateItem){
            guiGraphics.drawString(Minecraft.getInstance().font, templateItem.upgradeDescription.copy().withStyle(ChatFormatting.DARK_GRAY), 20,5, 0xFFFFFF, false);
        }


        var inventory = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
        var buttons = ResourceLocation.fromNamespaceAndPath(SmithingTemplateViewer.MODID, "textures/gui/buttons.png");
        guiGraphics.blit(inventory, armorStandX - 25, armorStandY - 57, 25,7,51,72);
        //SLOTS
        guiGraphics.blit(inventory, 0,0, 7,83,18,18);
        guiGraphics.blit(inventory, 0,18, 7,83,18,18);
        guiGraphics.blit(inventory, 0,18*2, 7,83,18,18);

        for (int i = 0; i < 4; i++) {
            //LEFT
            guiGraphics.blit(buttons, armorStandX - 25 - 20, armorStandY - 57 + 2 + 18*i, 17,56,15,15);
            //RIGHT
            guiGraphics.blit(buttons, armorStandX + 25 + 6, armorStandY - 57 + 2 + 18*i, 33,56,15,15);
        }
        //LEFT
        guiGraphics.blit(buttons, armorStandX - 24, armorStandY + 20, 17,56,15,15);
        //RIGHT
        guiGraphics.blit(buttons, armorStandX + 25 - 14, armorStandY + 20, 33,56,15,15);

        InventoryScreen.renderEntityInInventory(
                guiGraphics, armorStandX, armorStandY, 25.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, recipe.getArmorStand()
        );

        guiGraphics.renderItem(recipe.getRecipe().addition.getItems()[recipe.getColorIndex()], armorStandX - 24 + 17, armorStandY + 20);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, SmithingTrimWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SmithingTrimWrapper recipe, IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        var armorStandX = 75;
        var armorStandY = 75;
        for (int i = 0; i < 4; i++) {
            System.out.println(i);
            //LEFT
            int finalI = i;
            builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle(armorStandX - 25 - 20, armorStandY - 57 + 2 + 18*i, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
                if (recipe.getArmorIndex()[finalI] > 0) {
                    --recipe.getArmorIndex()[finalI];
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    recipe.updateArmorStand();
                    return true;
                }
                return false;
            }));
            //RIGHT
            builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle( armorStandX + 25 + 6, armorStandY - 57 + 2 + 18*i, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
                if (recipe.getArmorIndex()[finalI] < recipe.getArmors().get(finalI).size()) {
                    ++recipe.getArmorIndex()[finalI];
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    recipe.updateArmorStand();
                    return true;
                }
                return false;
            }));
        }

        builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle(armorStandX - 24, armorStandY + 20, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.getColorIndex() > 0) {
                recipe.setColorIndex(recipe.getColorIndex() - 1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                recipe.updateArmorStand();
                return true;
            }
            return false;
        }));
        //RIGHT
        builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle( armorStandX + 25 - 14, armorStandY + 20, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.getColorIndex() < recipe.getRecipe().addition.getItems().length - 1) {
                recipe.setColorIndex(recipe.getColorIndex() + 1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                recipe.updateArmorStand();
                return true;
            }
            return false;
        }));
    }

    public static record ClickHandler<T>(ScreenRectangle area, T recipe,
                                         TriPredicate<Double, Double, IJeiUserInput> handleInput) implements IJeiInputHandler {

        public ScreenRectangle getArea() {
            return this.area;
        }

        public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
            if (!input.isSimulate())return this.handleInput.test(mouseX, mouseY, input);
            return false;
        }

        public ScreenRectangle area() {
            return this.area;
        }

        public T recipe() {
            return this.recipe;
        }

    }
}
