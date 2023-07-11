/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.ChunkCache
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.kineticgenerator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IKineticSource;
import ic2.api.item.IKineticRotor;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.WorldData;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotConsumableKineticRotor;
import ic2.core.block.kineticgenerator.container.ContainerWindKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiWindKineticGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Util;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityWindKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IHasGui {
    public final InvSlotConsumableClass rotorSlot;
    private double windStrength;
    private int obstructedCrossSection;
    private int crossSection;
    private int updateTicker = IC2.random.nextInt(this.getTickRate());
    private float rotationSpeed;
    private static final double efficiencyRollOffExponent = 2.0;
    private static final int nominalRotationPeriod = 500;
    public static final float outputModifier = 10.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/wind");

    public TileEntityWindKineticGenerator() {
        this.rotorSlot = new InvSlotConsumableKineticRotor((TileEntityInventory)this, "rotorslot", 0, InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, IKineticRotor.GearboxType.WIND);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        boolean needsInvUpdate = false;
        if (!this.rotorSlot.isEmpty()) {
            if (this.checkSpace(1, true) == 0) {
                if (!this.getActive()) {
                    this.setActive(true);
                }
                needsInvUpdate = true;
            } else {
                if (this.getActive()) {
                    this.setActive(false);
                }
                needsInvUpdate = true;
            }
        } else {
            if (this.getActive()) {
                this.setActive(false);
            }
            needsInvUpdate = true;
        }
        if (this.getActive()) {
            this.crossSection = this.getRotorDiameter() / 2 * 2 * 2 + 1;
            this.crossSection *= this.crossSection;
            this.obstructedCrossSection = this.checkSpace(this.getRotorDiameter() * 3, false);
            if (this.obstructedCrossSection > 0 && this.obstructedCrossSection <= (this.getRotorDiameter() + 1) / 2) {
                this.obstructedCrossSection = 0;
            }
            if (this.obstructedCrossSection < 0) {
                this.windStrength = 0.0;
                this.setRotationSpeed(0.0f);
            } else {
                this.windStrength = this.calcWindStrength();
                float speed = (float)Util.limit((this.windStrength - (double)this.getMinWindStrength()) / (double)this.getMaxWindStrength(), 0.0, 2.0);
                this.setRotationSpeed(speed);
                if (this.windStrength >= (double)this.getMinWindStrength()) {
                    if (this.windStrength <= (double)this.getMaxWindStrength()) {
                        this.rotorSlot.damage(1, false);
                    } else {
                        this.rotorSlot.damage(4, false);
                    }
                }
            }
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(1);
        ret.add("rotationSpeed");
        ret.add("rotorSlot");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    public ContainerBase<TileEntityWindKineticGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerWindKineticGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiWindKineticGenerator(new ContainerWindKineticGenerator(entityPlayer, this));
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        if (side == 0 || side == 1) {
            return false;
        }
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        super.setFacing(side);
    }

    public String getRotorhealth() {
        if (!this.rotorSlot.isEmpty()) {
            return StatCollector.translateToLocalFormatted((String)"ic2.WindKineticGenerator.gui.rotorhealth", (Object[])new Object[]{(int)(100.0f - (float)this.rotorSlot.get().getItemDamage() / (float)this.rotorSlot.get().getMaxDamage() * 100.0f)});
        }
        return "";
    }

    @Override
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
        return this.getKuOutput();
    }

    @Override
    public int requestkineticenergy(ForgeDirection directionFrom, int requestkineticenergy) {
        if (this.facingMatchesDirection(directionFrom.getOpposite())) {
            return Math.min(requestkineticenergy, this.getKuOutput());
        }
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "Wind Kinetic Generator";
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }

    public int checkSpace(int length, boolean onlyrotor) {
        int box = this.getRotorDiameter() / 2;
        int lentemp = 0;
        if (onlyrotor) {
            length = 1;
            lentemp = length + 1;
        }
        if (!onlyrotor) {
            box *= 2;
        }
        ForgeDirection fwdDir = ForgeDirection.VALID_DIRECTIONS[this.getFacing()];
        ForgeDirection rightDir = fwdDir.getRotation(ForgeDirection.DOWN);
        int xMaxDist = Math.abs(length * fwdDir.offsetX + box * rightDir.offsetX);
        int zMaxDist = Math.abs(length * fwdDir.offsetZ + box * rightDir.offsetZ);
        ChunkCache chunkCache = new ChunkCache(this.worldObj, this.xCoord - xMaxDist, this.yCoord - box, this.zCoord - zMaxDist, this.xCoord + xMaxDist, this.yCoord + box, this.zCoord + zMaxDist, 0);
        int ret = 0;
        for (int up = -box; up <= box; ++up) {
            int y = this.yCoord + up;
            for (int right = -box; right <= box; ++right) {
                boolean occupied = false;
                for (int fwd = lentemp - length; fwd <= length; ++fwd) {
                    int x = this.xCoord + fwd * fwdDir.offsetX + right * rightDir.offsetX;
                    int z = this.zCoord + fwd * fwdDir.offsetZ + right * rightDir.offsetZ;
                    assert (Math.abs(x - this.xCoord) <= xMaxDist);
                    assert (Math.abs(z - this.zCoord) <= zMaxDist);
                    Block block = chunkCache.getBlock(x, y, z);
                    if (block.isAir((IBlockAccess)chunkCache, x, y, z)) continue;
                    occupied = true;
                    if (up == 0 && right == 0 && fwd == 0 || !(chunkCache.getTileEntity(x, y, z) instanceof TileEntityWindKineticGenerator) || onlyrotor) continue;
                    return -1;
                }
                if (!occupied) continue;
                ++ret;
            }
        }
        return ret;
    }

    public boolean checkrotor() {
        return !this.rotorSlot.isEmpty();
    }

    public boolean rotorspace() {
        return this.checkSpace(1, true) == 0;
    }

    private void setRotationSpeed(float speed) {
        if (this.rotationSpeed != speed) {
            this.rotationSpeed = speed;
            IC2.network.get().updateTileEntityField(this, "rotationSpeed");
        }
    }

    public int getTickRate() {
        return 32;
    }

    public double calcWindStrength() {
        double windStr = WorldData.get((World)this.worldObj).windSim.getWindAt(this.yCoord);
        return Math.max(0.0, windStr *= 1.0 - Math.pow((double)this.obstructedCrossSection / (double)this.crossSection, 2.0));
    }

    public float getAngle() {
        if (this.rotationSpeed > 0.0f) {
            long period = (long)(5.0E8f / this.rotationSpeed);
            return (float)(System.nanoTime() % period) / (float)period * 360.0f;
        }
        return 0.0f;
    }

    public float getefficiency() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getEfficiency(stack);
        }
        return 0.0f;
    }

    public int getMinWindStrength() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getMinWindStrength(stack);
        }
        return 0;
    }

    public int getMaxWindStrength() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getMaxWindStrength(stack);
        }
        return 0;
    }

    public int getRotorDiameter() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getDiameter(stack);
        }
        return 0;
    }

    public ResourceLocation getRotorRenderTexture() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getRotorRenderTexture(stack);
        }
        return new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorWoodmodel.png");
    }

    public boolean guiisoverload() {
        return this.windStrength > (double)this.getMaxWindStrength();
    }

    public boolean guiisminWindStrength() {
        return this.windStrength >= (double)this.getMinWindStrength();
    }

    public int getKuOutput() {
        if (this.windStrength >= (double)this.getMinWindStrength() && this.getActive()) {
            return (int)(this.windStrength * (double)outputModifier * (double)this.getefficiency());
        }
        return 0;
    }

    public int getWindStrength() {
        return (int)this.windStrength;
    }

    @Override
    public void setActive(boolean active) {
        if (active != this.getActive()) {
            IC2.network.get().updateTileEntityField(this, "rotorSlot");
        }
        super.setActive(active);
    }
}

