/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameData;
import ic2.core.IC2;
import ic2.core.block.BlockMultiID;
import ic2.core.block.BlockWall;
import ic2.core.block.IObscurable;
import ic2.core.block.TileEntityBlock;
import ic2.core.network.ClientModifiable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityWall
extends TileEntityBlock
implements IObscurable {
    @ClientModifiable
    public Block[] retextureRef;
    @ClientModifiable
    public int[] retextureRefMeta;
    @ClientModifiable
    public int[] retextureRefSide;

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("retextureRefMeta")) {
            this.retextureRef = new Block[6];
            boolean found = false;
            for (int i = 0; i < 6; ++i) {
                if (!nbttagcompound.hasKey("retextureRef" + i)) continue;
                this.retextureRef[i] = (Block)GameData.getBlockRegistry().getRaw(nbttagcompound.getString("retextureRef" + i));
                found = found || this.retextureRef[i] != null;
            }
            if (found) {
                this.retextureRefMeta = nbttagcompound.getIntArray("retextureRefMeta");
                this.retextureRefSide = nbttagcompound.getIntArray("retextureRefSide");
                if (this.retextureRefMeta.length != 6 || this.retextureRefSide.length != 6) {
                    this.clearRetexture();
                }
            } else {
                this.clearRetexture();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (this.retextureRef != null) {
            for (int i = 0; i < this.retextureRef.length; ++i) {
                if (this.retextureRef[i] == null) continue;
                nbttagcompound.setString("retextureRef" + i, GameData.getBlockRegistry().getNameForObject((Object)this.retextureRef[i]));
            }
            nbttagcompound.setIntArray("retextureRefMeta", this.retextureRefMeta);
            nbttagcompound.setIntArray("retextureRefSide", this.retextureRefSide);
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("retextureRef");
        ret.add("retextureRefMeta");
        ret.add("retextureRefSide");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("retextureRef") || field.equals("retextureRefMeta") || field.equals("retextureRefSide")) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public boolean retexture(int side, Block referencedBlock, int referencedMeta, int referencedSide) {
        boolean ret = false;
        boolean updateAll = false;
        if (this.retextureRef == null) {
            this.retextureRef = new Block[6];
            this.retextureRefMeta = new int[6];
            this.retextureRefSide = new int[6];
            updateAll = true;
        }
        if (this.retextureRef[side] != referencedBlock || updateAll) {
            this.retextureRef[side] = referencedBlock;
            IC2.network.get().updateTileEntityField(this, "retextureRef");
            ret = true;
        }
        if (this.retextureRefMeta[side] != referencedMeta || updateAll) {
            this.retextureRefMeta[side] = referencedMeta;
            IC2.network.get().updateTileEntityField(this, "retextureRefMeta");
            ret = true;
        }
        if (this.retextureRefSide[side] != referencedSide || updateAll) {
            this.retextureRefSide[side] = referencedSide;
            IC2.network.get().updateTileEntityField(this, "retextureRefSide");
            ret = true;
        }
        return ret;
    }

    @Override
    public Block getReferencedBlock(int side) {
        if (this.retextureRef != null) {
            return this.retextureRef[side];
        }
        return null;
    }

    @Override
    public int getReferencedMeta(int side) {
        if (this.retextureRefMeta != null) {
            return this.retextureRefMeta[side];
        }
        return 0;
    }

    @Override
    public void setColorMultiplier(int colorMultiplier) {
        ((BlockWall)this.getBlockType()).colorMultiplier = colorMultiplier;
    }

    @Override
    public void setRenderMask(int mask) {
        ((BlockMultiID)this.getBlockType()).renderMask = mask;
    }

    private void clearRetexture() {
        this.retextureRef = null;
        this.retextureRefMeta = null;
        this.retextureRefSide = null;
    }
}

