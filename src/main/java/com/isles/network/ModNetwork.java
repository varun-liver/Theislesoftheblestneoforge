package com.isles.network;

import com.isles.blest;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = blest.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetwork {
    private ModNetwork() {}

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(blest.MODID).versioned("1.0.0");

        registrar.playToClient(
                CutsceneStartS2CPacket.TYPE,
                CutsceneStartS2CPacket.CODEC,
                CutsceneStartS2CPacket::handle
        );

        registrar.playToClient(
                CutsceneEndS2CPacket.TYPE,
                CutsceneEndS2CPacket.CODEC,
                CutsceneEndS2CPacket::handle
        );

        registrar.playToServer(
                CutsceneOverlayDoneC2SPacket.TYPE,
                CutsceneOverlayDoneC2SPacket.CODEC,
                CutsceneOverlayDoneC2SPacket::handle
        );
    }

    public static void init() {
        // Registration is now handled via RegisterPayloadHandlersEvent
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        if (message instanceof net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}
