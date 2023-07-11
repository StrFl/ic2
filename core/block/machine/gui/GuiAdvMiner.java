/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityAdvMiner;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiAdvMiner
extends GuiIC2 {
    public ContainerAdvMiner container;

    public GuiAdvMiner(ContainerAdvMiner container1) {
        super(container1, 203);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.AdvMiner.gui.info.minelevel", (Object[])new Object[]{((TileEntityAdvMiner)this.container.base).xcounter, ((TileEntityAdvMiner)this.container.base).zcounter, ((TileEntityAdvMiner)this.container.base).getminelayer()}), 28, 105, 2157374);
        if (((TileEntityAdvMiner)this.container.base).blacklist) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.AdvMiner.gui.mode.blacklist"), 40, 31, 2157374);
        } else {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.AdvMiner.gui.mode.whitelist"), 40, 31, 2157374);
        }
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, StatCollector.translateToLocal((String)"ic2.AdvMiner.gui.switch.reset"), 134, 102, 167, 114);
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, StatCollector.translateToLocal((String)"ic2.AdvMiner.gui.switch.mode"), 124, 28, 138, 40);
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.AdvMiner.gui.switch.silktouch", (Object[])new Object[]{((TileEntityAdvMiner)this.container.base).silktouch}), 129, 45, 149, 59);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        if (x >= 134 && y >= 102 && x <= 167 && y <= 114) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 0);
        }
        if (x >= 124 && y >= 28 && x <= 139 && y <= 40) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 1);
        }
        if (x >= 129 && y >= 45 && x <= 146 && y <= 59) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 2);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityAdvMiner)this.container.base).getChargeLevel());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 9, this.yoffset + 67 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.AdvMiner.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIAdvMiner.png");
    }
}

