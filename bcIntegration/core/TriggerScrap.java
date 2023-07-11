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
import ic2.core.block.machine.tileentity.TileEntityMatter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerScrap
extends Trigger {
    public TriggerScrap(TriggerType type) {
        super(type);
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case HasScrap: {
                return "Has Amplifier";
            }
            case NoScrap: {
                return "No Amplifier";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target instanceof TileEntityMatter) {
            TileEntityMatter te = (TileEntityMatter)target;
            boolean available = te.amplificationIsAvailable();
            return this.type == TriggerType.HasScrap && available || this.type == TriggerType.NoScrap && !available;
        }
        return false;
    }
}

