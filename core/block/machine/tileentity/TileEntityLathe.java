/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IKineticSource;
import ic2.api.item.ILatheItem;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerLathe;
import ic2.core.block.machine.gui.GuiLathe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityLathe
extends TileEntityInventory
implements IHasGui,
INetworkClientTileEntityEventListener {
    public InvSlotConsumableClass toolSlot = new InvSlotConsumableClass((TileEntityInventory)this, "slotTool", 0, 1, ILatheItem.ILatheTool.class);
    public InvSlotConsumableClass latheSlot = new InvSlotConsumableClass((TileEntityInventory)this, "lathe", 1, 1, ILatheItem.class);
    public InvSlotOutput outputSlot = new InvSlotOutput(this, "dusts", 2, 1);
    public int kUBuffer = 0;
    public static final int maxKUBuffer = 10000;

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.setActive(this.kUBuffer > 500);
        this.kUBuffer = (int)((float)this.kUBuffer - ((float)(this.kUBuffer / 100) + 0.5f));
        this.getKU();
    }

    private void getKU() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int kineticbandwith;
            TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
            if (!(te instanceof IKineticSource) || (kineticbandwith = ((IKineticSource)te).maxrequestkineticenergyTick(dir.getOpposite())) == 0) continue;
            int diff = Math.min(kineticbandwith, 10000 - this.kUBuffer);
            this.kUBuffer += ((IKineticSource)te).requestkineticenergy(dir.getOpposite(), diff);
            break;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("kUBuffer", this.kUBuffer);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.kUBuffer = nbttagcompound.getInteger("kUBuffer");
    }

    public ContainerBase<TileEntityLathe> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerLathe(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiLathe((ContainerLathe)this.getGuiContainer(entityPlayer));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Lathe";
    }

    public boolean process(int position) {
        if (!this.canWork(false)) {
            return false;
        }
        ILatheItem l = (ILatheItem)this.latheSlot.get().getItem();
        ILatheItem.ILatheTool t = (ILatheItem.ILatheTool)this.toolSlot.get().getItem();
        if (!this.outputSlot.canAdd(l.getOutputItem(this.latheSlot.get(), position))) {
            return false;
        }
        int[] currentState = l.getCurrentState(this.latheSlot.get());
        if (currentState[position] <= 1) {
            return false;
        }
        l.setState(this.latheSlot.get(), position, currentState[position] - 1);
        if (this.worldObj.rand.nextFloat() < l.getOutputChance(this.latheSlot.get(), position)) {
            this.outputSlot.add(l.getOutputItem(this.latheSlot.get(), position));
        }
        t.setCustomDamage(this.toolSlot.get(), t.getCustomDamage(this.toolSlot.get()) + 1);
        if (t.getCustomDamage(this.toolSlot.get()) >= t.getMaxCustomDamage(this.toolSlot.get())) {
            this.toolSlot.put(null);
        }
        this.kUBuffer -= 1000;
        return true;
    }

    public boolean canWork(boolean power) {
        if (this.toolSlot.get() == null || !(this.toolSlot.get().getItem() instanceof ILatheItem.ILatheTool)) {
            return false;
        }
        if (this.latheSlot.get() == null || !(this.latheSlot.get().getItem() instanceof ILatheItem)) {
            return false;
        }
        if (this.kUBuffer < 1000 && !power) {
            return false;
        }
        ILatheItem l = (ILatheItem)this.latheSlot.get().getItem();
        ILatheItem.ILatheTool t = (ILatheItem.ILatheTool)this.toolSlot.get().getItem();
        return t.getHardness(this.toolSlot.get()) > l.getHardness(this.latheSlot.get());
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.process(event);
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return side != this.getFacing();
    }
}

