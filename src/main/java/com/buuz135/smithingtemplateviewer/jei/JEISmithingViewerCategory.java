package com.buuz135.smithingtemplateviewer.jei;


import com.buuz135.smithingtemplateviewer.Config;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.neoforge.common.util.TriPredicate;
import org.jetbrains.annotations.Nullable;

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
        if(Config.CONFIG.displayMaterials.get()) {
            builder.addInputSlot(1, 1 + 18 * 2).addIngredients(recipe.getRecipe().addition);
        } else {
            ItemStack itemStack = Items.BARRIER.getDefaultInstance();
            itemStack.set(DataComponents.ITEM_NAME, Component.literal("Use any item!"));
            builder.addInputSlot(1, 1 + 18 * 2).addItemStack(itemStack);
        }
    }

    @Override
    public void draw(SmithingTrimWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        var armorStandX = 75;
        var armorStandY = 75;
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        var template = recipe.getTemplate();
        if (template.getItem() instanceof SmithingTemplateItem templateItem){
            guiGraphics.text(Minecraft.getInstance().font, Component.translatable("item.minecraft.smithing_template.upgrade").withStyle(ChatFormatting.DARK_GRAY), 20,5, 0xFFFFFF, false);
        }

        var inventory = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");
        var buttons = Identifier.fromNamespaceAndPath(SmithingTemplateViewer.MODID, "textures/gui/buttons.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, inventory, armorStandX - 25, armorStandY - 57, 25,7,51,72, 256, 256);
        //SLOTS
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, inventory, 0,0, 7,83,18,18, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, inventory, 0,18, 7,83,18,18, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, inventory, 0,18*2, 7,83,18,18, 256, 256);

        for (int i = 0; i < 4; i++) {
            //LEFT
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttons, armorStandX - 25 - 20, armorStandY - 57 + 2 + 18*i, 17,56,15,15, 256, 256);
            //RIGHT
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttons, armorStandX + 25 + 6, armorStandY - 57 + 2 + 18*i, 33,56,15,15, 256, 256);
        }
        //LEFT
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttons, armorStandX - 24, armorStandY + 20, 17,56,15,15, 256, 256);
        //RIGHT
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttons, armorStandX + 25 - 14, armorStandY + 20, 33,56,15,15, 256, 256);

        var entityBounds = new ScreenRectangle(armorStandX - 25, armorStandY - 57, 50, 72)
                .transformAxisAligned(guiGraphics.pose());
        InventoryScreen.renderEntityInInventoryFollowsAngle(
                guiGraphics, entityBounds.left(), entityBounds.top(), entityBounds.right(), entityBounds.bottom(),
                25, 0, 0, 0, recipe.getArmorStand()
        );

        guiGraphics.item(recipe.getAdditionItems().get(recipe.getColorIndex()), armorStandX - 24 + 17, armorStandY + 20);
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
            //LEFT
            int finalI = i;
            builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle(armorStandX - 25 - 20, armorStandY - 57 + 2 + 18*i, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
                if (recipe.getArmorIndex()[finalI] > 0) {
                    --recipe.getArmorIndex()[finalI];
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    recipe.updateArmorStand(Minecraft.getInstance().level);
                    return true;
                }
                return false;
            }));
            //RIGHT
            builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle( armorStandX + 25 + 6, armorStandY - 57 + 2 + 18*i, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
                if (recipe.getArmorIndex()[finalI] < recipe.getArmors().get(finalI).size()) {
                    ++recipe.getArmorIndex()[finalI];
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    recipe.updateArmorStand(Minecraft.getInstance().level);
                    return true;
                }
                return false;
            }));
        }

        builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle(armorStandX - 24, armorStandY + 20, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.getColorIndex() > 0) {
                recipe.setColorIndex(recipe.getColorIndex() - 1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                recipe.updateArmorStand(Minecraft.getInstance().level);
                return true;
            }
            return false;
        }));
        //RIGHT
        builder.addInputHandler(new ClickHandler<SmithingTrimWrapper>(new ScreenRectangle( armorStandX + 25 - 14, armorStandY + 20, 15,15), recipe, (mouseX, mouseY, iJeiUserInput) -> {
            if (recipe.getColorIndex() < recipe.getAdditionItems().size() - 1) {
                recipe.setColorIndex(recipe.getColorIndex() + 1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                recipe.updateArmorStand(Minecraft.getInstance().level);
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
            if (input.isSimulate()) return true;
            return this.handleInput.test(mouseX, mouseY, input);
        }

        public ScreenRectangle area() {
            return this.area;
        }

        public T recipe() {
            return this.recipe;
        }

    }
}
