/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.input.Keyboard
 */
package ic2.core.item.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ILatheItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

public class ItemLatheDefault
extends ItemIC2
implements ILatheItem {
    private final LatheMaterial material;
    private static final String location = IC2.textureDomain + ":textures/items/turnables/";
    private static final String tooltipPrefix = "ic2." + (Object)((Object)InternalName.itemTurningBlanks) + ".tooltip.";

    public ItemLatheDefault(LatheMaterial material) {
        super(material.name);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.material = material;
    }

    @Override
    public int getWidth(ItemStack stack) {
        return this.material.width;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "ic2." + (Object)((Object)InternalName.itemTurningBlanks) + (Object)((Object)this.material);
    }

    @Override
    public int[] getCurrentState(ItemStack stack) {
        if (stack == null) {
            return new int[0];
        }
        int[] ret = new int[5];
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("state")) {
            NBTTagCompound tag = (NBTTagCompound)stack.getTagCompound().getTag("state");
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = tag.hasKey("l" + i) ? tag.getInteger("l" + i) : this.getWidth(stack);
            }
        } else {
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = this.getWidth(stack);
            }
        }
        return ret;
    }

    @Override
    public void setState(ItemStack stack, int position, int value) {
        if (stack == null) {
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.stackTagCompound = new NBTTagCompound();
        }
        if (!stack.getTagCompound().hasKey("state")) {
            stack.stackTagCompound.setTag("state", (NBTBase)new NBTTagCompound());
        }
        ((NBTTagCompound)stack.getTagCompound().getTag("state")).setInteger("l" + position, Math.max(Math.min(value, this.getWidth(stack)), 1));
    }

    @Override
    public ItemStack getOutputItem(ItemStack stack, int position) {
        switch (this.material) {
            case WOOD: {
                return new ItemStack(Items.stick);
            }
            case IRON: {
                return Ic2Items.smallIronDust;
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack) {
        return new ResourceLocation(location + (Object)((Object)this.material) + ".png");
    }

    @Override
    public float getOutputChance(ItemStack stack, int position) {
        return this.material.chance;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        String localized;
        int[] state = this.getCurrentState(stack);
        int numState = ItemLatheDefault.getNumericState(state);
        String str = "ic2." + (Object)((Object)this.material.name) + ".tooltip." + Integer.toHexString(numState);
        if (str.equals(localized = StatCollector.translateToLocal((String)str))) {
            list.add(StatCollector.translateToLocal((String)"ic2.itemTurningBlanks.tooltip.blank"));
        } else {
            list.add(localized);
        }
        if (Keyboard.isKeyDown((int)42)) {
            int max = this.getWidth(stack);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 5; ++j) {
                sb.append(StatCollector.translateToLocalFormatted((String)"ic2.Lathe.gui.info", (Object[])new Object[]{state[j], max}));
                sb.append("   ");
            }
            list.add(sb.toString());
        }
    }

    public static ItemStack getStackForState(ItemStack stack, int[] state) {
        if (stack == null) {
            return null;
        }
        if (state == null || state.length != 5 || !(stack.getItem() instanceof ILatheItem)) {
            return stack.copy();
        }
        stack = stack.copy();
        ILatheItem l = (ILatheItem)stack.getItem();
        for (int i = 0; i < 5; ++i) {
            l.setState(stack, i, state[i]);
        }
        return stack;
    }

    public static ItemStack getStackForState(ItemStack stack, int state) {
        return ItemLatheDefault.getStackForState(stack, ItemLatheDefault.getStateFromNumeric(state));
    }

    public static int getNumericState(int[] state) {
        if (state == null || state.length != 5) {
            return -1;
        }
        int i = 0;
        for (int j = 0; j < 5; ++j) {
            if (state[j] >= 16) continue;
            i += state[j];
            if (j == 4) continue;
            i <<= 4;
        }
        return i;
    }

    public static int[] getStateFromNumeric(int state) {
        int[] ret = new int[5];
        if (state == -1) {
            return ret;
        }
        for (int j = 4; j >= 0; --j) {
            ret[j] = state & 0xF;
            if (j == 0) continue;
            state >>= 4;
        }
        return ret;
    }

    public int getDamage(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("state")) {
            if (super.getDamage(stack) == 0) {
                for (int i = 0; i < 5; ++i) {
                    this.setState(stack, i, this.getWidth(stack));
                }
            } else {
                int[] state = ItemLatheDefault.getStateFromNumeric(super.getDamage(stack));
                for (int i = 0; i < 5; ++i) {
                    this.setState(stack, i, state[i]);
                }
            }
        }
        this.setDamage(stack, ItemLatheDefault.getNumericState(this.getCurrentState(stack)));
        return super.getDamage(stack);
    }

    @Override
    public String getTextureName(int index) {
        return index == 0 ? super.getTextureName(0) : null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return super.getIconFromDamage(0);
    }

    @Override
    public int getHardness(ItemStack stack) {
        return this.material.harvestLevel;
    }

    public static enum LatheMaterial {
        WOOD(InternalName.itemTurningBlanksWood, 0.1f, 3, 0),
        IRON(InternalName.itemTurningBlanks, 0.5f, 5, 2);

        private InternalName name;
        private float chance;
        private int width;
        private int harvestLevel;

        private LatheMaterial(InternalName name, float chance, int width, int harvestLevel) {
            this.name = name;
            this.chance = chance;
            this.width = width;
            this.harvestLevel = harvestLevel;
        }
    }
}

