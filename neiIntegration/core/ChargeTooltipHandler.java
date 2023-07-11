/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.guihook.IContainerTooltipHandler
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.item.ItemStack
 */
package ic2.neiIntegration.core;

import codechicken.nei.guihook.IContainerTooltipHandler;
import ic2.api.item.ElectricItem;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class ChargeTooltipHandler
implements IContainerTooltipHandler {
    public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
        return currenttip;
    }

    public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
        return currenttip;
    }

    public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey, List<String> currenttip) {
        if (itemstack == null) {
            return currenttip;
        }
        String tooltip = ElectricItem.manager.getToolTip(itemstack);
        if (tooltip != null) {
            currenttip.add(tooltip);
        }
        return currenttip;
    }
}

