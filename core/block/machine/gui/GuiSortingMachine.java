/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.gui;

import ic2.api.Direction;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerSortingMachine;
import ic2.core.block.machine.tileentity.TileEntitySortingMachine;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.StackUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiSortingMachine
extends GuiIC2 {
    public ContainerSortingMachine container;

    public GuiSortingMachine(ContainerSortingMachine container1) {
        super(container1, 212, 243);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (((TileEntitySortingMachine)this.container.base).defaultRoute != dir.ordinal()) {
                GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SortingMachine.whitelist"), 43, 18 + dir.ordinal() * 20, 61, 36 + dir.ordinal() * 20);
                continue;
            }
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SortingMachine.default"), 43, 18 + dir.ordinal() * 20, 61, 36 + dir.ordinal() * 20);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (StackUtil.getAdjacentInventory((TileEntity)this.container.base, Direction.fromForgeDirection(dir)) != null) {
                this.drawTexturedModalRect(this.xoffset + 60, this.yoffset + 18 + dir.ordinal() * 20, 212, 15, 18, 18);
            } else {
                this.drawTexturedModalRect(this.xoffset + 60, this.yoffset + 18 + dir.ordinal() * 20, 212, 33, 18, 18);
            }
            if (((TileEntitySortingMachine)this.container.base).defaultRoute != dir.ordinal()) {
                this.drawTexturedModalRect(this.xoffset + 43, this.yoffset + 18 + dir.ordinal() * 20, 230, 15, 18, 18);
                continue;
            }
            this.drawTexturedModalRect(this.xoffset + 43, this.yoffset + 18 + dir.ordinal() * 20, 230, 33, 18, 18);
        }
        int chargeLevel = (int)(14.0f * ((TileEntitySortingMachine)this.container.base).getChargeLevel());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 171, this.yoffset + 233 - chargeLevel, 212, 14 - chargeLevel, 14, chargeLevel);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (x < 43 || y < 18 + dir.ordinal() * 20 || x > 61 || y > 36 + dir.ordinal() * 20) continue;
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, dir.ordinal());
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.SortingMachine.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISortingMachine.png");
    }
}

