/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
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
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotConsumableKineticRotor;
import ic2.core.block.kineticgenerator.block.BlockKineticGenerator;
import ic2.core.block.kineticgenerator.container.ContainerWaterKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiWaterKineticGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityWaterKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IHasGui {
    private boolean firsttick = true;
    private int updateTicker = IC2.random.nextInt(this.getTickRate());
    public InvSlotConsumableClass rotorSlot = new InvSlotConsumableKineticRotor((TileEntityInventory)this, "rotorslot", 0, InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, IKineticRotor.GearboxType.WATER);
    public short type = 0;
    private boolean rightFacing;
    private int distanceToNormalBiome;
    private int crossSection;
    private int obstructedCrossSection;
    private int waterFlow;
    private final double efficiencyRollOffExponent = 2.0;
    private static final float outputModifier = 0.2f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/water");
    private float angle = 0.0f;
    private float rotationSpeed;
    private static final float rotationModifier = 0.1f;
    private long lastcheck;

    private int getTickRate() {
        return 20;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.firsttick) {
            this.updateSeaInfo();
            this.firsttick = false;
        }
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (this.type == 0) {
            BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord);
            this.type = BiomeDictionary.isBiomeOfType((BiomeGenBase)biome, (BiomeDictionary.Type)BiomeDictionary.Type.OCEAN) ? (short)1 : (BiomeDictionary.isBiomeOfType((BiomeGenBase)biome, (BiomeDictionary.Type)BiomeDictionary.Type.RIVER) ? (short)2 : (short)-1);
        }
        if (this.type == -1) {
            return;
        }
        boolean needsInvUpdate = false;
        if (!this.rotorSlot.isEmpty()) {
            if (this.checkSpace(1, true) == 0) {
                if (!this.getActive()) {
                    this.setActive(true);
                    needsInvUpdate = true;
                }
            } else if (this.getActive()) {
                this.setActive(false);
                needsInvUpdate = true;
            }
        } else if (this.getActive()) {
            this.setActive(false);
            needsInvUpdate = true;
        }
        if (this.getActive()) {
            this.crossSection = this.getRotorDiameter() / 2 * 2 * 2 + 1;
            this.crossSection *= this.crossSection;
            this.obstructedCrossSection = this.checkSpace(this.getRotorDiameter() * 3, false);
            if (this.obstructedCrossSection > 0 && this.obstructedCrossSection <= (this.getRotorDiameter() + 1) / 2) {
                this.obstructedCrossSection = 0;
            }
            int rotordamage = 0;
            if (this.obstructedCrossSection < 0) {
                boolean update = this.rotationSpeed != 0.0f;
                this.rotationSpeed = 0.0f;
                this.waterFlow = 0;
                if (update) {
                    IC2.network.get().updateTileEntityField(this, "rotationSpeed");
                }
            } else if (this.type == 1) {
                float diff = (float)Math.sin((double)this.worldObj.getWorldTime() * Math.PI / 6000.0);
                diff *= Math.abs(diff);
                this.rotationSpeed = (float)((double)(diff * (float)this.distanceToNormalBiome / 100.0f) * (1.0 - Math.pow((double)this.obstructedCrossSection / (double)this.crossSection, 2.0)));
                this.waterFlow = (int)(this.rotationSpeed * 3000.0f);
                if (this.rightFacing) {
                    this.rotationSpeed *= -1.0f;
                }
                IC2.network.get().updateTileEntityField(this, "rotationSpeed");
                this.waterFlow = (int)((float)this.waterFlow * this.getefficiency());
                rotordamage = 2;
            } else if (this.type == 2) {
                this.rotationSpeed = Math.max(Math.min(this.distanceToNormalBiome, 20), 50) / 50;
                this.waterFlow = (int)(this.rotationSpeed * 1000.0f);
                if (this.getFacing() == ForgeDirection.EAST.ordinal() || this.getFacing() == ForgeDirection.NORTH.ordinal()) {
                    this.rotationSpeed *= -1.0f;
                }
                IC2.network.get().updateTileEntityField(this, "rotationSpeed");
                this.waterFlow = (int)((float)this.waterFlow * (this.getefficiency() * (1.0f - 0.3f * this.worldObj.rand.nextFloat() - 0.1f * ((float)this.obstructedCrossSection / (float)this.crossSection))));
                rotordamage = 1;
            }
            this.rotorSlot.damage(rotordamage, false);
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

    public int getRotorDiameter() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            if (this.type == 1) {
                return ((IKineticRotor)stack.getItem()).getDiameter(stack);
            }
            return (((IKineticRotor)stack.getItem()).getDiameter(stack) + 1) * 2 / 3;
        }
        return 0;
    }

    public int checkSpace(int length, boolean onlyrotor) {
        int box = this.getRotorDiameter() / 2;
        int lentemp = 0;
        if (onlyrotor) {
            length = 1;
            lentemp = length + 1;
        } else {
            box *= 2;
        }
        ForgeDirection fwdDir = ForgeDirection.VALID_DIRECTIONS[this.getFacing()];
        ForgeDirection rightDir = fwdDir.getRotation(ForgeDirection.DOWN);
        int ret = 0;
        for (int up = -box; up <= box; ++up) {
            int y = this.yCoord + up;
            for (int right = -box; right <= box; ++right) {
                boolean occupied = false;
                for (int fwd = lentemp - length; fwd <= length; ++fwd) {
                    int x = this.xCoord + fwd * fwdDir.offsetX + right * rightDir.offsetX;
                    int z = this.zCoord + fwd * fwdDir.offsetZ + right * rightDir.offsetZ;
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block == Blocks.water) continue;
                    occupied = true;
                    if (block instanceof BlockKineticGenerator) {
                        boolean bl = false;
                    }
                    if (up == 0 && right == 0 && fwd == 0 || !(this.worldObj.getTileEntity(x, y, z) instanceof TileEntityWaterKineticGenerator) || onlyrotor) continue;
                    return -1;
                }
                if (!occupied) continue;
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerWaterKineticGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiWaterKineticGenerator((ContainerWaterKineticGenerator)this.getGuiContainer(entityPlayer));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Water Kinetic Generator";
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
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
        this.updateSeaInfo();
    }

    public void updateSeaInfo() {
        ForgeDirection facing = ForgeDirection.VALID_DIRECTIONS[this.getFacing()];
        for (int i = 1; i < 200; ++i) {
            BiomeGenBase biometmp = this.worldObj.getBiomeGenForCoords(this.xCoord + facing.offsetX * i, this.zCoord + facing.offsetZ * i);
            if (!this.isValidBiome(biometmp)) {
                this.distanceToNormalBiome = i;
                this.rightFacing = true;
                return;
            }
            biometmp = this.worldObj.getBiomeGenForCoords(this.xCoord - facing.offsetX * i, this.zCoord - facing.offsetZ * i);
            if (this.isValidBiome(biometmp)) continue;
            this.distanceToNormalBiome = i;
            this.rightFacing = false;
            return;
        }
        this.distanceToNormalBiome = 200;
        this.rightFacing = true;
    }

    public boolean isValidBiome(BiomeGenBase biome) {
        return biome == BiomeGenBase.deepOcean || biome == BiomeGenBase.ocean || biome == BiomeGenBase.river;
    }

    public ResourceLocation getRotorRenderTexture() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getRotorRenderTexture(stack);
        }
        return new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorWoodmodel.png");
    }

    public float getAngle() {
        if (this.rotationSpeed != 0.0f) {
            this.angle += (float)(System.currentTimeMillis() - this.lastcheck) * this.rotationSpeed * 0.1f;
            this.angle %= 360.0f;
            this.lastcheck = System.currentTimeMillis();
        }
        this.lastcheck = System.currentTimeMillis();
        return this.angle;
    }

    @Override
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
        return this.getKuOutput();
    }

    @Override
    public int requestkineticenergy(ForgeDirection directionFrom, int requestkineticenergy) {
        if (directionFrom.getOpposite().ordinal() == this.getFacing()) {
            return Math.min(requestkineticenergy, this.getKuOutput());
        }
        return 0;
    }

    public int getKuOutput() {
        if (this.getActive()) {
            return (int)Math.abs((float)this.waterFlow * outputModifier);
        }
        return 0;
    }

    public float getefficiency() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getEfficiency(stack);
        }
        return 0.0f;
    }

    public String getRotorhealth() {
        if (!this.rotorSlot.isEmpty()) {
            return StatCollector.translateToLocalFormatted((String)"ic2.WaterKineticGenerator.gui.rotorhealth", (Object[])new Object[]{(int)(100.0f - (float)this.rotorSlot.get().getItemDamage() / (float)this.rotorSlot.get().getMaxDamage() * 100.0f)});
        }
        return "";
    }

    @Override
    public void setActive(boolean active) {
        if (active != this.getActive()) {
            IC2.network.get().updateTileEntityField(this, "rotorSlot");
        }
        super.setActive(active);
    }
}

