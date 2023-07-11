/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.FMLLog
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderSnowball
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.ChatComponentTranslation
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 *  net.minecraftforge.common.MinecraftForge
 *  org.lwjgl.opengl.Display
 */
package ic2.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiOverlayer;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.Platform;
import ic2.core.audio.PositionSpec;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.KineticGeneratorRenderer;
import ic2.core.block.OverlayTesr;
import ic2.core.block.RenderBlock;
import ic2.core.block.RenderBlockCrop;
import ic2.core.block.RenderBlockDefault;
import ic2.core.block.RenderBlockFence;
import ic2.core.block.RenderBlockWall;
import ic2.core.block.RenderExplosiveBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.block.personal.RenderBlockPersonal;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityPersonalChestRenderer;
import ic2.core.block.wiring.RenderBlockCable;
import ic2.core.block.wiring.RenderBlockLuminator;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.RenderIC2Boat;
import ic2.core.item.RenderLiquidCell;
import ic2.core.item.resources.LatheRenderer;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.EntityParticle;
import ic2.core.item.tool.RenderBillboardEntity;
import ic2.core.item.tool.RenderCrossed;
import ic2.core.network.RpcHandler;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;

@SideOnly(value=Side.CLIENT)
public class PlatformClient
extends Platform {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, RenderBlock> renders = new HashMap<String, RenderBlock>();

    public PlatformClient() {
        this.addBlockRenderer("default", new RenderBlockDefault());
        this.addBlockRenderer("cable", new RenderBlockCable());
        this.addBlockRenderer("crop", new RenderBlockCrop());
        this.addBlockRenderer("fence", new RenderBlockFence());
        this.addBlockRenderer("luminator", new RenderBlockLuminator());
        this.addBlockRenderer("personal", new RenderBlockPersonal());
        this.addBlockRenderer("wall", new RenderBlockWall());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlock.class, (TileEntitySpecialRenderer)new OverlayTesr());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPersonalChest.class, (TileEntitySpecialRenderer)new TileEntityPersonalChestRenderer());
        KineticGeneratorRenderer kineticRenderer = new KineticGeneratorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindKineticGenerator.class, (TileEntitySpecialRenderer)kineticRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterKineticGenerator.class, (TileEntitySpecialRenderer)kineticRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityIC2Explosive.class, (Render)new RenderExplosiveBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityMiningLaser.class, (Render)new RenderCrossed(new ResourceLocation(IC2.textureDomain, "textures/models/laser.png")));
        RenderingRegistry.registerEntityRenderingHandler(EntityIC2Boat.class, (Render)new RenderIC2Boat());
        RenderingRegistry.registerEntityRenderingHandler(EntityParticle.class, (Render)new RenderBillboardEntity());
    }

    @Override
    public void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer((Item)Ic2Items.FluidCell.getItem(), (IItemRenderer)new RenderLiquidCell());
        LatheRenderer latheItemRenderer = new LatheRenderer();
        MinecraftForgeClient.registerItemRenderer((Item)Ic2Items.turningBlankIron.getItem(), (IItemRenderer)latheItemRenderer);
        MinecraftForgeClient.registerItemRenderer((Item)Ic2Items.turningBlankWood.getItem(), (IItemRenderer)latheItemRenderer);
    }

    @Override
    public void displayError(String error, Object ... args) {
        if (args.length > 0) {
            error = String.format(error, args);
        }
        error = "IndustrialCraft 2 Error\n\n" + error;
        String dialogError = error.replaceAll("([^\n]{80,}?) ", "$1\n");
        error = error.replace("\n", System.getProperty("line.separator"));
        dialogError = dialogError.replace("\n", System.getProperty("line.separator"));
        FMLLog.severe((String)"%s", (Object[])new Object[]{error});
        Minecraft.getMinecraft().setIngameNotInFocus();
        try {
            Display.destroy();
            JFrame frame = new JFrame("IndustrialCraft 2 Error");
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(frame, dialogError, "IndustrialCraft 2 Error", 0);
        }
        catch (Throwable t) {
            IC2.log.error(LogCategory.General, t, "Exception caught while showing an error.");
        }
        Util.exit(1);
    }

    @Override
    public EntityPlayer getPlayerInstance() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getWorld(int dimId) {
        if (this.isSimulating()) {
            return super.getWorld(dimId);
        }
        WorldClient world = Minecraft.getMinecraft().theWorld;
        return world.provider.dimensionId == dimId ? world : null;
    }

    @Override
    public void messagePlayer(EntityPlayer player, String message, Object ... args) {
        if (args.length > 0) {
            this.mc.ingameGUI.getChatGUI().printChatMessage((IChatComponent)new ChatComponentTranslation(message, (Object[])this.getMessageComponents(args)));
        } else {
            this.mc.ingameGUI.getChatGUI().printChatMessage((IChatComponent)new ChatComponentText(message));
        }
    }

    @Override
    public boolean launchGuiClient(EntityPlayer entityPlayer, IHasGui inventory, boolean isAdmin) {
        FMLClientHandler.instance().displayGuiScreen(entityPlayer, inventory.getGui(entityPlayer, isAdmin));
        return true;
    }

    @Override
    public void profilerStartSection(String section) {
        if (this.isRendering()) {
            Minecraft.getMinecraft().mcProfiler.startSection(section);
        } else {
            super.profilerStartSection(section);
        }
    }

    @Override
    public void profilerEndSection() {
        if (this.isRendering()) {
            Minecraft.getMinecraft().mcProfiler.endSection();
        } else {
            super.profilerEndSection();
        }
    }

    @Override
    public void profilerEndStartSection(String section) {
        if (this.isRendering()) {
            Minecraft.getMinecraft().mcProfiler.endStartSection(section);
        } else {
            super.profilerEndStartSection(section);
        }
    }

    @Override
    public File getMinecraftDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public void playSoundSp(String sound, float f, float g) {
        IC2.audioManager.playOnce(this.getPlayerInstance(), PositionSpec.Hand, sound, true, IC2.audioManager.getDefaultVolume());
    }

    @Override
    public int addArmor(String name) {
        return RenderingRegistry.addNewArmourRendererPrefix((String)name);
    }

    @Override
    public int getRenderId(String name) {
        return this.renders.get(name).getRenderId();
    }

    @Override
    public RenderBlock getRender(String name) {
        return this.renders.get(name);
    }

    @Override
    public void onPostInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, (Render)new RenderSnowball(Ic2Items.dynamite.getItem()));
        MinecraftForge.EVENT_BUS.register((Object)new GuiOverlayer(Minecraft.getMinecraft()));
        new RpcHandler();
    }

    private void addBlockRenderer(String name, RenderBlock renderer) {
        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler)renderer);
        this.renders.put(name, renderer);
    }
}

