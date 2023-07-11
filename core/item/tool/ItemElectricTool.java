/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class ItemElectricTool
extends ItemTool
implements IElectricItem,
IBoxable,
IItemHudInfo {
    public double operationEnergyCost;
    public int maxCharge;
    public int transferLimit;
    public int tier;
    private final EnumSet<ToolClass> toolClasses;

    public ItemElectricTool(InternalName internalName, int operationEnergyCost) {
        this(internalName, operationEnergyCost, HarvestLevel.Iron, EnumSet.noneOf(ToolClass.class));
    }

    public ItemElectricTool(InternalName internalName, int operationEnergyCost, HarvestLevel harvestLevel, EnumSet<ToolClass> toolClasses) {
        this(internalName, operationEnergyCost, harvestLevel, toolClasses, new HashSet<Block>());
    }

    private ItemElectricTool(InternalName internalName, int operationEnergyCost, HarvestLevel harvestLevel, EnumSet<ToolClass> toolClasses, Set<Block> mineableBlocks) {
        super(0.0f, harvestLevel.toolMaterial, mineableBlocks);
        this.operationEnergyCost = operationEnergyCost;
        this.toolClasses = toolClasses;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        for (ToolClass toolClass : toolClasses) {
            if (toolClass.name == null) continue;
            this.setHarvestLevel(toolClass.name, harvestLevel.level);
        }
        if (toolClasses.contains((Object)ToolClass.Pickaxe) && harvestLevel.toolMaterial == Item.ToolMaterial.EMERALD) {
            mineableBlocks.add(Blocks.obsidian);
            mineableBlocks.add(Blocks.redstone_ore);
            mineableBlocks.add(Blocks.lit_redstone_ore);
        }
        GameRegistry.registerItem((Item)this, (String)internalName.name());
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        ElectricItem.manager.use(stack, 0.0, (EntityLivingBase)player);
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ElectricItem.manager.use(stack, 0.0, (EntityLivingBase)player);
        return super.onItemRightClick(stack, world, player);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(4));
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        return StatCollector.translateToLocal((String)this.getUnlocalizedName(itemStack));
    }

    public boolean canHarvestBlock(Block block, ItemStack stack) {
        Material material = block.getMaterial();
        for (ToolClass toolClass : this.toolClasses) {
            if (!toolClass.whitelist.contains(block) && !toolClass.whitelist.contains(material)) continue;
            return true;
        }
        return super.canHarvestBlock(block, stack);
    }

    public float getDigSpeed(ItemStack tool, Block block, int meta) {
        if (!ElectricItem.manager.canUse(tool, this.operationEnergyCost)) {
            return 1.0f;
        }
        return super.getDigSpeed(tool, block, meta);
    }

    public float func_150893_a(ItemStack stack, Block block) {
        if (this.canHarvestBlock(block, stack)) {
            return this.efficiencyOnProperMaterial;
        }
        return super.func_150893_a(stack, block);
    }

    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase entityliving1) {
        return true;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return this.transferLimit;
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, Block block, int par4, int par5, int par6, EntityLivingBase par7EntityLiving) {
        if ((double)block.getBlockHardness(par2World, par4, par5, par6) != 0.0) {
            if (par7EntityLiving != null) {
                ElectricItem.manager.use(par1ItemStack, this.operationEnergyCost, par7EntityLiving);
            } else {
                ElectricItem.manager.discharge(par1ItemStack, this.operationEnergyCost, this.tier, true, false, false);
            }
        }
        return true;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        itemList.add(this.getItemStack(Double.POSITIVE_INFINITY));
        itemList.add(this.getItemStack(0.0));
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerTier") + " " + this.tier);
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerTier") + " " + this.tier);
        return info;
    }

    protected ItemStack getItemStack(double charge) {
        ItemStack ret = new ItemStack((Item)this);
        ElectricItem.manager.charge(ret, charge, Integer.MAX_VALUE, true, false);
        return ret;
    }

    protected static enum ToolClass {
        Axe("axe", Material.wood, Material.plants, Material.vine),
        Pickaxe("pickaxe", Material.iron, Material.anvil, Material.rock),
        Shears("shears", Blocks.web, Blocks.wool, Blocks.redstone_wire, Blocks.tripwire, Material.leaves),
        Shovel("shovel", Blocks.snow_layer, Blocks.snow),
        Sword("sword", Blocks.web, Material.plants, Material.vine, Material.coral, Material.leaves, Material.gourd),
        Hoe(null, Blocks.dirt, Blocks.grass, Blocks.mycelium);

        public final String name;
        public final Set<Object> whitelist;

        private ToolClass(String name, Object ... whitelist) {
            this.name = name;
            this.whitelist = new HashSet<Object>(Arrays.asList(whitelist));
        }
    }

    protected static enum HarvestLevel {
        Wood(0, Item.ToolMaterial.WOOD),
        Stone(1, Item.ToolMaterial.STONE),
        Iron(2, Item.ToolMaterial.IRON),
        Diamond(3, Item.ToolMaterial.EMERALD),
        Iridium(100, Item.ToolMaterial.EMERALD);

        public final int level;
        public final Item.ToolMaterial toolMaterial;

        private HarvestLevel(int level, Item.ToolMaterial toolMaterial) {
            this.level = level;
            this.toolMaterial = toolMaterial;
        }
    }
}

