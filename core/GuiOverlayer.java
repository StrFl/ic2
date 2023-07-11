/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.EventPriority
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.item.ElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.armor.ItemArmorBatpack;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.item.armor.ItemArmorEnergypack;
import ic2.core.item.armor.ItemArmorJetpack;
import ic2.core.item.armor.ItemArmorJetpackElectric;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class GuiOverlayer
extends Gui {
    private final Minecraft mc;
    private int chargeproz;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIOverlay.png");

    public GuiOverlayer(Minecraft mc1) {
        this.mc = mc1;
    }

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void onRenderHotBar(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        ItemStack boots = this.mc.thePlayer.getEquipmentInSlot(1);
        ItemStack legs = this.mc.thePlayer.getEquipmentInSlot(2);
        ItemStack chestplate = this.mc.thePlayer.getEquipmentInSlot(3);
        ItemStack helm = this.mc.thePlayer.getEquipmentInSlot(4);
        if (helm != null && (helm.getItem() == Ic2Items.quantumHelmet.getItem() || helm.getItem() == Ic2Items.nanoHelmet.getItem())) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(helm);
            Short HudMode = nbtData.getShort("HudMode");
            boolean Nightvision = nbtData.getBoolean("Nightvision");
            if (HudMode > 0) {
                ItemStack item;
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glDisable((int)2896);
                RenderItem renderItem = new RenderItem();
                RenderHelper.enableGUIStandardItemLighting();
                this.mc.getTextureManager().bindTexture(background);
                this.drawTexturedModalRect(0, 0, 0, 0, 71, 69);
                if (helm.getItem() == Ic2Items.quantumHelmet.getItem()) {
                    this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(helm), ((ItemArmorQuantumSuit)helm.getItem()).getMaxCharge(helm), 100.0);
                    renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.quantumHelmet, 5, 4);
                }
                if (helm.getItem() == Ic2Items.nanoHelmet.getItem()) {
                    this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(helm), ((ItemArmorNanoSuit)helm.getItem()).getMaxCharge(helm), 100.0);
                    renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.nanoHelmet, 5, 4);
                }
                this.mc.fontRenderer.drawString(this.chargeproz + "%", 25, 9, 0xFFFFFF);
                if (Nightvision) {
                    renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.nightvisionGoggles, 50, 4);
                }
                if (chestplate != null) {
                    NBTTagCompound nbtDatachestplate = StackUtil.getOrCreateNbtData(chestplate);
                    boolean jetpack = nbtDatachestplate.getBoolean("jetpack");
                    boolean hoverMode = nbtDatachestplate.getBoolean("hoverMode");
                    if (chestplate.getItem() == Ic2Items.quantumBodyarmor.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(chestplate), ((ItemArmorQuantumSuit)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.quantumBodyarmor, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.nanoBodyarmor.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(chestplate), ((ItemArmorNanoSuit)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.nanoBodyarmor, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.electricJetpack.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(chestplate), ((ItemArmorJetpackElectric)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.electricJetpack, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.jetpack.getItem()) {
                        this.chargeproz = (int)Util.map(((ItemArmorJetpack)chestplate.getItem()).getCharge(chestplate), ((ItemArmorJetpack)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.jetpack, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.batPack.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(chestplate), ((ItemArmorBatpack)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.batPack, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.energyPack.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(chestplate), ((ItemArmorEnergypack)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.energyPack, 5, 20);
                    }
                    if (chestplate.getItem() == Ic2Items.cfPack.getItem()) {
                        this.chargeproz = (((ItemArmorCFPack)chestplate.getItem()).getMaxDamage(chestplate) - ((ItemArmorCFPack)chestplate.getItem()).getDamage(chestplate)) * 100 / ((ItemArmorCFPack)chestplate.getItem()).getMaxDamage(chestplate);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.cfPack, 5, 20);
                    }
                    this.mc.fontRenderer.drawString(this.chargeproz + "%", 25, 25, 0xFFFFFF);
                    if (jetpack && !hoverMode) {
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.jetpack, 50, 20);
                    }
                    if (jetpack && hoverMode) {
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.electricJetpack, 50, 20);
                    }
                }
                if (legs != null) {
                    if (legs.getItem() == Ic2Items.quantumLeggings.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(legs), ((ItemArmorQuantumSuit)legs.getItem()).getMaxCharge(legs), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.quantumLeggings, 5, 36);
                    }
                    if (legs.getItem() == Ic2Items.nanoLeggings.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(legs), ((ItemArmorNanoSuit)legs.getItem()).getMaxCharge(legs), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.nanoLeggings, 5, 36);
                    }
                    this.mc.fontRenderer.drawString(this.chargeproz + "%", 25, 41, 0xFFFFFF);
                }
                if (boots != null) {
                    if (boots.getItem() == Ic2Items.quantumBoots.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(boots), ((ItemArmorQuantumSuit)boots.getItem()).getMaxCharge(boots), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.quantumBoots, 5, 52);
                    }
                    if (boots.getItem() == Ic2Items.nanoBoots.getItem()) {
                        this.chargeproz = (int)Util.map(ElectricItem.manager.getCharge(boots), ((ItemArmorNanoSuit)boots.getItem()).getMaxCharge(boots), 100.0);
                        renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.nanoBoots, 5, 52);
                    }
                    this.mc.fontRenderer.drawString(this.chargeproz + "%", 25, 56, 0xFFFFFF);
                }
                if (HudMode == 2 && (item = this.mc.thePlayer.getCurrentEquippedItem()) != null) {
                    int l;
                    renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, item, 5, 74);
                    this.mc.fontRenderer.drawString(item.getDisplayName(), 30, 78, 0xFFFFFF);
                    LinkedList<String> info = new LinkedList<String>();
                    if (item.getItem() instanceof IItemHudInfo) {
                        info.addAll(((IItemHudInfo)item.getItem()).getHudInfo(item));
                        if (info.size() > 0) {
                            for (l = 0; l <= info.size() - 1; ++l) {
                                this.mc.fontRenderer.drawString(((String)info.get(l)).toString(), 8, 83 + (l + 1) * 14, 0xFFFFFF);
                            }
                        }
                    } else {
                        info.addAll(item.getTooltip((EntityPlayer)this.mc.thePlayer, true));
                        if (info.size() > 1) {
                            for (l = 1; l <= info.size() - 1; ++l) {
                                this.mc.fontRenderer.drawString(((String)info.get(l)).toString(), 8, 83 + l * 14, 0xFFFFFF);
                            }
                        }
                    }
                }
            }
            RenderHelper.disableStandardItemLighting();
        }
    }
}

