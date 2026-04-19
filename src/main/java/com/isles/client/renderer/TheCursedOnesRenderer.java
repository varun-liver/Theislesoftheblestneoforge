package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheCursedOnesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TheCursedOnesRenderer extends MobRenderer<TheCursedOnesEntity, TheCursedOnesModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(blest.MODID, "textures/entity/thecursedones.png");

    public TheCursedOnesRenderer(EntityRendererProvider.Context context) {
        super(context, new TheCursedOnesModel(context.bakeLayer(TheCursedOnesModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(TheCursedOnesEntity entity) {
        return TEXTURE;
    }
}
