/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.comp;

import ic2.core.block.TileEntityBlock;
import java.io.DataInput;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityComponent {
    protected final TileEntityBlock parent;

    public TileEntityComponent(TileEntityBlock parent) {
        this.parent = parent;
    }

    public abstract String getDefaultName();

    public void readFromNbt(NBTTagCompound nbt) {
    }

    public NBTTagCompound writeToNbt() {
        return null;
    }

    public void onLoaded() {
    }

    public void onUnloaded() {
    }

    public void onNeighborUpdate(Block srcBlock) {
    }

    public void onContainerUpdate(String name, EntityPlayerMP player) {
    }

    public void onNetworkUpdate(DataInput is) throws IOException {
    }

    public boolean enableWorldTick() {
        return false;
    }

    public void onWorldTick() {
    }
}

