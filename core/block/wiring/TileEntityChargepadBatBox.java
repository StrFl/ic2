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

public class TileEntityChargepadBatBox
extends TileEntityChargepadBlock {
    public TileEntityChargepadBatBox() {
        super(1, 32, 40000);
    }

    @Override
    public String getInventoryName() {
        return "Chargepad BatBox";
    }

    @Override
    protected void getItems(EntityPlayer player) {
        if (player != null) {
            for (ItemStack current : player.inventory.armorInventory) {
                if (current == null) continue;
                this.chargeitems(current, 32);
            }
            for (ItemStack current : player.inventory.mainInventory) {
                if (current == null) continue;
                this.chargeitems(current, 32);
            }
        }
    }
}

