/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  buildcraft.api.statements.IStatementContainer
 *  buildcraft.api.statements.IStatementParameter
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.bcIntegration.core;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import ic2.bcIntegration.core.Trigger;
import ic2.bcIntegration.core.TriggerType;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerHeat
extends Trigger {
    public TriggerHeat(TriggerType type) {
        super(type);
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case FullHeat: {
                return "Fully Heated Up";
            }
            case NoFullHeat: {
                return "Not Fully Heated Up";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target instanceof TileEntityInduction) {
            TileEntityInduction te = (TileEntityInduction)target;
            return this.type == TriggerType.FullHeat && te.heat >= TileEntityInduction.maxHeat || this.type == TriggerType.NoFullHeat && te.heat < TileEntityInduction.maxHeat;
        }
        if (target instanceof TileEntityBlastFurnace) {
            TileEntityBlastFurnace te = (TileEntityBlastFurnace)target;
            return this.type == TriggerType.FullHeat && te.heat >= TileEntityBlastFurnace.maxHeat || this.type == TriggerType.NoFullHeat && te.heat < TileEntityBlastFurnace.maxHeat;
        }
        if (target instanceof TileEntityCentrifuge) {
            TileEntityCentrifuge te = (TileEntityCentrifuge)target;
            return this.type == TriggerType.FullHeat && te.workheat >= te.heat || this.type == TriggerType.NoFullHeat && te.workheat < te.heat;
        }
        return false;
    }
}

