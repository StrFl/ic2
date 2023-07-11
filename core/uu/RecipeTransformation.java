/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.item.ItemStack;

public class RecipeTransformation {
    public final double transformCost;
    public List<List<ItemStack>> inputs;
    public List<ItemStack> outputs;

    public RecipeTransformation(double transformCost, List<List<ItemStack>> inputs, ItemStack ... outputs) {
        this(transformCost, inputs, Arrays.asList(outputs));
    }

    public RecipeTransformation(double transformCost, List<List<ItemStack>> inputs, List<ItemStack> outputs) {
        this.transformCost = transformCost;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    protected void merge() {
        ArrayList<List<ItemStack>> cleanInputs = new ArrayList<List<ItemStack>>();
        for (List<ItemStack> inputList : this.inputs) {
            boolean found = false;
            ListIterator<List<ItemStack>> listIterator = cleanInputs.listIterator();
            while (listIterator.hasNext()) {
                List<ItemStack> cleanInputList = (List<ItemStack>)listIterator.next();
                if ((cleanInputList = this.mergeEqualLists(inputList, cleanInputList)) == null) continue;
                found = true;
                listIterator.set(cleanInputList);
                break;
            }
            if (found) continue;
            cleanInputs.add(inputList);
        }
        for (List<ItemStack> inputList : this.inputs) {
            block3: for (List list : cleanInputs) {
                LinkedList<ItemStack> unmatched = new LinkedList<ItemStack>(inputList);
                boolean found = false;
                for (ItemStack stackOffer : list) {
                    found = false;
                    Iterator it = unmatched.iterator();
                    while (it.hasNext()) {
                        ItemStack stackReq = (ItemStack)it.next();
                        if (!StackUtil.isStackEqual(stackOffer, stackReq)) continue;
                        found = true;
                        it.remove();
                        break;
                    }
                    if (found) continue;
                    continue block3;
                }
            }
        }
        this.inputs = cleanInputs;
        ArrayList<ItemStack> cleanOutputs = new ArrayList<ItemStack>();
        for (ItemStack output : this.outputs) {
            boolean bl = false;
            ListIterator<ItemStack> it = cleanOutputs.listIterator();
            while (it.hasNext()) {
                ItemStack stack = (ItemStack)it.next();
                if (!StackUtil.isStackEqual(output, stack)) continue;
                bl = true;
                it.set(StackUtil.copyWithSize(stack, stack.stackSize + output.stackSize));
                break;
            }
            if (bl) continue;
            cleanOutputs.add(output);
        }
        this.outputs = cleanOutputs;
    }

    public String toString() {
        return "{ " + this.transformCost + " + " + StackUtil.toStringSafe2(this.inputs) + " -> " + StackUtil.toStringSafe(this.outputs) + " }";
    }

    private List<ItemStack> mergeEqualLists(List<ItemStack> listA, List<ItemStack> listB) {
        if (listA.size() != listB.size()) {
            return null;
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(listA.size());
        LinkedList<ItemStack> listBCopy = new LinkedList<ItemStack>(listB);
        for (ItemStack a : listA) {
            boolean found = false;
            Iterator it = listBCopy.iterator();
            while (it.hasNext()) {
                ItemStack b = (ItemStack)it.next();
                if (!StackUtil.isStackEqual(a, b)) continue;
                found = true;
                ret.add(StackUtil.copyWithSize(a, a.stackSize + b.stackSize));
                it.remove();
                break;
            }
            if (found) continue;
            return null;
        }
        return ret;
    }
}

