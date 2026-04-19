package com.isles.network;

import com.isles.ArmorStandCutsceneManager;
import com.isles.blest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CutsceneOverlayDoneC2SPacket() implements CustomPacketPayload {

    public static final Type<CutsceneOverlayDoneC2SPacket> TYPE = new Type<>(new ResourceLocation(blest.MODID, "cutscene_overlay_done"));

    public static final StreamCodec<FriendlyByteBuf, CutsceneOverlayDoneC2SPacket> CODEC = StreamCodec.of(
            (buf, msg) -> {},
            (buf) -> new CutsceneOverlayDoneC2SPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer sender) {
            context.enqueueWork(() -> ArmorStandCutsceneManager.requestEnd(sender));
        }
    }
}
