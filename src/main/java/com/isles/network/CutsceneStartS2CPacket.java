package com.isles.network;

import com.isles.blest;
import com.isles.client.cutscene.CutsceneClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CutsceneStartS2CPacket(
    int totalTicks,
    int card1Ticks,
    int gap1Ticks,
    int card2Ticks,
    int gap2Ticks,
    int card3Ticks,
    int gap3Ticks,
    int card4Ticks,
    int gap4Ticks,
    int scrollTicks,
    String card1Title,
    String card1Subtitle,
    String card2Title,
    String card2Subtitle,
    String card3Title,
    String card3Subtitle,
    String card4Title,
    String card4Subtitle,
    String scrollText
) implements CustomPacketPayload {

    public static final Type<CutsceneStartS2CPacket> TYPE = new Type<>(new ResourceLocation(blest.MODID, "cutscene_start"));

    public static final StreamCodec<FriendlyByteBuf, CutsceneStartS2CPacket> CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeVarInt(msg.totalTicks);
                buf.writeVarInt(msg.card1Ticks);
                buf.writeVarInt(msg.gap1Ticks);
                buf.writeVarInt(msg.card2Ticks);
                buf.writeVarInt(msg.gap2Ticks);
                buf.writeVarInt(msg.card3Ticks);
                buf.writeVarInt(msg.gap3Ticks);
                buf.writeVarInt(msg.card4Ticks);
                buf.writeVarInt(msg.gap4Ticks);
                buf.writeVarInt(msg.scrollTicks);
                buf.writeUtf(msg.card1Title, 256);
                buf.writeUtf(msg.card1Subtitle, 256);
                buf.writeUtf(msg.card2Title, 256);
                buf.writeUtf(msg.card2Subtitle, 256);
                buf.writeUtf(msg.card3Title, 256);
                buf.writeUtf(msg.card3Subtitle, 256);
                buf.writeUtf(msg.card4Title, 256);
                buf.writeUtf(msg.card4Subtitle, 256);
                buf.writeUtf(msg.scrollText, 8192);
            },
            (buf) -> new CutsceneStartS2CPacket(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(256),
                buf.readUtf(8192)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            CutsceneClientState.start(
                totalTicks,
                card1Ticks,
                gap1Ticks,
                card2Ticks,
                gap2Ticks,
                card3Ticks,
                gap3Ticks,
                card4Ticks,
                gap4Ticks,
                scrollTicks,
                card1Title,
                card1Subtitle,
                card2Title,
                card2Subtitle,
                card3Title,
                card3Subtitle,
                card4Title,
                card4Subtitle,
                scrollText
            );
        });
    }
}
