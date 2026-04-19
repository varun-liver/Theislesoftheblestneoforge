package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.ThewhispererEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
public class ThewhispererRenderer extends MobRenderer<ThewhispererEntity, ThewhispererModel> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(blest.MODID, "textures/entity/thewhisperer.png");
    public ThewhispererRenderer(EntityRendererProvider.Context context) {
        super(context,new ThewhispererModel(context.bakeLayer(ThewhispererModel.LAYER_LOCATION)), 0.0F);
    }
    @Override
    public ResourceLocation getTextureLocation(ThewhispererEntity entity) {return TEXTURE;}
}
