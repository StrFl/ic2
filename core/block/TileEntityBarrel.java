/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.item.ItemBooze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBarrel
extends TileEntity {
    public int type = 0;
    public int boozeAmount = 0;
    public int age = 0;
    public boolean detailed = true;
    public int treetapSide = 0;
    public int hopsCount = 0;
    public int wheatCount = 0;
    public int solidRatio = 0;
    public int hopsRatio = 0;
    public int timeRatio = 0;

    public void set(int value) {
        this.type = ItemBooze.getTypeOfValue(value);
        if (this.type > 0) {
            this.boozeAmount = ItemBooze.getAmountOfValue(value);
        }
        if (this.type == 1) {
            this.detailed = false;
            this.hopsRatio = ItemBooze.getHopsRatioOfBeerValue(value);
            this.solidRatio = ItemBooze.getSolidRatioOfBeerValue(value);
            this.timeRatio = ItemBooze.getTimeRatioOfBeerValue(value);
        }
        if (this.type == 2) {
            this.detailed = true;
            this.age = this.timeNedForRum(this.boozeAmount) * ItemBooze.getProgressOfRumValue(value) / 100;
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.type = nbttagcompound.getByte("type");
        this.boozeAmount = nbttagcompound.getByte("waterCount");
        this.age = nbttagcompound.getInteger("age");
        this.treetapSide = nbttagcompound.getByte("treetapSide");
        this.detailed = nbttagcompound.getBoolean("detailed");
        if (this.type == 1) {
            if (this.detailed) {
                this.hopsCount = nbttagcompound.getByte("hopsCount");
                this.wheatCount = nbttagcompound.getByte("wheatCount");
            }
            this.solidRatio = nbttagcompound.getByte("solidRatio");
            this.hopsRatio = nbttagcompound.getByte("hopsRatio");
            this.timeRatio = nbttagcompound.getByte("timeRatio");
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setByte("type", (byte)this.type);
        nbttagcompound.setByte("waterCount", (byte)this.boozeAmount);
        nbttagcompound.setInteger("age", this.age);
        nbttagcompound.setByte("treetapSide", (byte)this.treetapSide);
        nbttagcompound.setBoolean("detailed", this.detailed);
        if (this.type == 1) {
            if (this.detailed) {
                nbttagcompound.setByte("hopsCount", (byte)this.hopsCount);
                nbttagcompound.setByte("wheatCount", (byte)this.wheatCount);
            }
            nbttagcompound.setByte("solidRatio", (byte)this.solidRatio);
            nbttagcompound.setByte("hopsRatio", (byte)this.hopsRatio);
            nbttagcompound.setByte("timeRatio", (byte)this.timeRatio);
        }
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this.isEmpty() && this.treetapSide < 2) {
            ++this.age;
            if (this.type == 1 && this.timeRatio < 5) {
                int x = this.timeRatio;
                if (x == 4) {
                    x += 2;
                }
                if ((double)this.age >= 24000.0 * Math.pow(3.0, x)) {
                    this.age = 0;
                    ++this.timeRatio;
                }
            }
        }
    }

    public boolean isEmpty() {
        return this.type == 0 || this.boozeAmount <= 0;
    }

    public boolean rightclick(EntityPlayer player) {
        ItemStack cur = player.getCurrentEquippedItem();
        if (cur == null) {
            return false;
        }
        if (cur.getItem() == Items.water_bucket) {
            if (!this.detailed || this.boozeAmount + 1 > 32 || this.type > 1) {
                return false;
            }
            this.type = 1;
            cur.func_150996_a(Items.bucket);
            ++this.boozeAmount;
            return true;
        }
        if (cur.getItem() == Ic2Items.waterCell.getItem()) {
            if (!this.detailed || this.type > 1) {
                return false;
            }
            this.type = 1;
            int wantgive = cur.stackSize;
            if (player.isSneaking()) {
                wantgive = 1;
            }
            if (this.boozeAmount + wantgive > 32) {
                wantgive = 32 - this.boozeAmount;
            }
            if (wantgive <= 0) {
                return false;
            }
            this.boozeAmount += wantgive;
            cur.stackSize -= wantgive;
            if (cur.stackSize <= 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            return true;
        }
        if (cur.getItem() == Items.wheat) {
            if (!this.detailed || this.type > 1) {
                return false;
            }
            this.type = 1;
            int wantgive = cur.stackSize;
            if (player.isSneaking()) {
                wantgive = 1;
            }
            if (wantgive > 64 - this.wheatCount) {
                wantgive = 64 - this.wheatCount;
            }
            if (wantgive <= 0) {
                return false;
            }
            this.wheatCount += wantgive;
            cur.stackSize -= wantgive;
            if (cur.stackSize <= 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            this.alterComposition();
            return true;
        }
        if (cur.getItem() == Ic2Items.hops.getItem()) {
            if (!this.detailed || this.type > 1) {
                return false;
            }
            this.type = 1;
            int wantgive = cur.stackSize;
            if (player.isSneaking()) {
                wantgive = 1;
            }
            if (wantgive > 64 - this.hopsCount) {
                wantgive = 64 - this.hopsCount;
            }
            if (wantgive <= 0) {
                return false;
            }
            this.hopsCount += wantgive;
            cur.stackSize -= wantgive;
            if (cur.stackSize <= 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            this.alterComposition();
            return true;
        }
        if (cur.getItem() == Items.reeds) {
            if (this.age > 600 || this.type > 0 && this.type != 2) {
                return false;
            }
            this.type = 2;
            int wantgive = cur.stackSize;
            if (player.isSneaking()) {
                wantgive = 1;
            }
            if (this.boozeAmount + wantgive > 32) {
                wantgive = 32 - this.boozeAmount;
            }
            if (wantgive <= 0) {
                return false;
            }
            this.boozeAmount += wantgive;
            cur.stackSize -= wantgive;
            if (cur.stackSize <= 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            return true;
        }
        return false;
    }

    public void alterComposition() {
        if (this.timeRatio == 0) {
            this.age = 0;
        }
        if (this.timeRatio == 1) {
            if (this.worldObj.rand.nextBoolean()) {
                this.timeRatio = 0;
            } else if (this.worldObj.rand.nextBoolean()) {
                this.timeRatio = 5;
            }
        }
        if (this.timeRatio == 2 && this.worldObj.rand.nextBoolean()) {
            this.timeRatio = 5;
        }
        if (this.timeRatio > 2) {
            this.timeRatio = 5;
        }
    }

    public boolean drainLiquid(int amount) {
        if (this.isEmpty()) {
            return false;
        }
        if (amount > this.boozeAmount) {
            return false;
        }
        this.enforceUndetailed();
        if (this.type == 2) {
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            this.boozeAmount -= amount;
            this.age = progress / 100 * this.timeNedForRum(this.boozeAmount);
        } else {
            this.boozeAmount -= amount;
        }
        if (this.boozeAmount <= 0) {
            if (this.type == 1) {
                this.hopsCount = 0;
                this.wheatCount = 0;
                this.hopsRatio = 0;
                this.solidRatio = 0;
                this.timeRatio = 0;
            }
            this.type = 0;
            this.detailed = true;
            this.boozeAmount = 0;
        }
        return true;
    }

    public void enforceUndetailed() {
        if (!this.detailed) {
            return;
        }
        this.detailed = false;
        if (this.type == 1) {
            float solid;
            float hops;
            float f = hops = this.wheatCount > 0 ? (float)this.hopsCount / (float)this.wheatCount : 10.0f;
            if (this.hopsCount <= 0 && this.wheatCount <= 0) {
                hops = 0.0f;
            }
            float f2 = solid = this.boozeAmount > 0 ? (float)(this.hopsCount + this.wheatCount) / (float)this.boozeAmount : 10.0f;
            if (hops <= 0.25f) {
                this.hopsRatio = 0;
            }
            if (hops > 0.25f && hops <= 0.33333334f) {
                this.hopsRatio = 1;
            }
            if (hops > 0.33333334f && hops <= 0.5f) {
                this.hopsRatio = 2;
            }
            if (hops > 0.5f && hops < 2.0f) {
                this.hopsRatio = 3;
            }
            if (hops >= 2.0f && hops < 3.0f) {
                this.hopsRatio = 4;
            }
            if (hops >= 3.0f && hops < 4.0f) {
                this.hopsRatio = 5;
            }
            if (hops >= 4.0f && hops < 5.0f) {
                this.hopsRatio = 6;
            }
            if (hops >= 5.0f) {
                this.timeRatio = 5;
            }
            if (solid <= 0.41666666f && solid > 0.41666666f && solid <= 0.5f) {
                this.solidRatio = 1;
            }
            if (solid > 0.5f && solid < 1.0f) {
                this.solidRatio = 2;
            }
            if (solid == 1.0f) {
                this.solidRatio = 3;
            }
            if (solid > 1.0f && solid < 2.0f) {
                this.solidRatio = 4;
            }
            if (solid >= 2.0f && solid < 2.4f) {
                this.solidRatio = 5;
            }
            if (solid >= 2.4f && solid < 4.0f) {
                this.solidRatio = 6;
            }
            if (solid >= 4.0f) {
                this.timeRatio = 5;
            }
        }
    }

    public boolean useTreetapOn(EntityPlayer player, int side) {
        ItemStack cur = player.getCurrentEquippedItem();
        if (cur != null && cur.getItem() == Ic2Items.treetap.getItem() && cur.getItemDamage() == 0 && side > 1) {
            this.treetapSide = side;
            this.update();
            if (!player.capabilities.isCreativeMode) {
                --player.inventory.mainInventory[player.inventory.currentItem].stackSize;
                if (player.inventory.mainInventory[player.inventory.currentItem].stackSize == 0) {
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }
            }
            return true;
        }
        return false;
    }

    public void update() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public int calculateMetaValue() {
        if (this.isEmpty()) {
            return 0;
        }
        if (this.type == 1) {
            this.enforceUndetailed();
            int value = 0;
            value |= this.timeRatio;
            value <<= 3;
            value |= this.hopsRatio;
            value <<= 3;
            value |= this.solidRatio;
            value <<= 5;
            value |= this.boozeAmount - 1;
            value <<= 2;
            return value |= this.type;
        }
        if (this.type == 2) {
            this.enforceUndetailed();
            int value = 0;
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            if (progress > 100) {
                progress = 100;
            }
            value |= progress;
            value <<= 5;
            value |= this.boozeAmount - 1;
            value <<= 2;
            return value |= this.type;
        }
        return 0;
    }

    public int timeNedForRum(int amount) {
        return (int)((double)(1200 * amount) * Math.pow(0.95, amount - 1));
    }
}

