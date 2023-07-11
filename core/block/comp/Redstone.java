/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.world.World
 */
package ic2.core.block.comp;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class Redstone
extends TileEntityComponent {
    private Iterable<IRedstoneModifier> modifiers;
    private int redstoneInput;

    public Redstone(TileEntityBlock parent) {
        super(parent);
    }

    @Override
    public String getDefaultName() {
        return "redstone";
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.update();
    }

    @Override
    public void onNeighborUpdate(Block srcBlock) {
        super.onNeighborUpdate(srcBlock);
        this.update();
    }

    public void update() {
        World world = this.parent.getWorldObj();
        if (world == null) {
            return;
        }
        int input = world.getStrongestIndirectPower(this.parent.xCoord, this.parent.yCoord, this.parent.zCoord);
        if (this.modifiers != null) {
            for (IRedstoneModifier modifier : this.modifiers) {
                input = modifier.getRedstoneInput(input);
            }
        }
        this.redstoneInput = input;
    }

    public int getRedstoneInput() {
        return this.redstoneInput;
    }

    public boolean hasRedstoneInput() {
        return this.redstoneInput > 0;
    }

    public void setRedstoneModifier(Iterable<IRedstoneModifier> modifiers) {
        this.modifiers = modifiers;
        this.update();
    }

    public static interface IRedstoneModifier {
        public int getRedstoneInput(int var1);
    }
}

