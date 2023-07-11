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
import ic2.core.block.wiring.TileEntityCableDetector;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerEnergyFlow
extends Trigger {
    public TriggerEnergyFlow(TriggerType type) {
        super(type);
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case EnergyFlowing: {
                return "Energy flowing";
            }
            case EnergyNotFlowing: {
                return "Energy not flowing";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target instanceof TileEntityCableDetector) {
            TileEntityCableDetector te = (TileEntityCableDetector)target;
            boolean active = te.getActive();
            return this.type == TriggerType.EnergyFlowing && active || this.type == TriggerType.EnergyNotFlowing && !active;
        }
        return false;
    }
}

