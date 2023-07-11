/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.DimensionManager
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.oredict.OreDictionary
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.IRecipeInput;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.GridInfo;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.DropScan;
import ic2.core.uu.UuGraph;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class CommandIc2
extends CommandBase {
    public String getCommandName() {
        return "ic2";
    }

    public String getCommandUsage(ICommandSender icommandsender) {
        return "/ic2 uu-world-scan <small|medium|large> | debug (dumpUuValues | resolveIngredient <name> | dumpTextures <name> <size> | dumpLargeGrids)";
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandIc2.getListOfStringsMatchingLastWord((String[])args, (String[])new String[]{"uu-world-scan", "debug"});
        }
        if (args.length == 2 && args[0].equals("uu-world-scan")) {
            return CommandIc2.getListOfStringsMatchingLastWord((String[])args, (String[])new String[]{"small", "medium", "large"});
        }
        if (args.length >= 2 && args[0].equals("debug")) {
            if (args.length == 2) {
                return CommandIc2.getListOfStringsMatchingLastWord((String[])args, (String[])new String[]{"dumpUuValues", "resolveIngredient", "dumpTextures", "dumpLargeGrids"});
            }
            if (args.length == 3 && args[1].equals("resolveIngredient")) {
                ArrayList<String> possibilities = new ArrayList<String>(Item.itemRegistry.getKeys());
                for (String name : OreDictionary.getOreNames()) {
                    possibilities.add("OreDict:" + name);
                }
                for (String name : FluidRegistry.getRegisteredFluids().keySet()) {
                    possibilities.add("Fluid:" + name);
                }
                return CommandIc2.getListOfStringsFromIterableMatchingLastWord((String[])args, possibilities);
            }
        }
        return null;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
        }
        if (args.length == 2 && args[0].equals("uu-world-scan")) {
            this.cmdUuWorldScan(sender, args[1]);
        } else if (args[0].equals("debug")) {
            if (args.length == 2 && args[1].equals("dumpUuValues")) {
                this.cmdDumpUuValues(sender);
            } else if (args.length == 3 && args[1].equals("resolveIngredient")) {
                this.cmdDebugResolveIngredient(sender, args[2]);
            } else if (args.length == 4 && args[1].equals("dumpTextures")) {
                this.cmdDebugDumpTextures(sender, args[2], args[3]);
            } else if (args.length == 2 && args[1].equals("dumpLargeGrids")) {
                this.dumpLargeGrids(sender);
            } else {
                throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
            }
        }
    }

    public int compareTo(Object o) {
        return super.compareTo((ICommand)o);
    }

    private void cmdUuWorldScan(ICommandSender sender, String arg) {
        int areaCount;
        if (arg.equals("small")) {
            areaCount = 1024;
        } else if (arg.equals("medium")) {
            areaCount = 2048;
        } else if (arg.equals("large")) {
            areaCount = 4096;
        } else {
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
        }
        int time = areaCount / 1024 * 4;
        sender.addChatMessage((IChatComponent)new ChatComponentText("Starting world scan, this will take about " + time + " minutes with a powerful cpu."));
        sender.addChatMessage((IChatComponent)new ChatComponentText("The server will not respond while the calculations are running."));
        Object world = null;
        world = sender instanceof EntityPlayerMP ? ((EntityPlayerMP)sender).worldObj : DimensionManager.getWorld((int)0);
        if (world == null) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("Can't determine the world to scan."));
            return;
        }
        int area = 50000;
        int range = 5;
        DropScan.start((World)world, area, areaCount, range);
    }

    private void cmdDumpUuValues(ICommandSender sender) {
        ArrayList<Map.Entry<ItemStack, Double>> list = new ArrayList<Map.Entry<ItemStack, Double>>();
        Iterator<Map.Entry<ItemStack, Double>> it = UuGraph.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        Collections.sort(list, new Comparator<Map.Entry<ItemStack, Double>>(){

            @Override
            public int compare(Map.Entry<ItemStack, Double> a, Map.Entry<ItemStack, Double> b) {
                return a.getKey().getItem().getItemStackDisplayName(a.getKey()).compareTo(b.getKey().getItem().getItemStackDisplayName(b.getKey()));
            }
        });
        sender.addChatMessage((IChatComponent)new ChatComponentText("UU Values:"));
        for (Map.Entry entry : list) {
            sender.addChatMessage((IChatComponent)new ChatComponentText(String.format("  %s: %s", ((ItemStack)entry.getKey()).getItem().getItemStackDisplayName((ItemStack)entry.getKey()), entry.getValue())));
        }
        sender.addChatMessage((IChatComponent)new ChatComponentText("(check console for full list)"));
    }

    private void cmdDebugResolveIngredient(ICommandSender sender, String arg) {
        try {
            IRecipeInput input = ConfigUtil.asRecipeInput(arg);
            if (input == null) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("No match"));
            } else {
                List<ItemStack> inputs = input.getInputs();
                sender.addChatMessage((IChatComponent)new ChatComponentText(inputs.size() + " matches:"));
                for (ItemStack stack : inputs) {
                    if (stack == null) {
                        sender.addChatMessage((IChatComponent)new ChatComponentText(" null"));
                        continue;
                    }
                    sender.addChatMessage((IChatComponent)new ChatComponentText(String.format(" %s (%s, od: %s, name: %s / %s)", StackUtil.toStringSafe(stack), GameData.getItemRegistry().getNameForObject((Object)stack.getItem()), this.getOreDictNames(stack), stack.getUnlocalizedName(), stack.getDisplayName())));
                }
            }
        }
        catch (Exception e) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("Error: " + e));
        }
    }

    private String getOreDictNames(ItemStack stack) {
        String ret = "";
        for (int oreId : OreDictionary.getOreIDs((ItemStack)stack)) {
            if (!ret.isEmpty()) {
                ret = ret + ", ";
            }
            ret = ret + OreDictionary.getOreName((int)oreId);
        }
        return ret.isEmpty() ? "<none>" : ret;
    }

    private void cmdDebugDumpTextures(ICommandSender sender, String arg1, String arg2) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("Can't dump textures on the dedicated server."));
            return;
        }
        sender.addChatMessage((IChatComponent)new ChatComponentText("Dumping requested textures to sprites texture..."));
        Integer meta = null;
        int pos = arg1.indexOf(64);
        if (pos != -1) {
            meta = Integer.valueOf(arg1.substring(pos + 1));
            arg1 = arg1.substring(0, pos);
        }
        String regex = "^" + Pattern.quote(arg1).replace("*", "\\E.*\\Q") + "$";
        Pattern pattern = Pattern.compile(regex);
        IC2.tickHandler.addSingleTickCallback(IC2.platform.getPlayerInstance().worldObj, new TextureDumper(pattern, Integer.valueOf(arg2), meta));
    }

    private void dumpLargeGrids(ICommandSender sender) {
        ArrayList<GridInfo> allGrids = new ArrayList<GridInfo>();
        for (WorldServer world : DimensionManager.getWorlds()) {
            EnergyNetLocal energyNet = WorldData.get((World)world).energyNet;
            allGrids.addAll(energyNet.getGridInfos());
        }
        Collections.sort(allGrids, new Comparator<GridInfo>(){

            @Override
            public int compare(GridInfo a, GridInfo b) {
                return b.complexNodeCount - a.complexNodeCount;
            }
        });
        sender.addChatMessage((IChatComponent)new ChatComponentText("found " + allGrids.size() + " grids overall"));
        for (int i = 0; i < 8 && i < allGrids.size(); ++i) {
            GridInfo grid = (GridInfo)allGrids.get(i);
            if (grid.nodeCount == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("grid " + grid.id + " is empty"));
                continue;
            }
            sender.addChatMessage((IChatComponent)new ChatComponentText(String.format("%d complex / %d total nodes in grid %d (%d/%d/%d - %d/%d/%d)", grid.complexNodeCount, grid.nodeCount, grid.id, grid.minX, grid.minY, grid.minZ, grid.maxX, grid.maxY, grid.maxZ)));
        }
    }

    public static class TextureDumper
    implements ITickCallback {
        private final Pattern pattern;
        private final int size;
        private final Integer meta;

        TextureDumper(Pattern pattern, int size, Integer meta) {
            this.pattern = pattern;
            this.size = size;
            this.meta = meta;
        }

        @Override
        public void tickCallback(World world) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }

        @SubscribeEvent
        @SideOnly(value=Side.CLIENT)
        public void onRenderWorldLast(RenderWorldLastEvent event) {
            IC2.log.info(LogCategory.General, "Starting texture dump.");
            int count = 0;
            block2: for (Item item : GameData.getItemRegistry().typeSafeIterable()) {
                String regName = GameData.getItemRegistry().getNameForObject((Object)item);
                if (!this.pattern.matcher(regName).matches()) continue;
                if (this.meta == null) {
                    HashSet<String> processedNames = new HashSet<String>();
                    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                        ItemStack stack;
                        block6: {
                            stack = new ItemStack(item, 1, i);
                            try {
                                String name = stack.getUnlocalizedName();
                                if (name == null) continue block2;
                                if (!processedNames.add(name)) {
                                }
                                break block6;
                            }
                            catch (Exception e) {
                                IC2.log.info(LogCategory.General, e, "Exception for %s.", stack);
                            }
                            continue block2;
                        }
                        this.dump(stack, regName);
                        ++count;
                    }
                    continue;
                }
                this.dump(new ItemStack(item, 1, this.meta.intValue()), regName);
                ++count;
            }
            IC2.log.info(LogCategory.General, "Dumped %d sprites.", count);
            MinecraftForge.EVENT_BUS.unregister((Object)this);
        }

        @SideOnly(value=Side.CLIENT)
        void dump(ItemStack stack, String name) {
            Minecraft mc = Minecraft.getMinecraft();
            GL11.glClear((int)16640);
            GL11.glMatrixMode((int)5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho((double)0.0, (double)mc.displayWidth, (double)mc.displayHeight, (double)0.0, (double)1000.0, (double)3000.0);
            GL11.glMatrixMode((int)5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-2000.0f);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glEnable((int)32826);
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
            float scale = (float)this.size / 16.0f;
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            RenderItem.getInstance().renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, 0, 0);
            BufferedImage img = new BufferedImage(this.size, this.size, 2);
            if (OpenGlHelper.isFramebufferEnabled()) {
                int width = Minecraft.getMinecraft().getFramebuffer().framebufferTextureWidth;
                int height = Minecraft.getMinecraft().getFramebuffer().framebufferTextureHeight;
                IntBuffer buffer = BufferUtils.createIntBuffer((int)(width * height));
                int[] data = new int[width * height];
                GL11.glBindTexture((int)3553, (int)Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
                GL11.glGetTexImage((int)3553, (int)0, (int)32993, (int)33639, (IntBuffer)buffer);
                buffer.get(data);
                int[] mirroredData = new int[data.length];
                for (int y = 0; y < height; ++y) {
                    System.arraycopy(data, y * width, mirroredData, (height - y - 1) * width, width);
                }
                img.setRGB(0, 0, this.size, this.size, mirroredData, 0, width);
            } else {
                IntBuffer buffer = BufferUtils.createIntBuffer((int)(this.size * this.size));
                int[] data = new int[this.size * this.size];
                GL11.glReadPixels((int)0, (int)0, (int)this.size, (int)this.size, (int)32993, (int)33639, (IntBuffer)buffer);
                buffer.get(data);
                img.setRGB(0, 0, this.size, this.size, data, 0, this.size);
            }
            try {
                File dir = new File(IC2.platform.getMinecraftDir(), "sprites");
                dir.mkdir();
                String modId = name.indexOf(58) >= 0 ? name.substring(0, name.indexOf(58)) : name;
                String fileName = "Sprite_" + modId + "_" + stack.getDisplayName() + "_" + this.size;
                fileName = fileName.replaceAll("[^\\w\\- ]+", "");
                ImageIO.write((RenderedImage)img, "png", new File(dir, fileName + ".png"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            GL11.glPopMatrix();
            GL11.glMatrixMode((int)5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode((int)5888);
        }
    }
}

