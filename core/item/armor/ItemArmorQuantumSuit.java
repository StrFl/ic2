/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 */
package ic2.core.item.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemTinCan;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorQuantumSuit
extends ItemArmorElectric {
    protected static final Map<Integer, Integer> potionRemovalCost = new HashMap<Integer, Integer>();
    private float jumpCharge;
    public static AudioSource audioSource;
    private static boolean lastJetpackUsed;

    public ItemArmorQuantumSuit(InternalName internalName, int armorType1) {
        super(internalName, InternalName.quantum, armorType1, 1.0E7, 12000.0, 4);
        if (armorType1 == 3) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
        potionRemovalCost.put(Potion.poison.id, 10000);
        potionRemovalCost.put(IC2Potion.radiation.id, 10000);
        potionRemovalCost.put(Potion.wither.id, 25000);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public boolean hasColor(ItemStack aStack) {
        return this.getColor(aStack) != 10511680;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
        return this.getIconFromDamage(par1);
    }

    public void removeColor(ItemStack par1ItemStack) {
        NBTTagCompound tNBT = par1ItemStack.getTagCompound();
        if (tNBT != null && (tNBT = tNBT.getCompoundTag("display")).hasKey("color")) {
            tNBT.removeTag("color");
        }
    }

    public int getColor(ItemStack aStack) {
        NBTTagCompound tNBT = aStack.getTagCompound();
        if (tNBT == null) {
            return 10511680;
        }
        return (tNBT = tNBT.getCompoundTag("display")) == null ? 10511680 : (tNBT.hasKey("color") ? tNBT.getInteger("color") : 10511680);
    }

    public void colorQArmor(ItemStack aStack, int par2) {
        NBTTagCompound tNBT = aStack.getTagCompound();
        if (tNBT == null) {
            tNBT = new NBTTagCompound();
            aStack.setTagCompound(tNBT);
        }
        if (!tNBT.hasKey("display")) {
            tNBT.setTag("display", (NBTBase)tNBT);
        }
        tNBT = tNBT.getCompoundTag("display");
        tNBT.setInteger("color", par2);
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source == DamageSource.fall && this.armorType == 3) {
            int energyPerDamage = this.getEnergyPerDamage();
            int damageLimit = Integer.MAX_VALUE;
            if (energyPerDamage > 0) {
                damageLimit = (int)Math.min((double)damageLimit, 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage);
            }
            return new ISpecialArmor.ArmorProperties(10, 1.0, damageLimit);
        }
        return super.getProperties(entity, armor, source, damage, slot);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        EntityLivingBase entity;
        ItemStack armor;
        if (IC2.platform.isSimulating() && event.entity instanceof EntityLivingBase && (armor = (entity = (EntityLivingBase)event.entity).getEquipmentInSlot(1)) != null && armor.getItem() == this) {
            int fallDamage = Math.max((int)event.distance - 10, 0);
            double energyCost = this.getEnergyPerDamage() * fallDamage;
            if (energyCost <= ElectricItem.manager.getCharge(armor)) {
                ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                event.setCanceled(true);
            }
        }
    }

    @Override
    public double getDamageAbsorptionRatio() {
        if (this.armorType == 1) {
            return 1.1;
        }
        return 1.0;
    }

    @Override
    public int getEnergyPerDamage() {
        return 20000;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean ret = false;
        switch (this.armorType) {
            case 0: {
                IC2.platform.profilerStartSection("QuantumHelmet");
                int air = player.getAir();
                if (ElectricItem.manager.canUse(itemStack, 1000.0) && air < 100) {
                    player.setAir(air + 200);
                    ElectricItem.manager.use(itemStack, 1000.0, null);
                    ret = true;
                } else if (air <= 0) {
                    IC2.achievements.issueAchievement(player, "starveWithQHelmet");
                }
                if (ElectricItem.manager.canUse(itemStack, 1000.0) && player.getFoodStats().needFood()) {
                    int slot = -1;
                    for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                        if (player.inventory.mainInventory[i] == null || player.inventory.mainInventory[i].getItem() != Ic2Items.filledTinCan.getItem()) continue;
                        slot = i;
                        break;
                    }
                    if (slot > -1) {
                        ItemStack stack = player.inventory.mainInventory[slot];
                        ItemTinCan can = (ItemTinCan)stack.getItem();
                        stack = can.onEaten(player, stack);
                        if (stack.stackSize <= 0) {
                            player.inventory.mainInventory[slot] = null;
                        }
                        ElectricItem.manager.use(itemStack, 1000.0, null);
                        ret = true;
                    }
                } else if (player.getFoodStats().getFoodLevel() <= 0) {
                    IC2.achievements.issueAchievement(player, "starveWithQHelmet");
                }
                for (PotionEffect effect : new LinkedList(player.getActivePotionEffects())) {
                    int id = effect.getPotionID();
                    Integer cost = potionRemovalCost.get(id);
                    if (cost == null || !ElectricItem.manager.canUse(itemStack, (cost = Integer.valueOf(cost * (effect.getAmplifier() + 1))).intValue())) continue;
                    ElectricItem.manager.use(itemStack, cost.intValue(), null);
                    IC2.platform.removePotion((EntityLivingBase)player, id);
                }
                boolean Nightvision = nbtData.getBoolean("Nightvision");
                short hubmode = nbtData.getShort("HudMode");
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = Nightvision = !Nightvision;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("Nightvision", Nightvision);
                        if (Nightvision) {
                            IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    hubmode = hubmode == 2 ? (short)0 : (short)(hubmode + 1);
                    if (IC2.platform.isSimulating()) {
                        nbtData.setShort("HudMode", hubmode);
                        switch (hubmode) {
                            case 0: {
                                IC2.platform.messagePlayer(player, "HUD disabled.", new Object[0]);
                                break;
                            }
                            case 1: {
                                IC2.platform.messagePlayer(player, "HUD (basic) enabled.", new Object[0]);
                                break;
                            }
                            case 2: {
                                IC2.platform.messagePlayer(player, "HUD (extended) enabled", new Object[0]);
                            }
                        }
                    }
                }
                if (IC2.platform.isSimulating() && toggleTimer > 0) {
                    toggleTimer = (byte)(toggleTimer - 1);
                    nbtData.setByte("toggleTimer", toggleTimer);
                }
                if (Nightvision && IC2.platform.isSimulating() && ElectricItem.manager.use(itemStack, 1.0, (EntityLivingBase)player)) {
                    int x = MathHelper.floor_double((double)player.posX);
                    int z = MathHelper.floor_double((double)player.posZ);
                    int y = MathHelper.floor_double((double)player.posY);
                    int skylight = player.worldObj.getBlockLightValue(x, y, z);
                    if (skylight > 8) {
                        IC2.platform.removePotion((EntityLivingBase)player, Potion.nightVision.id);
                        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0, true));
                    } else {
                        IC2.platform.removePotion((EntityLivingBase)player, Potion.blindness.id);
                        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
                    }
                    ret = true;
                }
                IC2.platform.profilerEndSection();
                break;
            }
            case 1: {
                IC2.platform.profilerStartSection("QuantumBodyarmor");
                boolean jetpack = nbtData.getBoolean("jetpack");
                boolean hoverMode = nbtData.getBoolean("hoverMode");
                boolean jetpackUsed = false;
                if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = hoverMode = !hoverMode;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("hoverMode", hoverMode);
                        if (hoverMode) {
                            IC2.platform.messagePlayer(player, "Quantum Hover Mode enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Quantum Hover Mode disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isBoostKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = jetpack = !jetpack;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("jetpack", jetpack);
                        if (jetpack) {
                            IC2.platform.messagePlayer(player, "Quantum Jetpack enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Quantum Jetpack disabled.", new Object[0]);
                        }
                    }
                }
                if (jetpack && (IC2.keyboard.isJumpKeyDown(player) || hoverMode && player.motionY < (double)-0.03f)) {
                    jetpackUsed = this.useJetpack(player, hoverMode);
                }
                if (IC2.platform.isSimulating() && toggleTimer > 0) {
                    toggleTimer = (byte)(toggleTimer - 1);
                    nbtData.setByte("toggleTimer", toggleTimer);
                }
                if (IC2.platform.isRendering() && player == IC2.platform.getPlayerInstance()) {
                    if (lastJetpackUsed != jetpackUsed) {
                        if (jetpackUsed) {
                            if (audioSource == null) {
                                audioSource = IC2.audioManager.createSource(player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                            }
                            if (audioSource != null) {
                                audioSource.play();
                            }
                        } else if (audioSource != null) {
                            audioSource.remove();
                            audioSource = null;
                        }
                        lastJetpackUsed = jetpackUsed;
                    }
                    if (audioSource != null) {
                        audioSource.updatePosition();
                    }
                }
                ret = jetpackUsed;
                player.extinguish();
                IC2.platform.profilerEndSection();
                break;
            }
            case 2: {
                IC2.platform.profilerStartSection("QuantumLeggings");
                boolean enableQuantumSpeedOnSprint = IC2.platform.isRendering() ? ConfigUtil.getBool(MainConfig.get(), "misc/quantumSpeedOnSprint") : true;
                if (ElectricItem.manager.canUse(itemStack, 1000.0) && (player.onGround || player.isInWater()) && IC2.keyboard.isForwardKeyDown(player) && (enableQuantumSpeedOnSprint && player.isSprinting() || !enableQuantumSpeedOnSprint && IC2.keyboard.isBoostKeyDown(player))) {
                    byte speedTicker = nbtData.getByte("speedTicker");
                    if ((speedTicker = (byte)(speedTicker + 1)) >= 10) {
                        speedTicker = 0;
                        ElectricItem.manager.use(itemStack, 1000.0, null);
                        ret = true;
                    }
                    nbtData.setByte("speedTicker", speedTicker);
                    float speed = 0.22f;
                    if (player.isInWater()) {
                        speed = 0.1f;
                        if (IC2.keyboard.isJumpKeyDown(player)) {
                            player.motionY += (double)0.1f;
                        }
                    }
                    if (speed > 0.0f) {
                        player.moveFlying(0.0f, 1.0f, speed);
                    }
                }
                IC2.platform.profilerEndSection();
                break;
            }
            case 3: {
                IC2.platform.profilerStartSection("QuantumBoots");
                if (IC2.platform.isSimulating()) {
                    boolean wasOnGround;
                    boolean bl = wasOnGround = nbtData.hasKey("wasOnGround") ? nbtData.getBoolean("wasOnGround") : true;
                    if (wasOnGround && !player.onGround && IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                        ElectricItem.manager.use(itemStack, 4000.0, null);
                        ret = true;
                    }
                    if (player.onGround != wasOnGround) {
                        nbtData.setBoolean("wasOnGround", player.onGround);
                    }
                } else {
                    if (ElectricItem.manager.canUse(itemStack, 4000.0) && player.onGround) {
                        this.jumpCharge = 1.0f;
                    }
                    if (player.motionY >= 0.0 && this.jumpCharge > 0.0f && !player.isInWater()) {
                        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                            if (this.jumpCharge == 1.0f) {
                                player.motionX *= 3.5;
                                player.motionZ *= 3.5;
                            }
                            player.motionY += (double)(this.jumpCharge * 0.3f);
                            this.jumpCharge = (float)((double)this.jumpCharge * 0.75);
                        } else if (this.jumpCharge < 1.0f) {
                            this.jumpCharge = 0.0f;
                        }
                    }
                }
                IC2.platform.profilerEndSection();
            }
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
        int worldHeight;
        double y;
        ItemStack jetpack = player.inventory.armorInventory[2];
        if (ElectricItem.manager.getCharge(jetpack) == 0.0) {
            return false;
        }
        float power = 1.0f;
        float dropPercentage = 0.05f;
        if ((double)((float)ElectricItem.manager.getCharge(jetpack)) / this.getMaxCharge(jetpack) <= (double)dropPercentage) {
            power = (float)((double)power * (ElectricItem.manager.getCharge(jetpack) / (this.getMaxCharge(jetpack) * (double)dropPercentage)));
        }
        if (IC2.keyboard.isForwardKeyDown(player)) {
            float forwardpower;
            float retruster = 3.5f;
            if (hoverMode) {
                retruster = 0.5f;
            }
            if ((forwardpower = power * retruster * 2.0f) > 0.0f) {
                player.moveFlying(0.0f, 0.4f * forwardpower, 0.02f);
            }
        }
        if ((y = player.posY) > (double)((worldHeight = IC2.getWorldHeight(player.worldObj)) - 25)) {
            if (y > (double)worldHeight) {
                y = worldHeight;
            }
            power = (float)((double)power * (((double)worldHeight - y) / 25.0));
        }
        double prevmotion = player.motionY;
        player.motionY = Math.min(player.motionY + (double)(power * 0.2f), (double)0.6f);
        if (hoverMode) {
            float maxHoverY = -0.025f;
            if (IC2.keyboard.isSneakKeyDown(player)) {
                maxHoverY = -0.1f;
            }
            if (IC2.keyboard.isJumpKeyDown(player)) {
                maxHoverY = 0.1f;
            }
            if (player.motionY > (double)maxHoverY) {
                player.motionY = maxHoverY;
                if (prevmotion > player.motionY) {
                    player.motionY = prevmotion;
                }
            }
        }
        double consume = 8.0;
        if (hoverMode) {
            consume = 10.0;
        }
        ElectricItem.manager.discharge(jetpack, consume, Integer.MAX_VALUE, true, false, false);
        player.fallDistance = 0.0f;
        player.distanceWalkedModified = 0.0f;
        IC2.platform.resetPlayerInAirTime(player);
        return true;
    }

    static {
        lastJetpackUsed = false;
    }
}

