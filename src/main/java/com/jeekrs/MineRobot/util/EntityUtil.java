package com.jeekrs.MineRobot.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

interface ICheck {
    boolean check(Entity entity);
}

public class EntityUtil {
    public static List<Entity> sortEntitiesByDistance(EntityPlayer player, List<Entity> entities) {
        entities.sort(Comparator.comparingDouble(entity -> entity.getDistanceSq(player)));
        return entities;
    }

    public static List<Entity> entityList(World world) {
        return world.loadedEntityList;
    }

    public static Entity nearestEntity(EntityPlayer player, ICheck checker) {
        if (checker == null)
            checker = entity -> entity instanceof EntityLivingBase;

        World world = player.world;
        float closest = Float.MAX_VALUE;
        Entity thisOne = null;
        for (int i = 0; i < world.loadedEntityList.size(); i++) {
            Entity entity = world.loadedEntityList.get(i);
            if (player.getDistance(entity) < closest) {
                if (checker.check(entity) && entity != player) {
                    closest = entity.getDistance(player);
                    thisOne = entity;
                }
            }
        }
        return thisOne;
    }

    public static Entity nearestMob(EntityPlayer player) {
        return nearestEntity(player, e -> e instanceof EntityMob);
    }

    public static EntityPlayer nearestPlayer(EntityPlayer player) {
        return (EntityPlayer) nearestEntity(player, e -> e instanceof EntityPlayer);
    }

    public static boolean canCoordBeSeen(EntityLivingBase ent, int x, int y, int z) {
        return ent.world.rayTraceBlocks(new net.minecraft.util.math.Vec3d(ent.posX, ent.posY + (double) ent.getEyeHeight(), ent.posZ), new net.minecraft.util.math.Vec3d(x, y, z)) == null;
    }

    public static boolean canCoordBeSeenFromFeet(EntityLivingBase ent, int x, int y, int z) {
        return ent.world.rayTraceBlocks(new net.minecraft.util.math.Vec3d(ent.posX, ent.getEntityBoundingBox().minY + 0.15, ent.posZ), new net.minecraft.util.math.Vec3d(x, y, z)) == null;
    }

    public static Vec3d getTargetVector(EntityLivingBase parEnt, EntityLivingBase target) {
        double vecX = target.posX - parEnt.posX;
        double vecY = target.posY - parEnt.posY;
        double vecZ = target.posZ - parEnt.posZ;
        double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
        Vec3d vec3D = new Vec3d(vecX / dist, vecY / dist, vecZ / dist);
        return vec3D;
    }

    public static void moveTowards(Entity ent, Entity targ, float speed) {
        double vecX = targ.posX - ent.posX;
        double vecY = targ.posY - ent.posY;
        double vecZ = targ.posZ - ent.posZ;

        double dist2 = (double) Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
        ent.motionX += vecX / dist2 * speed;
        ent.motionY += vecY / dist2 * speed;
        ent.motionZ += vecZ / dist2 * speed;
    }

    public static String getName(Entity ent) {
        return ent != null ? ent.getName() : "nullObject";
    }

    public static EntityPlayer getPlayerByUUID(UUID uuid) {
        Iterator iterator = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().iterator();
        EntityPlayerMP entityplayermp;

        while (iterator.hasNext()) {
            entityplayermp = (EntityPlayerMP) iterator.next();

            if (entityplayermp.getGameProfile().getId().equals(uuid)) {
                return entityplayermp;
            }
        }

        return null;
    }

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public static EntityPlayer getClosestVulnerablePlayerToEntity(World world, Entity p_72856_1_, double p_72856_2_) {
        return getClosestVulnerablePlayer(world, p_72856_1_.posX, p_72856_1_.posY, p_72856_1_.posZ, p_72856_2_);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public static EntityPlayer getClosestVulnerablePlayer(World world, double p_72846_1_, double p_72846_3_, double p_72846_5_, double p_72846_7_) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < world.playerEntities.size(); ++i) {
            EntityPlayer entityplayer1 = (EntityPlayer) world.playerEntities.get(i);

            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive()) {
                double d5 = entityplayer1.getDistanceSq(p_72846_1_, p_72846_3_, p_72846_5_);
                double d6 = p_72846_7_;

                if (entityplayer1.isSneaking()) {
                    d6 = p_72846_7_ * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible()) {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F) {
                        f = 0.1F;
                    }

                    d6 *= (double) (0.7F * f);
                }

                if ((p_72846_7_ < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    public static boolean canProcessForList(String playerName, String list, boolean whitelistMode) {
        if (whitelistMode) {
            if (!list.contains(playerName)) {
                return false;
            }
        } else {
            if (list.contains(playerName)) {
                return false;
            }
        }
        return true;
    }

    public static Class getClassFromRegistry(String name) {
//Class clazz = EntityList.NAME_TO_CLASS.get(name);
        Class clazz = EntityList.getClass(new ResourceLocation(name));
//dont think this will be needed for proper registered entity names
/*if (clazz == null) {
clazz = EntityList.NAME_TO_CLASS.get(name.replace("minecraft:", "").replace("minecraft.", ""));
}*/
        return clazz;
    }

    /**
     * Mimicing some of vanillas rules for spawning in a mob
     * <p>
     * x y z coords are expected to be the ground the mob is going to spawn on
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean canSpawnMobOnGround(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos.toMcPos());
        Block block = state.getBlock();
        if (BlockUtil.isAir(block) || !block.canCreatureSpawn(state, world, pos.toMcPos(), EntityLiving.SpawnPlacementType.ON_GROUND)) {
            return false;
        }
        return true;
    }

    public static boolean isInDarkCave(World world, int x, int y, int z, boolean checkSpaceToSpawn, boolean skipLightCheck) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos posAir = new BlockPos(x, y + 1, z);
        IBlockState state = world.getBlockState(pos.toMcPos());
        Block block = state.getBlock();
        if (!world.canSeeSky(posAir.toMcPos()) && (skipLightCheck || world.getLightFromNeighbors(posAir.toMcPos()) < 5)) {
            if (!BlockUtil.isAir(block) && state.getMaterial() == Material.ROCK/*(block != Blocks.grass || block.getMaterial() != Material.grass)*/) {

                if (!checkSpaceToSpawn) {
                    return true;
                } else {
                    if (world.isAirBlock(posAir.toMcPos()) && world.isAirBlock(pos.up(2).toMcPos())) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public static boolean attackEntityAsMobForPassives(EntityLivingBase source, Entity entityIn) {

        float f;
        source.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        f = (float) source.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        int i = 0;

        if (entityIn instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(source.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(source);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(source), f);

        if (flag) {
            if (i > 0) {
                ((EntityLivingBase) entityIn).knockBack(source, (float) i * 0.5F, (double) MathHelper.sin(source.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(source.rotationYaw * 0.017453292F)));
                source.motionX *= 0.6D;
                source.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(source);

            if (j > 0) {
                entityIn.setFire(j * 4);
            }

            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entityIn;
                ItemStack itemstack = source.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, source) && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
                    float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(source) * 0.05F;

                    if (source.world.rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        source.world.setEntityState(entityplayer, (byte) 30);
                    }
                }
            }

//protected access, disable or use AT
//source.applyEnchantments(source, entityIn);
        }

        return flag;
    }
}
