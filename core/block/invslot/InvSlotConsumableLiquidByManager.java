/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fluids.Fluid
 */
package ic2.core.block.invslot;

import ic2.api.recipe.ILiquidAcceptManager;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import net.minecraftforge.fluids.Fluid;

public class InvSlotConsumableLiquidByManager
extends InvSlotConsumableLiquid {
    private final ILiquidAcceptManager manager;

    public InvSlotConsumableLiquidByManager(TileEntityInventory base1, String name1, int oldStartIndex1, int count, ILiquidAcceptManager manager1) {
        super(base1, name1, oldStartIndex1, count);
        this.manager = manager1;
    }

    public InvSlotConsumableLiquidByManager(TileEntityInventory base1, String name1, int oldStartIndex1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, InvSlotConsumableLiquid.OpType opType, ILiquidAcceptManager manager1) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1, opType);
        this.manager = manager1;
    }

    @Override
    protected boolean acceptsLiquid(Fluid fluid) {
        return this.manager.acceptsFluid(fluid);
    }

    @Override
    protected Iterable<Fluid> getPossibleFluids() {
        return this.manager.getAcceptedFluids();
    }
}

