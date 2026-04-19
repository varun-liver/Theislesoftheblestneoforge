package com.isles.client.renderer;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.isles.blest;
import com.isles.client.renderer.ThewhispererAnimations;
import com.isles.entity.ThewhispererEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ThewhispererModel extends HierarchicalModel<ThewhispererEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(blest.MODID, "thewhisperer"), "main");
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart body;
	private final ModelPart arm1;
	private final ModelPart arm2;

	public ThewhispererModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.body = this.bone.getChild("body");
		this.arm1 = this.bone.getChild("arm1");
		this.arm2 = this.bone.getChild("arm2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arm1 = bone.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(4.5F, -6.0F, 0.0F));

		PartDefinition cube_r1 = arm1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, -2.0F, -5.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 1.0F, 5.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition arm2 = bone.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 0.0F));

		PartDefinition cube_r2 = arm2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(6, 16).addBox(-6.0F, -2.0F, 4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, 5.0F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(ThewhispererEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(ThewhispererAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
	}
	@Override
	public ModelPart root() {return this.root;}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
