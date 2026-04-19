package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheCursedOnesEntity;
import com.isles.client.renderer.TheCursedOnesAnimations;
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

public class TheCursedOnesModel extends HierarchicalModel<TheCursedOnesEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(blest.MODID, "thecursedones"), "main");
	private final ModelPart bone;
	private final ModelPart body;
	private final ModelPart leg2;
	private final ModelPart leg1;
	private final ModelPart arm1;
	private final ModelPart arm2;
	private final ModelPart head;

	public TheCursedOnesModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.body = this.bone.getChild("body");
		this.leg2 = this.bone.getChild("leg2");
		this.leg1 = this.bone.getChild("leg1");
		this.arm1 = this.bone.getChild("arm1");
		this.arm2 = this.bone.getChild("arm2");
		this.head = this.bone.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -7.0F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition leg2 = bone.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(12, 8).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -4.0F, 0.0F));

		PartDefinition leg1 = bone.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(12, 14).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -4.0F, 0.0F));

		PartDefinition arm1 = bone.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -2.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -9.0F, 0.0F));

		PartDefinition arm2 = bone.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 17).addBox(2.0F, -11.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.3054F, -0.0608F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -1.0F, 0.0F, 0.0F, -0.9599F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(TheCursedOnesEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(TheCursedOnesAnimations.walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
		this.animate(entity.punchAnimationState, TheCursedOnesAnimations.punch, ageInTicks, 1.0F);
	}

	@Override
	public ModelPart root() {
		return this.bone;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
