package com.jeekrs.MineRobot.processor;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.minecraft.util.EnumHand.MAIN_HAND;

public class BlockPutNode extends BlockEventNode {

    private final BlockPos pos;
    private final String itemName;
    private ItemStack item;
    // todo specify itemName/block name

    public BlockPutNode(World world, BlockPos pos, String itemName) {
        super(world);
        this.pos = pos;
        this.itemName = itemName;
    }

    @Override
    public boolean checkStart() {
        InventoryPlayer inventory = getMinecraft().player.inventory;
        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack is = inventory.getStackInSlot(i);
            if(is.getDisplayName().equals(itemName))
            {
                item = is;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkFinish() {
        return true;
    }

    @Override
    public void work() {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemstack = item;
        BlockPos blockpos = pos;
        if (mc.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
            int i = itemstack.getCount();
            EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, blockpos, EnumFacing.DOWN, mc.objectMouseOver.hitVec, MAIN_HAND);
            if (enumactionresult == EnumActionResult.SUCCESS) {
                mc.player.swingArm(MAIN_HAND);

                if (!itemstack.isEmpty() && (itemstack.getCount() != i || mc.playerController.isInCreativeMode())) {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress(MAIN_HAND);
                }
                return;
            }

        }

        if (!itemstack.isEmpty() && mc.playerController.processRightClick(mc.player, mc.world, MAIN_HAND) == EnumActionResult.SUCCESS) {
            mc.entityRenderer.itemRenderer.resetEquippedProgress(MAIN_HAND);
            return;
        }

    }

    @Override
    public void finish() {

    }
}
