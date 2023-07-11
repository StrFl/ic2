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

public class TileEntityChargepadCESU
extends TileEntityChargepadBlock {
    public TileEntityChargepadCESU() {
        super(2, 128, 300000);
    }

    @Override
    public String getInventoryName() {
        return "Chargepad CESU";
    }

    @Override
    protected void getItems(EntityPlayer player) {
        if (player != null) {
            for (ItemStack current : player.inventory.armorInventory) {
                if (current == null) continue;
                this.chargeitems(current, 128);
            }
            for (ItemStack current : player.inventory.mainInventory) {
                if (current == null) continue;
                this.chargeitems(current, 128);
            }
        }
    }
}

