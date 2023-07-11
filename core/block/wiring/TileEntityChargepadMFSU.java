/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityChargepadBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityChargepadMFSU
extends TileEntityChargepadBlock {
    public TileEntityChargepadMFSU() {
        super(4, 2048, 40000000);
    }

    @Override
    public String getInventoryName() {
        return "Chargepad MFSU";
    }

    @Override
    protected void getItems(EntityPlayer player) {
        for (ItemStack current : player.inventory.armorInventory) {
            if (current == null) continue;
            this.chargeitems(current, 2048);
        }
        for (ItemStack current : player.inventory.mainInventory) {
            if (current == null) continue;
            this.chargeitems(current, 2048);
        }
    }
}

