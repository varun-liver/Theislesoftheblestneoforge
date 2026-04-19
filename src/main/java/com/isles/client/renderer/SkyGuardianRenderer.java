package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.SkyGuardianEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SkyGuardianRenderer extends MobRenderer<SkyGuardianEntity, SkyGuardianModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(blest.MODID, "textures/entity/sky_guardian.png");

    public SkyGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new SkyGuardianModel(context.bakeLayer(SkyGuardianModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SkyGuardianEntity entity) {
        return TEXTURE;
    }
}
