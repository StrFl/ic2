/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.reactor.container;

import ic2.core.ContainerBase;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerNuclearReactor
extends ContainerBase<TileEntityNuclearReactorElectric> {
    public short size;

    public ContainerNuclearReactor(EntityPlayer entityPlayer, TileEntityNuclearReactorElectric tileEntity1) {
        super(tileEntity1);
        this.size = tileEntity1.getReactorSize();
        int startX = 26;
        int startY = 25;
        for (int i = 0; i < tileEntity1.reactorSlot.size(); ++i) {
            int x = i % this.size;
            int y = i / this.size;
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.reactorSlot, i, startX + 18 * x, startY + 18 * y));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot((IInventory)entityPlayer.inventory, col + row * 9 + 9, 26 + col * 18, 161 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot((IInventory)entityPlayer.inventory, col, 26 + col * 18, 219));
        }
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.coolantinputSlot, 0, 8, 25));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.hotcoolinputSlot, 0, 188, 25));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.coolantoutputSlot, 0, 8, 115));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.hotcoolantoutputSlot, 0, 188, 115));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("heat");
        ret.add("maxHeat");
        ret.add("EmitHeat");
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("fluidcoolreactor");
        return ret;
    }
}

