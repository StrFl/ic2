/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraftforge.event.entity.player.FillBucketEvent
 */
package ic2.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.core.block.BlockIC2Fluid;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class IC2BucketHandler {
    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);
        if (block instanceof BlockIC2Fluid && event.isCancelable()) {
            event.setCanceled(true);
        }
    }
}

