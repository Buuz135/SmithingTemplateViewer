package com.buuz135.smithingtemplateviewer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SmithingTrimWrapper {

    public static final List<SmithingTrimWrapper> INSTANCES = new ArrayList<>();

    private SmithingTrimRecipe recipe;
    private ArmorStand armorStand;
    private int[] armorIndex;
    private int colorIndex;
    private List<List<ItemStack>> armors;

    public SmithingTrimWrapper(SmithingTrimRecipe recipe) {
        this.recipe = recipe;
        this.armorIndex = new int[]{1,1,1,1};
        this.colorIndex = 0;
        this.armors = new ArrayList<>();
        this.armors.add(new ArrayList<>());
        this.armors.add(new ArrayList<>());
        this.armors.add(new ArrayList<>());
        this.armors.add(new ArrayList<>());
        for (ItemStack item : this.recipe.base.getItems()) {
            if (item.getItem() instanceof ArmorItem armorItem) {
                this.armors.get(3-armorItem.getType().getSlot().getIndex()).add(item.copy());
            }
        }
        recreateArmorStand(Minecraft.getInstance().level);
        INSTANCES.add(this);
    }

    public void recreateArmorStand(Level level) {
        this.armorStand = new ArmorStand(level, 0.0, 0.0, 0.0);
        this.armorStand.setNoBasePlate(true);
        this.armorStand.setShowArms(true);
        this.armorStand.yBodyRot = 210.0F;
        this.armorStand.setXRot(25.0F);
        this.armorStand.yHeadRot = this.armorStand.getYRot();
        this.armorStand.yHeadRotO = this.armorStand.getYRot();
        this.updateArmorStand();}

    public SmithingTrimRecipe getRecipe() {
        return recipe;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public int[] getArmorIndex() {
        return armorIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public List<List<ItemStack>> getArmors() {
        return armors;
    }

    public void updateArmorStand() {
        var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < 4; i++) {
            var index = this.armorIndex[i];
            if (index != 0){
                var stack = this.getArmors().get(i).get(index - 1);
                stack = recipe.assemble(new SmithingRecipeInput(recipe.template.getItems()[0].copy(), stack.copy(), recipe.addition.getItems()[getColorIndex()]), Minecraft.getInstance().level.registryAccess());
                this.armorStand.setItemSlot(slots[i], stack.copy());
            } else {
                this.armorStand.setItemSlot(slots[i], ItemStack.EMPTY);
            }

        }
    }
}
