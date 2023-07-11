/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.server.MinecraftServer
 */
package ic2.core.block.personal;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.personal.ContainerPersonalChest;
import ic2.core.block.personal.GuiPersonalChest;
import ic2.core.block.personal.IPersonalBlock;
import java.util.List;
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;

public class TileEntityPersonalChest
extends TileEntityInventory
implements IPersonalBlock,
IHasGui {
    private int ticksSinceSync;
    private int numUsingPlayers;
    public float lidAngle;
    public float prevLidAngle;
    private GameProfile owner = null;
    public final InvSlot contentSlot = new InvSlot(this, "content", 0, InvSlot.Access.NONE, 54);

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.func_152459_a((NBTTagCompound)nbt.getCompoundTag("ownerGameProfile"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.func_152460_a((NBTTagCompound)ownerNbt, (GameProfile)this.owner);
            nbt.setTag("ownerGameProfile", (NBTBase)ownerNbt);
        }
    }

    @Override
    public String getInventoryName() {
        return "Personal Safe";
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (++this.ticksSinceSync % 20 * 4 == 0 && IC2.platform.isSimulating()) {
            this.syncNumUsingPlayers();
        }
    }

    @Override
    protected void updateEntityClient() {
        double var4;
        super.updateEntityClient();
        this.prevLidAngle = this.lidAngle;
        float var1 = 0.1f;
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0f) {
            double var2 = (double)this.xCoord + 0.5;
            var4 = (double)this.zCoord + 0.5;
            this.worldObj.playSoundEffect(var2, (double)this.yCoord + 0.5, var4, "random.chestopen", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0f || this.numUsingPlayers > 0 && this.lidAngle < 1.0f) {
            float var3;
            float var8 = this.lidAngle;
            this.lidAngle = this.numUsingPlayers > 0 ? (this.lidAngle += var1) : (this.lidAngle -= var1);
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            if (this.lidAngle < (var3 = 0.5f) && var8 >= var3) {
                var4 = (double)this.xCoord + 0.5;
                double var6 = (double)this.zCoord + 0.5;
                this.worldObj.playSoundEffect(var4, (double)this.yCoord + 0.5, var6, "random.chestclosed", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
    }

    @Override
    public void openInventory() {
        ++this.numUsingPlayers;
        this.syncNumUsingPlayers();
    }

    @Override
    public void closeInventory() {
        --this.numUsingPlayers;
        this.syncNumUsingPlayers();
    }

    public boolean receiveClientEvent(int event, int data) {
        if (event == 1) {
            this.numUsingPlayers = data;
            return true;
        }
        return false;
    }

    private void syncNumUsingPlayers() {
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), 1, this.numUsingPlayers);
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(1);
        ret.add("owner");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer player) {
        if (!this.permitsAccess(player.getGameProfile())) {
            IC2.platform.messagePlayer(player, "This safe is owned by " + this.owner.getName(), new Object[0]);
            return false;
        }
        if (!this.contentSlot.isEmpty()) {
            IC2.platform.messagePlayer(player, "Can't wrench non-empty safe", new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean permitsAccess(GameProfile profile) {
        if (profile == null) {
            return this.owner == null;
        }
        if (IC2.platform.isSimulating()) {
            if (this.owner == null) {
                this.owner = profile;
                IC2.network.get().updateTileEntityField(this, "owner");
                return true;
            }
            if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(profile)) {
                return true;
            }
        } else if (this.owner == null) {
            return true;
        }
        return this.owner.equals((Object)profile);
    }

    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    public ContainerBase<TileEntityPersonalChest> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerPersonalChest(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiPersonalChest(new ContainerPersonalChest(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }
}

