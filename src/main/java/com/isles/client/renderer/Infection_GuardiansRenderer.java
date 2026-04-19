package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.Infection_GuardiansEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class Infection_GuardiansRenderer extends MobRenderer<Infection_GuardiansEntity, Infection_GuardiansModel<Infection_GuardiansEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(blest.MODID, "textures/entity/infection_guardians.png");

    public Infection_GuardiansRenderer(EntityRendererProvider.Context context) {
        super(context, new Infection_GuardiansModel<>(context.bakeLayer(Infection_GuardiansModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Infection_GuardiansEntity entity) {
        return TEXTURE;
    }
}
