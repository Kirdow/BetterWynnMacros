package com.kirdow.wynnmacros.util;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.WynnMacros;
import com.kirdow.wynnmacros.config.ConfigManager;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PlayerHelper {

    public static IScanResult getInteractableBlock(PlayerEntity player, World world) {
        double maxDistance = player.getBlockInteractionRange();
        Vec3d eyePos = player.getCameraPosVec(1.0f);
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d reachVec = eyePos.add(lookVec.multiply(maxDistance));

        BlockHitResult hitResult = world.raycast(new RaycastContext(
                eyePos, reachVec, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, player
        ));

        if (hitResult.getType() == BlockHitResult.Type.BLOCK) {
            Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
            BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());

            return new BlockScanResult(block, hitResult.getBlockPos(), blockEntity);
        }

        return null;
    }

    public static IScanResult getInteractableEntity(PlayerEntity player, World world) {
        double maxDistance = player.getBlockInteractionRange();
        Vec3d eyePos = player.getCameraPosVec(1.0f);
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d reachVec = eyePos.add(lookVec.multiply(maxDistance));

        Box searchBox = new Box(eyePos, reachVec).expand(1.0);

        Entity closestEntity = null;
        double closestDistanceSquared = maxDistance * maxDistance;

        for (Entity entity : world.getOtherEntities(player, searchBox, e -> e.isAlive() && e instanceof LivingEntity)) {
            Box entityBox = entity.getBoundingBox().expand(0.1);

            Optional<Vec3d> hitResult = entityBox.raycast(eyePos, reachVec);

            if (hitResult.isPresent()) {
                double distanceSquared = eyePos.squaredDistanceTo(hitResult.get());

                if (distanceSquared < closestDistanceSquared) {
                    closestEntity = entity;
                    closestDistanceSquared = distanceSquared;
                }
            }
        }

        if (closestEntity != null) {
            Vec3d hitPos = closestEntity.getPos();
            return new EntityScanResult(closestEntity, hitPos);
        }

        return null;
    }

    public static boolean isInteractableBlock(PlayerEntity player, World world) {
        if (getInteractableBlock(player, world) instanceof BlockScanResult(Block block, BlockPos pos, BlockEntity entity)) {
            Class<?> blockClass = block.getClass();
            Class<?> entityClass = entity != null ? entity.getClass() : null;

            for (var knownClass : CLASSES) {
                if (knownClass.isAssignableFrom(blockClass)
                    || (entityClass != null && knownClass.isAssignableFrom(entityClass))) {
                    return true;
                }
            }

            for (var knownBlock : BLOCKS) {
                if (knownBlock == block && isEntityAbove(pos.toCenterPos(), "crafting station", 1.0)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isInteractableEntity(PlayerEntity player, World world) {
        if (getInteractableEntity(player, world) instanceof EntityScanResult(Entity entity, Vec3d pos)) {
            if (entity instanceof VillagerEntity || entity instanceof PlayerEntity) {
                for (var scanEntry : SCAN_ENTRIES) {
                    for (var text : scanEntry.matches) {
                        if (isEntityAbove(pos, text, scanEntry.radius))
                            return true;
                    }
                }
            }
        }


        return false;
    }

    public static boolean canInteract(ClientPlayerEntity player) {
        if (WynnHelper.isBow()) return false; // Because then this is being run for left click which isn't interact
        if (!ConfigManager.get().allowInteraction) return false;

        if (isInteractableBlock(player, player.clientWorld)) return true;
        if (isInteractableEntity(player, player.clientWorld)) return true;

        return false;
    }

    private static boolean isEntityAbove(Vec3d pos, String text, double radius) {
        final String searchFor = text.toLowerCase();

        try (ClientWorld world = WynnHelper.world()) {
            return StreamSupport.stream(
                            world.getEntities().spliterator(),
                            false
                    )
                    .filter(p -> p.getPos().distanceTo(pos.add(0.0, 1.0, 0.0)) < radius)
                    .anyMatch(p -> {
                        if (!(p instanceof DisplayEntity.TextDisplayEntity entity)) return false;
                        var name = MixinHelper.getText(entity);
                        if (name == null) return false;
                        var sName = name.getString();
                        if (sName == null) return false;
                        return sName.toLowerCase().contains(searchFor);
                    });
        } catch (IOException ex) {
            Logger.exception(ex);
            return false;
        }
    }

    private static List<Class<?>> CLASSES;
    private static List<Block> BLOCKS;

    private static List<EntityScanEntry> SCAN_ENTRIES;

    public static void init() {
        CLASSES = Arrays.asList(
                DoorBlock.class,
                FenceGateBlock.class,
                ChestBlockEntity.class,
                EnderChestBlockEntity.class
        );

        BLOCKS = Arrays.asList(
                Blocks.ANVIL,
                Blocks.GRINDSTONE,
                Blocks.LOOM,
                Blocks.CRAFTING_TABLE,
                Blocks.CARTOGRAPHY_TABLE,
                Blocks.SMITHING_TABLE,
                Blocks.STONECUTTER,
                Blocks.BREWING_STAND,
                Blocks.ENCHANTING_TABLE,
                Blocks.END_PORTAL_FRAME,
                Blocks.SMOOTH_STONE_SLAB,
                Blocks.FURNACE,
                Blocks.BLAST_FURNACE
        );

        Map<Double, Set<String>> entries = new HashMap<>();
        entries.compute(1.0, (ignored, set) -> {
            var list = Arrays.asList("npc", "blacksmith", "party finder");
            if (set != null) {
                set.addAll(list);
            } else {
                set = new HashSet<>(list);
            }
            return set;
        });

        entries.compute(2.0, (ignored, set) -> {
            var list = Arrays.asList("trade market");
            if (set != null) {
                set.addAll(list);
            } else {
                set = new HashSet<>(list);
            }
            return set;
        });

        SCAN_ENTRIES = entries.entrySet()
                .stream()
                .map(p -> new EntityScanEntry(
                        p.getKey(),
                        p.getValue().toArray(new String[0])))
                .collect(Collectors.toList());
    }

    public record EntityScanEntry(double radius, String[] matches) {}

    public interface IScanResult {}

    public record BlockScanResult(Block block, BlockPos pos, BlockEntity entity) implements IScanResult {}

    public record EntityScanResult(Entity entity, Vec3d pos) implements IScanResult {}

}
