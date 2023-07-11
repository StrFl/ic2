/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 */
package ic2.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(value=Side.CLIENT)
public class GuiIC2ErrorScreen
extends GuiScreen {
    private final String title;
    private final String error;

    public GuiIC2ErrorScreen(String title1, String error1) {
        this.title = title1;
        this.error = error1;
    }

    public void drawScreen(int par1, int par2, float par3) {
        String[] split;
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
        int add = 0;
        for (String s : split = this.error.split("\n")) {
            this.drawString(this.fontRendererObj, s, this.width / 2 - 180, this.height / 4 - 60 + 60 - 10 + add, 0xA0A0A0);
            add += 10;
        }
    }
}

