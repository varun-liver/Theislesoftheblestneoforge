package com.isles;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SKY_CATALYST = blest.ARMOR_MATERIALS.register("sky_catalyst", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.HELMET, 3);
            }),
            18,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(blest.sky_catalyst.get()),
            List.of(new ArmorMaterial.Layer(new ResourceLocation(blest.MODID, "sky_catalyst"))),
            1.0F,
            0.0F
    ));
}
