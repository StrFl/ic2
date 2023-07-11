/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.ObfuscationReflectionHelper
 *  net.minecraft.block.Block
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.network.NetHandlerPlayServer
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.ChatComponentTranslation
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.DimensionManager
 */
package ic2.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.RenderBlock;
import ic2.core.util.Util;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class Platform {
    public boolean isSimulating() {
        return !FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public boolean isRendering() {
        return !this.isSimulating();
    }

    public void displayError(String error, Object ... args) {
        if (args.length > 0) {
            error = String.format(error, args);
        }
        error = "IndustrialCraft 2 Error\n\n == = IndustrialCraft 2 Error = == \n\n" + error + "\n\n == == == == == == == == == == == == == == == =\n";
        error = error.replace("\n", System.getProperty("line.separator"));
        throw new RuntimeException(error);
    }

    public void displayError(Exception e, String error, Object ... args) {
        if (args.length > 0) {
            error = String.format(error, args);
        }
        this.displayError("An unexpected Exception occured.\n\n" + this.getStackTrace(e) + "\n" + error, new Object[0]);
    }

    public String getStackTrace(Exception e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    public EntityPlayer getPlayerInstance() {
        return null;
    }

    public World getWorld(int dimId) {
        return DimensionManager.getWorld((int)dimId);
    }

    public void registerRenderers() {
    }

    public void messagePlayer(EntityPlayer player, String message, Object ... args) {
        if (player instanceof EntityPlayerMP) {
            ChatComponentTranslation msg = args.length > 0 ? new ChatComponentTranslation(message, (Object[])this.getMessageComponents(args)) : new ChatComponentTranslation(message, new Object[0]);
            ((EntityPlayerMP)player).addChatMessage((IChatComponent)msg);
        }
    }

    public boolean launchGui(EntityPlayer player, IHasGui inventory) {
        if (!Util.isFakePlayer(player, true)) {
            int windowId;
            EntityPlayerMP entityPlayerMp = (EntityPlayerMP)player;
            entityPlayerMp.currentWindowId = windowId = entityPlayerMp.currentWindowId % 100 + 1;
            entityPlayerMp.closeContainer();
            IC2.network.get().initiateGuiDisplay(entityPlayerMp, inventory, windowId);
            player.openContainer = inventory.getGuiContainer(player);
            player.openContainer.windowId = windowId;
            player.openContainer.addCraftingToCrafters((ICrafting)entityPlayerMp);
            return true;
        }
        return false;
    }

    public boolean launchGuiClient(EntityPlayer player, IHasGui inventory, boolean isAdmin) {
        return false;
    }

    public void profilerStartSection(String section) {
    }

    public void profilerEndSection() {
    }

    public void profilerEndStartSection(String section) {
    }

    public File getMinecraftDir() {
        return new File(".");
    }

    public void playSoundSp(String sound, float f, float g) {
    }

    public void resetPlayerInAirTime(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, (Object)((EntityPlayerMP)player).playerNetServerHandler, (Object)0, (String[])new String[]{"field_147365_f", "floatingTickCount"});
    }

    public int getBlockTexture(Block block, IBlockAccess world, int x, int y, int z, int side) {
        return 0;
    }

    public int addArmor(String name) {
        return 0;
    }

    public void removePotion(EntityLivingBase entity, int potion) {
        entity.removePotionEffect(potion);
    }

    public int getRenderId(String name) {
        return -1;
    }

    public RenderBlock getRender(String name) {
        return null;
    }

    public void onPostInit() {
    }

    protected IChatComponent[] getMessageComponents(Object ... args) {
        IChatComponent[] encodedArgs = new IChatComponent[args.length];
        for (int i = 0; i < args.length; ++i) {
            encodedArgs[i] = args[i] instanceof String && ((String)args[i]).startsWith("ic2.") ? new ChatComponentTranslation((String)args[i], new Object[0]) : new ChatComponentText(args[i].toString());
        }
        return encodedArgs;
    }
}

