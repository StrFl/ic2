/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  buildcraft.api.statements.IStatement
 *  buildcraft.api.statements.IStatementContainer
 *  buildcraft.api.statements.IStatementParameter
 *  buildcraft.api.statements.ITriggerExternal
 *  buildcraft.api.statements.StatementManager
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.bcIntegration.core;

import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.StatementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.bcIntegration.core.TriggerType;
import ic2.core.IC2;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class Trigger
implements ITriggerExternal {
    protected final TriggerType type;
    @SideOnly(value=Side.CLIENT)
    protected IIcon icon;

    public Trigger(TriggerType type) {
        this.type = type;
        StatementManager.registerStatement((IStatement)this);
    }

    public String getUniqueTag() {
        return "IC2_" + this.type.name();
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon() {
        return this.icon;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.icon = iconRegister.registerIcon(IC2.textureDomain + ":bcTrigger/" + this.type.name());
    }

    public abstract String getDescription();

    public abstract boolean isTriggerActive(TileEntity var1, ForgeDirection var2, IStatementContainer var3, IStatementParameter[] var4);

    public int maxParameters() {
        return 0;
    }

    public int minParameters() {
        return 0;
    }

    public IStatementParameter createParameter(int index) {
        return null;
    }

    public IStatement rotateLeft() {
        return this;
    }
}

