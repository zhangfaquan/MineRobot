package com.jeekrs.MineRobot.blockevent;

import com.jeekrs.MineRobot.util.BlockUtil;
import com.jeekrs.MineRobot.util.ItemUtil;
import com.jeekrs.MineRobot.util.PlayerUtil;
import com.jeekrs.MineRobot.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.minecraft.util.EnumHand.MAIN_HAND;

public class BlockPutNode extends BlockEventNode {

    private final BlockPos pos;
    private final String itemName;
    private int itemNum;
    private boolean ok = false;

    public BlockPutNode(World world, BlockPos pos, String itemName) {
        super(world);
        this.pos = pos;
        this.itemName = itemName;
    }

    @Override
    public boolean checkStart() {
        itemNum = ItemUtil.findIndexInInventory(itemName);
        return itemNum >= 0 && !BlockUtil.checkExists(world, pos);
    }

    @Override
    public boolean checkFinish() {
        return ok;
    }

    @Override
    public void work() {
        if (!PlayerUtil.testDistance(pos))
            return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = Utils.getEntityPlayer();
        ItemStack itemstack = player.inventory.getStackInSlot(itemNum);
        int i = itemstack.getCount();

        int backup = ItemUtil.changeItem(itemNum);

        EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, mc.objectMouseOver.hitVec, MAIN_HAND);
        if (enumactionresult == EnumActionResult.SUCCESS) {
            mc.player.swingArm(MAIN_HAND);

            if (!itemstack.isEmpty() && (itemstack.getCount() != i || mc.playerController.isInCreativeMode())) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress(MAIN_HAND);
            }
        }

        ItemUtil.changeItem(backup);
        ok = true;

    }

    @Override
    public void finish() {

    }

    @Override
    public String toString() {
        return "put:" + itemName + " " + pos;
    }
}