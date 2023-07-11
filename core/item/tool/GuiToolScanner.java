/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.item.tool.ContainerToolScanner;
import ic2.core.item.tool.HandHeldScanner;
import ic2.core.item.tool.ItemScanner;
import ic2.core.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiToolScanner
extends GuiIC2 {
    private final ContainerToolScanner container;

    public GuiToolScanner(ContainerToolScanner container1) {
        super(container1, 230);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemScanner.found"), 10, 20, 2157374);
        if (this.container.scanResults != null) {
            int count = 0;
            for (Tuple.T2<ItemStack, Integer> result : this.container.scanResults) {
                String name = ((ItemStack)result.a).getItem().getItemStackDisplayName((ItemStack)result.a);
                this.fontRendererObj.drawString(result.b + "x" + name, 10, 35 + count * 11, 5752026);
                if (++count != 10) continue;
                break;
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
    }

    @Override
    public String getName() {
        ItemStack scanner = ((HandHeldScanner)this.container.base).itemScanner;
        if (((ItemScanner)scanner.getItem()).getScannrange() == 6) {
            return StatCollector.translateToLocal((String)"ic2.itemScanner");
        }
        return StatCollector.translateToLocal((String)"ic2.itemScannerAdv");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIToolScanner.png");
    }
}

