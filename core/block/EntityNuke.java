/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityNuke
extends EntityIC2Explosive {
    public EntityNuke(World world, double x, double y, double z, float explosiveforce, int RadiationRange) {
        super(world, x, y, z, 300, explosiveforce, 0.05f, 1.5f, StackUtil.getBlock(Ic2Items.nuke), RadiationRange);
    }

    public EntityNuke(World world) {
        this(world, 0.0, 0.0, 0.0, 0.0f, 0);
    }

    public boolean interactFirst(EntityPlayer player) {
        ItemToolWrench wrench;
        if (IC2.platform.isSimulating() && player.inventory.mainInventory[player.inventory.currentItem] != null && player.inventory.mainInventory[player.inventory.currentItem].getItem() instanceof ItemToolWrench && (wrench = (ItemToolWrench)player.inventory.mainInventory[player.inventory.currentItem].getItem()).canTakeDamage(player.inventory.mainInventory[player.inventory.currentItem], 1)) {
            wrench.damage(player.inventory.mainInventory[player.inventory.currentItem], 1, player);
            this.setDead();
        }
        return false;
    }
}

