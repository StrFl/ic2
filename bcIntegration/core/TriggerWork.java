/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  buildcraft.api.statements.IStatementContainer
 *  buildcraft.api.statements.IStatementParameter
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.bcIntegration.core;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.bcIntegration.core.Trigger;
import ic2.bcIntegration.core.TriggerType;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerWork
extends Trigger {
    public TriggerWork(TriggerType type) {
        super(type);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        switch (this.type) {
            case Working: {
                this.icon = iconRegister.registerIcon("buildcraft:triggers/trigger_machine_active");
                break;
            }
            case NotWorking: {
                this.icon = iconRegister.registerIcon("buildcraft:triggers/trigger_machine_inactive");
                break;
            }
            default: {
                return;
            }
        }
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case Working: {
                return "Machine On";
            }
            case NotWorking: {
                return "Machine Off";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target instanceof TileEntityStandardMachine) {
            TileEntityStandardMachine te = (TileEntityStandardMachine)target;
            boolean active = te.getActive();
            return this.type == TriggerType.Working && active || this.type == TriggerType.NotWorking && !active;
        }
        if (target instanceof TileEntityLathe) {
            boolean active = ((TileEntityLathe)target).canWork(true);
            return this.type == TriggerType.Working && active || this.type == TriggerType.NotWorking && !active;
        }
        return false;
    }
}

