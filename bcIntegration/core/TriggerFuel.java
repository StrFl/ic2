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
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerFuel
extends Trigger {
    public TriggerFuel(TriggerType type) {
        super(type);
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case HasFuel: {
                return "Has Fuel";
            }
            case NoFuel: {
                return "No Fuel";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target instanceof TileEntityBaseGenerator) {
            TileEntityBaseGenerator te = (TileEntityBaseGenerator)target;
            return this.type == TriggerType.HasFuel && te.fuel > 0 || this.type == TriggerType.NoFuel && te.fuel <= 0;
        }
        if (target instanceof TileEntityGeoGenerator) {
            TileEntityGeoGenerator te = (TileEntityGeoGenerator)target;
            return this.type == TriggerType.HasFuel && te.getTankAmount() > 0 || this.type == TriggerType.NoFuel && te.getTankAmount() <= 0;
        }
        if (target instanceof TileEntitySolidHeatGenerator) {
            TileEntitySolidHeatGenerator te = (TileEntitySolidHeatGenerator)target;
            return this.type == TriggerType.HasFuel && te.fuel > 0 || this.type == TriggerType.NoFuel && te.fuel <= 0;
        }
        if (target instanceof TileEntityFluidHeatGenerator) {
            TileEntityFluidHeatGenerator te = (TileEntityFluidHeatGenerator)target;
            return this.type == TriggerType.HasFuel && te.getTankAmount() > 0 || this.type == TriggerType.NoFuel && te.getTankAmount() <= 0;
        }
        if (target instanceof TileEntitySemifluidGenerator) {
            TileEntitySemifluidGenerator te = (TileEntitySemifluidGenerator)target;
            return this.type == TriggerType.HasFuel && te.getTankAmount() > 0 || this.type == TriggerType.NoFuel && te.getTankAmount() <= 0;
        }
        return false;
    }
}

