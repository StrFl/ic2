/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.item.tfbp;

import ic2.api.item.ITerraformingBP;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;

public abstract class ItemTFBP
extends ItemIC2
implements ITerraformingBP {
    public ItemTFBP(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }
}

