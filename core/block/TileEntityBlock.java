/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package ic2.core.block;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.block.BlockTextureStitched;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.util.LogCategory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class TileEntityBlock
extends TileEntity
implements INetworkDataProvider,
INetworkUpdateListener,
IWrenchable {
    private static final List<TileEntityComponent> emptyComponents = Arrays.asList(new TileEntityComponent[0]);
    private static final List<Map.Entry<String, TileEntityComponent>> emptyNamedComponents = Arrays.asList(new Map.Entry[0]);
    private Map<String, TileEntityComponent> components;
    private List<TileEntityComponent> updatableComponents;
    private boolean active = false;
    private short facing = 0;
    public boolean prevActive = false;
    public short prevFacing = 0;
    private boolean loaded = false;
    private boolean enableWorldTick;
    private static final Map<Class<?>, TickSubscription> tickSubscriptions = new HashMap();
    @SideOnly(value=Side.CLIENT)
    private IIcon[] lastRenderIcons;
    private int tesrMask;
    public int tesrTtl;
    private static final int defaultTesrTtl = 500;

    public final void validate() {
        super.validate();
        IC2.tickHandler.addSingleTickCallback(this.worldObj, new ITickCallback(){

            @Override
            public void tickCallback(World world) {
                if (TileEntityBlock.this.isInvalid() || !world.blockExists(TileEntityBlock.this.xCoord, TileEntityBlock.this.yCoord, TileEntityBlock.this.zCoord)) {
                    return;
                }
                TileEntityBlock.this.onLoaded();
                if (!TileEntityBlock.this.isInvalid() && (TileEntityBlock.this.enableWorldTick || TileEntityBlock.this.updatableComponents != null)) {
                    world.loadedTileEntityList.add(TileEntityBlock.this);
                }
            }
        });
    }

    public final void invalidate() {
        this.onUnloaded();
        super.invalidate();
    }

    public final void onChunkUnload() {
        this.onUnloaded();
        super.onChunkUnload();
    }

    public void onLoaded() {
        this.loaded = true;
        this.enableWorldTick = this.requiresWorldTick();
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onLoaded();
                if (!component.enableWorldTick()) continue;
                if (this.updatableComponents == null) {
                    this.updatableComponents = new ArrayList<TileEntityComponent>(4);
                }
                this.updatableComponents.add(component);
            }
        }
    }

    public void onUnloaded() {
        if (!this.loaded) {
            return;
        }
        this.loaded = false;
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onUnloaded();
            }
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.prevFacing = this.facing = nbt.getShort("facing");
        this.prevActive = this.active = nbt.getBoolean("active");
        if (this.components != null && nbt.hasKey("components", 10)) {
            NBTTagCompound componentsNbt = nbt.getCompoundTag("components");
            for (String name : componentsNbt.func_150296_c()) {
                NBTTagCompound componentNbt = componentsNbt.getCompoundTag(name);
                TileEntityComponent component = this.components.get(name);
                if (component == null) {
                    IC2.log.warn(LogCategory.Block, "Can't find component {} while loading {}.", name, this);
                    continue;
                }
                component.readFromNbt(componentNbt);
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("facing", this.facing);
        nbt.setBoolean("active", this.active);
        NBTTagCompound componentsNbt = null;
        if (this.components != null) {
            for (Map.Entry<String, TileEntityComponent> entry : this.components.entrySet()) {
                NBTTagCompound componentNbt = entry.getValue().writeToNbt();
                if (componentNbt == null) continue;
                if (componentsNbt == null) {
                    componentsNbt = new NBTTagCompound();
                    nbt.setTag("components", (NBTBase)componentsNbt);
                }
                componentsNbt.setTag(entry.getKey(), (NBTBase)componentNbt);
            }
        }
    }

    public final boolean canUpdate() {
        return false;
    }

    public final void updateEntity() {
        if (this.updatableComponents != null) {
            for (TileEntityComponent component : this.updatableComponents) {
                component.onWorldTick();
            }
        }
        if (this.enableWorldTick) {
            if (this.worldObj.isRemote) {
                this.updateEntityClient();
            } else {
                this.updateEntityServer();
            }
        }
    }

    protected void updateEntityClient() {
    }

    protected void updateEntityServer() {
    }

    @SideOnly(value=Side.CLIENT)
    public void onRender() {
        Block block = this.getBlockType();
        if (this.lastRenderIcons == null) {
            this.lastRenderIcons = new IIcon[6];
        }
        for (int side = 0; side < 6; ++side) {
            this.lastRenderIcons[side] = block.getIcon((IBlockAccess)this.worldObj, this.xCoord, this.yCoord, this.zCoord, side);
        }
        this.tesrMask = 0;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active1) {
        this.active = active1;
        if (this.prevActive != active1) {
            IC2.network.get().updateTileEntityField(this, "active");
        }
        this.prevActive = active1;
    }

    @Override
    public short getFacing() {
        return this.facing;
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(2);
        ret.add("active");
        ret.add("facing");
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") && this.prevActive != this.active || field.equals("facing") && this.prevFacing != this.facing) {
            int reRenderMask = 0;
            Block block = this.getBlockType();
            if (this.lastRenderIcons == null) {
                reRenderMask = -1;
            } else {
                for (int side = 0; side < 6; ++side) {
                    IIcon newIcon;
                    IIcon oldIcon = this.lastRenderIcons[side];
                    if (oldIcon instanceof BlockTextureStitched) {
                        oldIcon = ((BlockTextureStitched)oldIcon).getRealTexture();
                    }
                    if ((newIcon = block.getIcon((IBlockAccess)this.worldObj, this.xCoord, this.yCoord, this.zCoord, side)) instanceof BlockTextureStitched) {
                        newIcon = ((BlockTextureStitched)newIcon).getRealTexture();
                    }
                    if (oldIcon == newIcon) continue;
                    reRenderMask |= 1 << side;
                }
            }
            if (reRenderMask != 0) {
                if (reRenderMask < 0 || this.prevFacing != this.facing || block.getRenderType() != IC2.platform.getRenderId("default")) {
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                } else {
                    this.tesrMask = reRenderMask;
                    this.tesrTtl = 500;
                }
            }
            this.prevActive = this.active;
            this.prevFacing = this.facing;
        }
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return false;
    }

    @Override
    public void setFacing(short facing1) {
        this.facing = facing1;
        if (this.prevFacing != facing1) {
            IC2.network.get().updateTileEntityField(this, "facing");
        }
        this.prevFacing = facing1;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public float getWrenchDropRate() {
        return 1.0f;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return new ItemStack(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), 1, this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
    }

    public boolean shouldRenderInPass(int pass) {
        return this.tesrMask != 0 && pass == 0;
    }

    public final int getTesrMask() {
        return this.tesrMask;
    }

    public void onBlockBreak(Block block, int meta) {
    }

    public String getTextureFolder() {
        return null;
    }

    public boolean onBlockActivated(EntityPlayer player, float xOffset, float yOffset, float zOffset, MutableBoolean result) {
        return false;
    }

    public void randomDisplayTick(Random random) {
    }

    public void adjustDrops(List<ItemStack> drops, int fortune) {
    }

    protected final <T extends TileEntityComponent> T addComponent(T component) {
        return this.addComponent(component.getDefaultName(), component);
    }

    protected final <T extends TileEntityComponent> T addComponent(String name, T component) {
        TileEntityComponent prev;
        if (this.components == null) {
            this.components = new HashMap<String, TileEntityComponent>(4);
        }
        if ((prev = this.components.put(name, component)) != null) {
            throw new RuntimeException("ambiguous component name " + name + " when adding " + component + ", already used by " + prev + ".");
        }
        return component;
    }

    public TileEntityComponent getComponent(String name) {
        if (this.components == null) {
            return null;
        }
        return this.components.get(name);
    }

    public final Iterable<TileEntityComponent> getComponents() {
        if (this.components == null) {
            return emptyComponents;
        }
        return this.components.values();
    }

    public final Iterable<Map.Entry<String, TileEntityComponent>> getNamedComponents() {
        if (this.components == null) {
            return emptyNamedComponents;
        }
        return this.components.entrySet();
    }

    public void onNeighborUpdate(Block srcBlock) {
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onNeighborUpdate(srcBlock);
            }
        }
    }

    private final boolean requiresWorldTick() {
        Class<?> cls = this.getClass();
        TickSubscription subscription = tickSubscriptions.get(cls);
        if (subscription == null) {
            boolean hasUpdateClient = false;
            boolean hasUpdateServer = false;
            boolean isClient = FMLCommonHandler.instance().getSide().isClient();
            while (cls != TileEntityBlock.class && (!hasUpdateClient && isClient || !hasUpdateServer)) {
                boolean found;
                if (!hasUpdateClient && isClient) {
                    found = true;
                    try {
                        cls.getDeclaredMethod("updateEntityClient", new Class[0]);
                    }
                    catch (NoSuchMethodException e) {
                        found = false;
                    }
                    if (found) {
                        hasUpdateClient = true;
                    }
                }
                if (!hasUpdateServer) {
                    found = true;
                    try {
                        cls.getDeclaredMethod("updateEntityServer", new Class[0]);
                    }
                    catch (NoSuchMethodException e) {
                        found = false;
                    }
                    if (found) {
                        hasUpdateServer = true;
                    }
                }
                cls = cls.getSuperclass();
            }
            subscription = hasUpdateClient ? (hasUpdateServer ? TickSubscription.Both : TickSubscription.Client) : (hasUpdateServer ? TickSubscription.Server : TickSubscription.None);
            tickSubscriptions.put(this.getClass(), subscription);
        }
        if (this.worldObj.isRemote) {
            return subscription == TickSubscription.Both || subscription == TickSubscription.Client;
        }
        return subscription == TickSubscription.Both || subscription == TickSubscription.Server;
    }

    private static enum TickSubscription {
        None,
        Client,
        Server,
        Both;

    }
}

