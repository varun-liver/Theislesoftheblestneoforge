package com.isles.network;

import com.isles.blest;
import com.isles.client.cutscene.CutsceneClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CutsceneEndS2CPacket() implements CustomPacketPayload {

    public static final Type<CutsceneEndS2CPacket> TYPE = new Type<>(new ResourceLocation(blest.MODID, "cutscene_end"));

    public static final StreamCodec<FriendlyByteBuf, CutsceneEndS2CPacket> CODEC = StreamCodec.of(
            (buf, msg) -> {},
            (buf) -> new CutsceneEndS2CPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> CutsceneClientState.beginEnd(20));
    }
}
