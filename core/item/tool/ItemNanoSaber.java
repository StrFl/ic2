/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemNanoSaber
extends ItemElectricTool {
    public static int ticker = 0;
    @SideOnly(value=Side.CLIENT)
    private IIcon[] textures;
    private int soundTicker = 0;

    public ItemNanoSaber(InternalName internalName) {
        this(internalName, 10, ItemElectricTool.HarvestLevel.Diamond);
    }

    public ItemNanoSaber(InternalName internalName, int operationEnergyCost, ItemElectricTool.HarvestLevel harvestLevel) {
        super(internalName, operationEnergyCost, harvestLevel, EnumSet.of(ItemElectricTool.ToolClass.Sword));
        this.maxCharge = 160000;
        this.transferLimit = 500;
        this.tier = 3;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.textures = new IIcon[2];
        this.textures[0] = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(4) + "." + InternalName.off.name());
        this.textures[1] = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(4) + "." + InternalName.active.name());
    }

    @SideOnly(value=Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(ItemStack itemStack, int pass) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        if (nbtData.getBoolean("active")) {
            return this.textures[1];
        }
        return this.textures[0];
    }

    @Override
    public float getDigSpeed(ItemStack itemStack, Block block, int meta) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        if (nbtData.getBoolean("active")) {
            ++this.soundTicker;
            if (this.soundTicker % 4 == 0) {
                IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0f, 1.0f);
            }
            return 4.0f;
        }
        return 1.0f;
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        NBTTagCompound nbtData;
        int dmg = 4;
        if (ElectricItem.manager.canUse(stack, 400.0) && (nbtData = StackUtil.getOrCreateNbtData(stack)).getBoolean("active")) {
            dmg = 20;
        }
        HashMultimap ret = HashMultimap.create();
        ret.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(field_111210_e, "Tool modifier", (double)dmg, 0));
        return ret;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase source) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        if (!nbtData.getBoolean("active")) {
            return true;
        }
        if (IC2.platform.isSimulating()) {
            ItemNanoSaber.drainSaber(stack, 400.0, source);
            if (!(source instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled() || !(target instanceof EntityPlayer)) {
                for (int i = 0; i < 4 && ElectricItem.manager.canUse(stack, 2000.0); ++i) {
                    ItemStack armor = target.getEquipmentInSlot(i + 1);
                    if (armor == null) continue;
                    double amount = 0.0;
                    if (armor.getItem() instanceof ItemArmorNanoSuit) {
                        amount = 48000.0;
                    } else if (armor.getItem() instanceof ItemArmorQuantumSuit) {
                        amount = 300000.0;
                    }
                    if (!(amount > 0.0)) continue;
                    ElectricItem.manager.discharge(armor, amount, this.tier, true, false, false);
                    if (!ElectricItem.manager.canUse(armor, 1.0)) {
                        target.setCurrentItemOrArmor(i + 1, null);
                    }
                    ItemNanoSaber.drainSaber(stack, 2000.0, source);
                }
            }
        }
        if (IC2.platform.isRendering()) {
            IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0f, 1.0f);
        }
        return true;
    }

    public String getRandomSwingSound() {
        switch (IC2.random.nextInt(3)) {
            default: {
                return "Tools/Nanosabre/NanosabreSwing1.ogg";
            }
            case 1: {
                return "Tools/Nanosabre/NanosabreSwing2.ogg";
            }
            case 2: 
        }
        return "Tools/Nanosabre/NanosabreSwing3.ogg";
    }

    public boolean onBlockStartBreak(ItemStack itemStack, int i, int j, int k, EntityPlayer player) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        if (nbtData.getBoolean("active")) {
            ItemNanoSaber.drainSaber(itemStack, 80.0, (EntityLivingBase)player);
        }
        return false;
    }

    public boolean isFull3D() {
        return true;
    }

    public static void drainSaber(ItemStack itemStack, double amount, EntityLivingBase entity) {
        if (!ElectricItem.manager.use(itemStack, amount, entity)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
            nbtData.setBoolean("active", false);
            ItemNanoSaber.updateAttributes(nbtData);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityplayer) {
        if (!IC2.platform.isSimulating()) {
            return itemStack;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        if (nbtData.getBoolean("active")) {
            nbtData.setBoolean("active", false);
            ItemNanoSaber.updateAttributes(nbtData);
        } else if (ElectricItem.manager.canUse(itemStack, 16.0)) {
            nbtData.setBoolean("active", true);
            ItemNanoSaber.updateAttributes(nbtData);
            IC2.platform.playSoundSp("Tools/Nanosabre/NanosabrePowerup.ogg", 1.0f, 1.0f);
        }
        return super.onItemRightClick(itemStack, world, entityplayer);
    }

    public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean par5) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        if (!nbtData.getBoolean("active")) {
            return;
        }
        if (ticker % 16 == 0 && entity instanceof EntityPlayerMP) {
            if (slot < 9) {
                ItemNanoSaber.drainSaber(itemStack, 64.0, (EntityLivingBase)((EntityPlayer)entity));
            } else if (ticker % 64 == 0) {
                ItemNanoSaber.drainSaber(itemStack, 16.0, (EntityLivingBase)((EntityPlayer)entity));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    private static void updateAttributes(NBTTagCompound nbtData) {
        boolean active = nbtData.getBoolean("active");
        int damage = active ? 20 : 4;
        NBTTagCompound entry = new NBTTagCompound();
        entry.setString("AttributeName", SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        entry.setLong("UUIDMost", field_111210_e.getMostSignificantBits());
        entry.setLong("UUIDLeast", field_111210_e.getLeastSignificantBits());
        entry.setString("Name", "Tool modifier");
        entry.setDouble("Amount", (double)damage);
        entry.setInteger("Operation", 0);
        NBTTagList list = new NBTTagList();
        list.appendTag((NBTBase)entry);
        nbtData.setTag("AttributeModifiers", (NBTBase)list);
    }
}

