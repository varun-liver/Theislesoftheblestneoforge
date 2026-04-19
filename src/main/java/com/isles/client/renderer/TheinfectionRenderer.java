package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheinfectionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
public class TheinfectionRenderer extends MobRenderer<TheinfectionEntity, TheinfectionModel> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(blest.MODID, "textures/entity/theinfection.png");
    public TheinfectionRenderer(EntityRendererProvider.Context context) {
        super(context,new TheinfectionModel(context.bakeLayer(TheinfectionModel.LAYER_LOCATION)), 0.0F);
    }
    @Override
    public ResourceLocation getTextureLocation(TheinfectionEntity entity) {return TEXTURE;}
}
