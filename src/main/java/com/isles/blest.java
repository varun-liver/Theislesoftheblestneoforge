package com.isles;

import com.isles.client.renderer.*;
import com.isles.network.ModNetwork;
import com.isles.entity.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent; // Replaces RegisterGuiOverlaysEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent;         // If needed
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import terrablender.api.SurfaceRuleManager;
import com.isles.portal.CloudPortalBlock;
import com.isles.portal.CloudPortalIgniterItem;
import com.isles.block.InfectionGrassBlock;
import com.isles.block.InfectionBlock;

import java.util.List;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

@Mod(blest.MODID)
public class blest {

    public static final String MODID = "theislesoftheblest";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> glowing_stars = PARTICLE_TYPES.register("glowing_stars", () -> new SimpleParticleType(true));

    public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.biome.BiomeSource>> BIOME_SOURCE_CODECS = DeferredRegister.create(Registries.BIOME_SOURCE, MODID);
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.biome.BiomeSource>, com.mojang.serialization.MapCodec<com.isles.worldgen.InfectionBiomeSource>> INFECTION_BIOME_SOURCE = BIOME_SOURCE_CODECS.register("infection_biome_source", () -> com.isles.worldgen.InfectionBiomeSource.CODEC);

    public static final DeferredRegister<net.minecraft.world.level.levelgen.feature.Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, net.minecraft.world.level.levelgen.feature.Feature<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration>> INFECTION_TREE_FEATURE = FEATURES.register("infection_trees", () -> new com.isles.worldgen.InfectionTreeFeature(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC));

    public static final DeferredBlock<Block> sky_grass = BLOCKS.register("sky_grass", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.8f,1.0f)
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.GRASS)
                    .requiresCorrectToolForDrops()));

    public static final DeferredItem<Item> sky_grass_ITEM = ITEMS.register("sky_grass", () -> new BlockItem(sky_grass.get(), new Item.Properties()));
    public static final DeferredBlock<Block> sky_crystal = BLOCKS.register("sky_crystal", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()));
    public static final DeferredItem<Item> sky_crystal_ITEM = ITEMS.register("sky_crystal", () -> new BlockItem(sky_crystal.get(), new Item.Properties()));
    public static final DeferredBlock<Block> cloud_portal = BLOCKS.register("cloud_portal", () -> new CloudPortalBlock(
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 11)
    ));
    public static final DeferredBlock<Block> summoner = BLOCKS.register("summoner", () -> new SummonerBlock(
            BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
    ));
    public static final DeferredItem<Item> summoner_ITEM = ITEMS.register("summoner", () -> new BlockItem(summoner.get(), new Item.Properties()));

    public static final DeferredItem<Item> golden_cherry = ITEMS.register("golden_cherry", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().fast().nutrition(3).saturationModifier(2f).build())));
    public static final DeferredItem<Item> sky_catalyst = ITEMS.register(
            "sky_catalyst",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> unshaped_sky_catalyst = ITEMS.register(
            "unshaped_sky_catalyst",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> cloud_igniter = ITEMS.register(
            "cloud_igniter",
            () -> new CloudPortalIgniterItem(new Item.Properties().durability(64))
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SkyGuardianEntity>> sky_guardian = ENTITY_TYPES.register("sky_guardian",
            () -> EntityType.Builder.of(SkyGuardianEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.3F)
                    .build(MODID + ":sky_guardian"));
    public static final DeferredItem<Item> sky_guardian_spawn_egg = ITEMS.register("sky_guardian_spawn_egg",
            () -> new DeferredSpawnEggItem(sky_guardian, 0x9dd5ef, 0x1c5f87, new Item.Properties()));

    public static final DeferredHolder<EntityType<?>, EntityType<TheinfectionEntity>> the_infection = ENTITY_TYPES.register("the_infection",
            () -> EntityType.Builder.of(TheinfectionEntity::new, MobCategory.MONSTER)
                    .sized(3F,3F)
                    .build(MODID + ":the_infection")
            );
    public static final DeferredItem<Item> the_infection_spawn_egg = ITEMS.register("the_infection_spawn_egg",
            () -> new DeferredSpawnEggItem(the_infection, 0x9dd5ef, 0x1c5f87, new Item.Properties()));

    public static final DeferredHolder<EntityType<?>, EntityType<ThewhispererEntity>> the_whisperer = ENTITY_TYPES.register("the_whisperer",
            () -> EntityType.Builder.of(ThewhispererEntity::new, MobCategory.CREATURE)
                    .sized(1f,1f)
                    .build(MODID + ":the_whisperer")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<TheGuardianEntity>> the_guardian = ENTITY_TYPES.register("the_guardian",
            () -> EntityType.Builder.of(TheGuardianEntity::new, MobCategory.MONSTER)
                .sized(1f,2f)
                .build(MODID + ":the_guardian"));
    public static final DeferredItem<Item> the_guardian_spawn_egg = ITEMS.register("the_guardian_spawn_egg",
            () -> new DeferredSpawnEggItem(the_guardian, 0x9dd5ef, 0x1c5f87, new Item.Properties()));

    public static final DeferredHolder<EntityType<?>, EntityType<TheCursedOnesEntity>> the_cursed_ones = ENTITY_TYPES.register("the_cursed_ones",
            () -> EntityType.Builder.of(TheCursedOnesEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .build(MODID + ":the_cursed_ones"));
    public static final DeferredItem<Item> the_cursed_ones_spawn_egg = ITEMS.register("the_cursed_ones_spawn_egg",
            () -> new DeferredSpawnEggItem(the_cursed_ones, 0x3b3b3b, 0x7d2b2b, new Item.Properties()));

    public static final DeferredItem<Item> the_whisperer_spawn_egg = ITEMS.register("the_whisperer_spawn_egg",
            () -> new DeferredSpawnEggItem(the_whisperer, 0x9dffef, 0x1cff87, new Item.Properties()));

    public static final DeferredHolder<EntityType<?>, EntityType<Infection_GuardiansEntity>> Infection_Guardians = ENTITY_TYPES.register("infection_guardians",
            () -> EntityType.Builder.of(Infection_GuardiansEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .build(MODID + ":infection_guardians"));

    public static final DeferredHolder<SoundEvent, SoundEvent> THE_INFECTION_AMBIENT = SOUND_EVENTS.register("entity.the_infection.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_infection.ambient")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_INFECTION_HURT = SOUND_EVENTS.register("entity.the_infection.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_infection.hurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_INFECTION_DEATH = SOUND_EVENTS.register("entity.the_infection.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_infection.death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_GUARDIAN_DEATH = SOUND_EVENTS.register("entity.the_guardian.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_guardian.death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_GUARDIAN_HURT = SOUND_EVENTS.register("entity.the_guardian.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_guardian.hurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_GUARDIAN_AMBIENT = SOUND_EVENTS.register("entity.the_guardian.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_guardian.ambient")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_GUARDIAN_WHOOSH = SOUND_EVENTS.register("entity.the_guardian.whoosh", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_guardian.whoosh")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_CURSED_ONES_AMBIENT = SOUND_EVENTS.register("entity.the_cursed_ones.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_cursed_ones.ambient")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_CURSED_ONES_HURT = SOUND_EVENTS.register("entity.the_cursed_ones.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_cursed_ones.hurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_CURSED_ONES_DEATH = SOUND_EVENTS.register("entity.the_cursed_ones.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.the_cursed_ones.death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_Guardians_DEATH = SOUND_EVENTS.register("entity.infection_guardians.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.infection_guardians.death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_Guardians_HURT = SOUND_EVENTS.register("entity.infection_guardians.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.infection_guardians.hurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_Guardians_AMBIENT = SOUND_EVENTS.register("entity.infection_guardians.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "entity.infection_guardians.ambient")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CLOUD_BREAK = SOUND_EVENTS.register("block.cloud.break", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.cloud.break")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CLOUD_STEP = SOUND_EVENTS.register("block.cloud.step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.cloud.step")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CLOUD_PLACE = SOUND_EVENTS.register("block.cloud.place", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.cloud.place")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CLOUD_HIT = SOUND_EVENTS.register("block.cloud.hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.cloud.hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> CLOUD_FALL = SOUND_EVENTS.register("block.cloud.fall", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.cloud.fall")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_BREAK = SOUND_EVENTS.register("block.infection.break", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.break")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_STEP = SOUND_EVENTS.register("block.infection.step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.step")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_PLACE = SOUND_EVENTS.register("block.infection.place", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.place")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_HIT = SOUND_EVENTS.register("block.infection.hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_FALL = SOUND_EVENTS.register("block.infection.fall", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.fall")));
    public static final DeferredHolder<SoundEvent, SoundEvent> Infection_SPREAD = SOUND_EVENTS.register("block.infection.spread", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.infection.spread")));

    public static final SoundType CLOUD_SOUNDS = new SoundType(1.0F, 1.0F, CLOUD_BREAK.get(), CLOUD_STEP.get(), CLOUD_PLACE.get(), CLOUD_HIT.get(), CLOUD_FALL.get());
    public static final SoundType infection_SOUNDS = new SoundType(1.0F, 1.0F, Infection_BREAK.get(), Infection_STEP.get(), Infection_PLACE.get(), Infection_HIT.get(), Infection_FALL.get());

    public static final DeferredBlock<Block> cloud = BLOCKS.register("cloud", () -> new Block(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(CLOUD_SOUNDS)));
    public static final DeferredBlock<Block> infection_grass = BLOCKS.register("infection_grass", () -> new InfectionGrassBlock(BlockBehaviour.Properties.of().strength(0.8f,1.0f).sound(SoundType.GRASS).randomTicks()));
    public static final DeferredBlock<Block> infection = BLOCKS.register("infection", () -> new InfectionBlock(BlockBehaviour.Properties.of().strength(2.0F,7.0F).sound(infection_SOUNDS).randomTicks()));

    public static final DeferredItem<Item> infection_ITEM = ITEMS.register("infection", () -> new BlockItem(infection.get(),new Item.Properties()));
    public static final DeferredItem<Item> infection_grass_ITEM = ITEMS.register("infection_grass", () -> new BlockItem(infection_grass.get(),new Item.Properties()));
    public static final DeferredItem<Item> cloud_ITEM = ITEMS.register("cloud", () -> new BlockItem(cloud.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SummonerBlockEntity>> SUMMONER_BLOCK_ENTITY = BLOCK_ENTITIES.register("summoner", () -> BlockEntityType.Builder.of(SummonerBlockEntity::new, summoner.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfectionBlockEntity>> INFECTION_BLOCK_ENTITY = BLOCK_ENTITIES.register("infection", () -> BlockEntityType.Builder.of(InfectionBlockEntity::new, infection.get(), infection_grass.get()).build(null));

    private static final TagKey<Block> INCORRECT_FOR_SKY_TOOL = BlockTags.create(new ResourceLocation("minecraft", "incorrect_for_iron_tool"));
    private static final TagKey<Block> INCORRECT_FOR_Legendary_Tier = BlockTags.create(new ResourceLocation("minecraft","incorrect_for_netherite_tool"));

    public static final Tier SKY_TIER = new SimpleTier(INCORRECT_FOR_SKY_TOOL, 750, 8.0F, 3.0F, 18, () -> Ingredient.of(sky_catalyst.get()));
    public static final Tier Legendary_Tier = new SimpleTier(INCORRECT_FOR_Legendary_Tier, 10000, 10F, 20F, 100, () -> Ingredient.of(sky_catalyst.get()));

    public static final DeferredItem<Item> SKY_SWORD = ITEMS.register("sky_sword", () -> new SwordItem(SKY_TIER,  new Item.Properties()));
    public static final DeferredItem<Item> SKY_PICKAXE = ITEMS.register("sky_pickaxe", () -> new PickaxeItem(SKY_TIER, new Item.Properties()));
    public static final DeferredItem<Item> SKY_AXE = ITEMS.register("sky_axe", () -> new AxeItem(SKY_TIER,  new Item.Properties()));
    public static final DeferredItem<Item> SKY_SHOVEL = ITEMS.register("sky_shovel", () -> new ShovelItem(SKY_TIER,  new Item.Properties()));
    public static final DeferredItem<Item> SKY_HOE = ITEMS.register("sky_hoe", () -> new HoeItem(SKY_TIER,  new Item.Properties()));
    public static final DeferredItem<Item> Harpe = ITEMS.register("harpe", ()-> new SwordItem(Legendary_Tier,new Item.Properties()));

    public static final DeferredItem<Item> SKY_CATALYST_HELMET = ITEMS.register("sky_catalyst_helmet", () -> new ArmorItem(ModArmorMaterials.SKY_CATALYST, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(25))));
    public static final DeferredItem<Item> SKY_CATALYST_CHESTPLATE = ITEMS.register("sky_catalyst_chestplate", () -> new ArmorItem(ModArmorMaterials.SKY_CATALYST, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(25))));
    public static final DeferredItem<Item> SKY_CATALYST_LEGGINGS = ITEMS.register("sky_catalyst_leggings", () -> new ArmorItem(ModArmorMaterials.SKY_CATALYST, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(25))));
    public static final DeferredItem<Item> SKY_CATALYST_BOOTS = ITEMS.register("sky_catalyst_boots", () -> new ArmorItem(ModArmorMaterials.SKY_CATALYST, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(25))));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> sky_grass_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(sky_grass_ITEM.get());
        output.accept(sky_crystal_ITEM.get());
        output.accept(cloud_ITEM.get());
        output.accept(golden_cherry.get());
        output.accept(sky_guardian_spawn_egg.get());
        output.accept(sky_catalyst.get());
        output.accept(unshaped_sky_catalyst.get());
        output.accept(cloud_igniter.get());
        output.accept(summoner_ITEM.get());
        output.accept(SKY_SWORD.get());
        output.accept(SKY_PICKAXE.get());
        output.accept(SKY_AXE.get());
        output.accept(SKY_SHOVEL.get());
        output.accept(SKY_HOE.get());
        output.accept(Harpe.get());
        output.accept(SKY_CATALYST_HELMET.get());
        output.accept(SKY_CATALYST_CHESTPLATE.get());
        output.accept(SKY_CATALYST_LEGGINGS.get());
        output.accept(SKY_CATALYST_BOOTS.get());
        output.accept(the_guardian_spawn_egg.get());
        output.accept(the_cursed_ones_spawn_egg.get());
        output.accept(infection_grass_ITEM.get());
        output.accept(infection.get());
    }).build());

    public blest(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ARMOR_MATERIALS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BIOME_SOURCE_CODECS.register(modEventBus);
        FEATURES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerAttributes);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("revert")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    Config.spreadInfection = true;
                    context.getSource().sendSuccess(() -> Component.literal("Infection spread has been manually re-enabled."), true);
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("spread")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerLevel level = context.getSource().getLevel();
                    int count = 0;
                    for (InfectionBlockEntity be : InfectionBlockEntity.INSTANCES) {
                        if (be.getLevel() == level) {
                            be.spread(level, be.getBlockPos(), be.getBlockState());
                            count++;
                        }
                    }
                    int finalCount = count;
                    context.getSource().sendSuccess(() -> Component.literal("Infection spread triggered for " + finalCount + " blocks."), true);
                    return 1;
                })
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.init();
            com.isles.worldgen.BlestTerrablender.registerRegions();
            SurfaceRuleManager.addSurfaceRules(
                    SurfaceRuleManager.RuleCategory.OVERWORLD,
                    MODID,
                    com.isles.worldgen.BlestSurfaceRules.makeRules()
            );
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(sky_grass_ITEM.get());
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) event.accept(sky_crystal_ITEM.get());
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) event.accept(sky_catalyst.get());
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) event.accept(unshaped_sky_catalyst.get());
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) event.accept(sky_guardian_spawn_egg.get());
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(summoner_ITEM.get());
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(sky_guardian.get(), SkyGuardianEntity.createAttributes().build());
        event.put(the_infection.get(), TheinfectionEntity.createAttributes().build());
        event.put(the_whisperer.get(), ThewhispererEntity.createAttributes().build());
        event.put(the_guardian.get(), TheGuardianEntity.createAttributes().build());
        event.put(the_cursed_ones.get(), TheCursedOnesEntity.createAttributes().build());
        event.put(Infection_Guardians.get(),Infection_GuardiansEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Config.spreadInfection = Config.SPREAD_INFECTION.get();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        CompoundTag data = player.getPersistentData();
        CompoundTag persisted = data.getCompound(Player.PERSISTED_NBT_TAG);
        String key = MODID + ":first_joined";
        if (!persisted.getBoolean(key)) {
            persisted.putBoolean(key, true);
            data.put(Player.PERSISTED_NBT_TAG, persisted);
            player.sendSystemMessage(Component.literal("Welcome to The Isles of the Blest!"));
            ThewhispererEntity e = blest.the_whisperer.get().create(level);
            if (e != null) {
                double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
                double distance = 8.0;
                double dx = Math.cos(angle) * distance;
                double dz = Math.sin(angle) * distance;
                BlockPos base = BlockPos.containing(player.position().add(dx, 0.0, dz));
                BlockPos safe = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);
                e.moveTo(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5);
                e.setFollowTarget(player);
                level.addFreshEntity(e);
                e.setPersistenceRequired();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        CompoundTag original = event.getOriginal().getPersistentData();
        CompoundTag originalPersisted = original.getCompound(Player.PERSISTED_NBT_TAG);
        event.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG, originalPersisted.copy());
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ForgeClientEvents {
        private static final ResourceLocation INFECTION_BAR = new ResourceLocation(blest.MODID, "textures/bossbars/theinfection.png");
        private static final ResourceLocation GUARDIAN_BAR = new ResourceLocation(blest.MODID, "textures/bossbars/theguardian.png");

        @SubscribeEvent
        public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
            Component name = event.getBossEvent().getName();
            String bossName = name.getString();
            ResourceLocation customTexture = null;
            if (bossName.equals("The Infection")) customTexture = INFECTION_BAR;
            else if (bossName.equals("The Guardian")) customTexture = GUARDIAN_BAR;

            if (customTexture != null) {
                event.setCanceled(true);
                int x = event.getX();
                int y = event.getY();
                float progress = event.getBossEvent().getProgress();
                ResourceLocation vanillaBars = new ResourceLocation("minecraft", "textures/gui/bars.png");
                int colorIndex = event.getBossEvent().getColor().ordinal();
                int overlayIndex = event.getBossEvent().getOverlay().ordinal();
                int vOffset = (overlayIndex * 7 + colorIndex) * 10;
                event.getGuiGraphics().blit(vanillaBars, x, y, 0, vOffset, 182, 5, 256, 256);
                int currentWidth = (int) (progress * 182.0F);
                if (currentWidth > 0) event.getGuiGraphics().blit(vanillaBars, x, y, 0, vOffset + 5, currentWidth, 5, 256, 256);
                event.getGuiGraphics().blit(customTexture, x, y, 0, 0, 182, 15, 256, 256);
                Font font = Minecraft.getInstance().font;
                int nameWidth = font.width(name);
                event.getGuiGraphics().drawString(font, name, x + 91 - nameWidth / 2, y - 10, 0xFFFFFF);
                event.setIncrement(25);
            }
        }
    }

    public static BlockPos getTowerCoords(ServerLevel level, BlockPos playerPos) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var towerKey = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, "portal_ruins"));
        Holder<Structure> towerHolder = registry.getHolderOrThrow(towerKey);
        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(level, net.minecraft.core.HolderSet.direct(towerHolder), playerPos, 100, false);
        return result != null ? result.getFirst() : null;
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(sky_guardian.get(), SkyGuardianRenderer::new);
            event.registerEntityRenderer(the_infection.get(), com.isles.client.renderer.TheinfectionRenderer::new);
            event.registerEntityRenderer(the_whisperer.get(), ThewhispererRenderer::new);
            event.registerEntityRenderer(the_guardian.get(), TheGuardianRenderer::new);
            event.registerEntityRenderer(the_cursed_ones.get(), TheCursedOnesRenderer::new);
            event.registerEntityRenderer(Infection_Guardians.get(), Infection_GuardiansRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(SkyGuardianModel.LAYER_LOCATION, SkyGuardianModel::createBodyLayer);
            event.registerLayerDefinition(com.isles.client.renderer.TheinfectionModel.LAYER_LOCATION, com.isles.client.renderer.TheinfectionModel::createBodyLayer);
            event.registerLayerDefinition(ThewhispererModel.LAYER_LOCATION, ThewhispererModel::createBodyLayer);
            event.registerLayerDefinition(TheGuardianModel.LAYER_LOCATION, TheGuardianModel::createBodyLayer);
            event.registerLayerDefinition(TheCursedOnesModel.LAYER_LOCATION, TheCursedOnesModel::createBodyLayer);
            event.registerLayerDefinition(Infection_GuardiansModel.LAYER_LOCATION, Infection_GuardiansModel::createBodyLayer);
        }
    }
}
