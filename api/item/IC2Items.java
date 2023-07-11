/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.item;

import net.minecraft.item.ItemStack;

public final class IC2Items {
    private static Class<?> Ic2Items;

    public static ItemStack getItem(String name) {
        try {
            Object ret;
            if (Ic2Items == null) {
                Ic2Items = Class.forName(IC2Items.getPackage() + ".core.Ic2Items");
            }
            if ((ret = Ic2Items.getField(name).get(null)) instanceof ItemStack) {
                return (ItemStack)ret;
            }
            return null;
        }
        catch (Exception e) {
            System.out.println("IC2 API: Call getItem failed for " + name);
            return null;
        }
    }

    private static String getPackage() {
        Package pkg = IC2Items.class.getPackage();
        if (pkg != null) {
            String packageName = pkg.getName();
            return packageName.substring(0, packageName.length() - ".api.item".length());
        }
        return "ic2";
    }
}

