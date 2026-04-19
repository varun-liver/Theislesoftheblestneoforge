package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.SkyGuardianEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class SkyGuardianModel extends HierarchicalModel<SkyGuardianEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(blest.MODID, "sky_guardian"), "main");
    private final ModelPart root;
    private final ModelPart bone;

    public SkyGuardianModel(ModelPart root) {
        this.root = root;
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition bone = partDefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, 21.0F, 0.0F)
        );

        bone.addOrReplaceChild(
                "rarm",
                CubeListBuilder.create().texOffs(6, 12)
                        .addBox(-8.0F, -2.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        bone.addOrReplaceChild(
                "larm",
                CubeListBuilder.create().texOffs(0, 12)
                        .addBox(-1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 0.0F)
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void setupAnim(SkyGuardianEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.attackAnimationState, SkyGuardianAnimations.ATTACK, ageInTicks, 1.0F);
        this.animate(entity.attack2AnimationState, SkyGuardianAnimations.ATTACK_2, ageInTicks, 1.0F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
