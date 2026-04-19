// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheinfectionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class TheinfectionModel extends HierarchicalModel<TheinfectionEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION =                         new ModelLayerLocation(new ResourceLocation(blest.MODID, "theinfection"), "main");
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart leg2;
	private final ModelPart leg1;
	private final ModelPart body;
	private final ModelPart arm2;
	private final ModelPart arm21;
	private final ModelPart armt;
	private final ModelPart armt1;
	private final ModelPart armt2;
	private final ModelPart armt3;
	private final ModelPart arm1;
	private final ModelPart arm11;
	private final ModelPart armt7;
	private final ModelPart armt5;
	private final ModelPart armt6;
	private final ModelPart armt4;
	private final ModelPart head;
	private final ModelPart horn1;
	private final ModelPart horn2;

	public TheinfectionModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.leg2 = this.bone.getChild("leg2");
		this.leg1 = this.bone.getChild("leg1");
		this.body = this.bone.getChild("body");
		this.arm2 = this.bone.getChild("arm2");
		this.arm21 = this.arm2.getChild("arm21");
		this.armt = this.arm21.getChild("armt");
		this.armt1 = this.armt.getChild("armt1");
		this.armt2 = this.armt.getChild("armt2");
		this.armt3 = this.armt.getChild("armt3");
		this.arm1 = this.bone.getChild("arm1");
		this.arm11 = this.arm1.getChild("arm11");
		this.armt7 = this.arm11.getChild("armt7");
		this.armt5 = this.armt7.getChild("armt5");
		this.armt6 = this.armt7.getChild("armt6");
		this.armt4 = this.armt7.getChild("armt4");
		this.head = this.bone.getChild("head");
		this.horn1 = this.head.getChild("horn1");
		this.horn2 = this.head.getChild("horn2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leg2 = bone.addOrReplaceChild("leg2", CubeListBuilder.create(), PartPose.offset(-12.0F, -25.0F, 0.0F));

		PartDefinition cube_r1 = leg2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-5.0F, 3.0F, -6.0F, 10.0F, 21.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition leg1 = bone.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offset(16.0F, -26.0F, 0.0F));

		PartDefinition cube_r2 = leg1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 64).addBox(-9.0F, -21.0F, -1.0F, 10.0F, 21.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 26.0F, -4.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -46.0F, -7.0F, 29.0F, 25.0F, 13.0F, new CubeDeformation(1.0F))
		.texOffs(0, 95).addBox(7.0F, -43.0F, -10.0F, 14.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 0.0F, 0.0F));

		PartDefinition arm2 = bone.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(17.6667F, -42.5643F, 0.3717F));

		PartDefinition cube_r3 = arm2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(80, 88).addBox(-1.0F, -2.0F, -7.0F, 10.0F, 20.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6667F, -0.4357F, -0.3717F, 0.4363F, 0.0F, 0.0F));

		PartDefinition arm21 = arm2.addOrReplaceChild("arm21", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -4.0F, -2.0F, 10.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(2.3333F, 14.5643F, 2.6283F));

		PartDefinition armt = arm21.addOrReplaceChild("armt", CubeListBuilder.create(), PartPose.offset(-34.0F, 28.0F, -3.0F));

		PartDefinition armt1 = armt.addOrReplaceChild("armt1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r4 = armt1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(84, 29).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(35.0F, -29.0F, 20.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition armt2 = armt.addOrReplaceChild("armt2", CubeListBuilder.create(), PartPose.offsetAndRotation(38.0F, -26.3425F, 21.7357F, -0.5396F, -0.036F, -0.655F));

		PartDefinition cube_r5 = armt2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 95).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.3425F, -1.7357F, 0.3927F, 0.0F, 0.0F));

		PartDefinition armt3 = armt.addOrReplaceChild("armt3", CubeListBuilder.create(), PartPose.offsetAndRotation(21.0F, -26.3425F, 21.7357F, 0.0F, 0.0F, 1.3963F));

		PartDefinition cube_r6 = armt3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(50, 95).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0955F, -9.4308F, -0.5648F, -0.2537F, -0.0368F, -0.4349F));

		PartDefinition arm1 = bone.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(-19.0F, -41.3543F, 1.8004F));

		PartDefinition cube_r7 = arm1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(84, 0).addBox(-1.0F, -2.0F, -7.0F, 10.0F, 20.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.6535F, -2.5576F, 0.4363F, 0.0F, 0.0F));

		PartDefinition arm11 = arm1.addOrReplaceChild("arm11", CubeListBuilder.create().texOffs(56, 38).addBox(-5.0F, -3.6535F, -0.5576F, 10.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.0F, -1.0F));

		PartDefinition armt7 = arm11.addOrReplaceChild("armt7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.7609F, 17.3763F));

		PartDefinition armt5 = armt7.addOrReplaceChild("armt5", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 1.2431F, 2.8018F, -0.3491F, 0.0F, -0.7418F));

		PartDefinition cube_r8 = armt5.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(32, 103).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.3425F, -1.7357F, 0.1745F, 0.0F, 0.0F));

		PartDefinition armt6 = armt7.addOrReplaceChild("armt6", CubeListBuilder.create(), PartPose.offset(-76.0F, 27.5856F, -18.9339F));

		PartDefinition cube_r9 = armt6.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(50, 103).addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(73.2856F, -25.7249F, 22.8014F, -0.3553F, -0.2449F, 0.7114F));

		PartDefinition armt4 = armt7.addOrReplaceChild("armt4", CubeListBuilder.create(), PartPose.offset(-115.0F, 27.5856F, -18.9339F));

		PartDefinition cube_r10 = armt4.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(102, 29).addBox(-3.0F, -2.0F, -1.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(117.0F, -29.0F, 20.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create().texOffs(80, 64).addBox(-6.0F, -9.0F, 7.0F, 12.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -45.0F, 0.0F));

		PartDefinition horn1 = head.addOrReplaceChild("horn1", CubeListBuilder.create().texOffs(68, 95).addBox(-13.0F, -8.0F, 13.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(1.0F))
		.texOffs(32, 111).addBox(-9.0F, -3.0F, 13.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition horn2 = head.addOrReplaceChild("horn2", CubeListBuilder.create().texOffs(68, 105).addBox(-5.0F, -8.0F, 13.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(1.0F))
		.texOffs(40, 111).addBox(-9.0F, -3.0F, 13.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offset(16.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(TheinfectionEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(TheinfectionAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(entity.attackAnimationState, TheinfectionAnimations.smash, ageInTicks, 1.0F);
		this.animate(entity.attack2AnimationState, TheinfectionAnimations.blast, ageInTicks, 1.0F);
		this.animate(entity.attack3AnimationState, TheinfectionAnimations.summon, ageInTicks, 1.0F);
	}

	@Override
	public ModelPart root(){
		return this.root;
	}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}
