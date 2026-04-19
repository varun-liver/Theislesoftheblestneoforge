package com.isles.client.renderer;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.isles.entity.TheGuardianEntity;
import com.isles.client.renderer.TheGuardianAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class TheGuardianModel extends HierarchicalModel<TheGuardianEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("theislesoftheblest", "theguardianmodel"), "main");
	private final ModelPart bone;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart body;
	private final ModelPart arm1;
	private final ModelPart arm2;
	private final ModelPart head;

	public TheGuardianModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.leg1 = this.bone.getChild("leg1");
		this.leg2 = this.bone.getChild("leg2");
		this.body = this.bone.getChild("body");
		this.arm1 = this.bone.getChild("arm1");
		this.arm2 = this.bone.getChild("arm2");
		this.head = this.bone.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leg1 = bone.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(30, 0).addBox(-2.5F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -12.5F, 0.5F));

		PartDefinition leg2 = bone.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(32, 18).addBox(-3.0F, -1.0F, -2.0F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -26.0F, -2.0F, 10.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arm1 = bone.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 34).addBox(3.0F, -1.5F, -2.5F, 4.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -24.5F, 0.5F));

		PartDefinition arm2 = bone.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(18, 36).addBox(-5.0F, -1.5F, -2.5F, 4.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -24.5F, 0.5F));

		PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(TheGuardianEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(TheGuardianAnimations.walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
		this.animate(entity.attackAnimationState, TheGuardianAnimations.smash, ageInTicks , 1.0F);
		this.animate(entity.attack2AnimationState, TheGuardianAnimations.punch, ageInTicks , 1.0F);
		this.animate(entity.attack3AnimationState, TheGuardianAnimations.spin, ageInTicks , 1.0F);
	}
	@Override
	public ModelPart root() {return this.bone;}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
