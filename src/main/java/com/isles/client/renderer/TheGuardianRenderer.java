package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheGuardianEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TheGuardianRenderer extends MobRenderer<TheGuardianEntity, TheGuardianModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(blest.MODID, "textures/entity/theguardian.png");

    public TheGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new TheGuardianModel(context.bakeLayer(TheGuardianModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TheGuardianEntity entity) {
        return TEXTURE;
    }
}
