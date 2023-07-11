/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IProgressUpdate
 *  net.minecraft.world.ChunkCoordIntPair
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraft.world.WorldSettings
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.EmptyChunk
 *  net.minecraft.world.chunk.IChunkProvider
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.commons.lang3.mutable.MutableLong
 */
package ic2.core.uu;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Player;
import ic2.core.init.MainConfig;
import ic2.core.util.Config;
import ic2.core.util.ConfigUtil;
import ic2.core.util.ItemStackWrapper;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.Util;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class DropScan {
    private EntityPlayer player;
    private final Map<ItemStackWrapper, MutableInt> drops = new HashMap<ItemStackWrapper, MutableInt>();
    private static final ConcurrentMap<BlockDesc, DropDesc> typicalDrops = new ConcurrentHashMap<BlockDesc, DropDesc>();
    private static final Object generatorLock = new Object();
    private static AtomicInteger uuScanProgress = new AtomicInteger();
    private static int uuScanAmount;

    public static void start(World world, int area, int areaCount, int range) {
        if (range < 3) {
            throw new IllegalArgumentException("range has to be at least 3");
        }
        uuScanProgress.set(0);
        uuScanAmount = areaCount;
        ArrayList<Callable<Calculation>> tasks = new ArrayList<Callable<Calculation>>(areaCount);
        for (int i = 0; i < areaCount; ++i) {
            int x = IC2.random.nextInt(area) - area / 2;
            int z = IC2.random.nextInt(area) - area / 2;
            tasks.add(new Calculation(world, x, z, range));
        }
        List futures = IC2.getInstance().threadPool.submitAll(tasks);
        DropScan.analyze(futures);
    }

    private static void analyze(List<? extends Future<Iterable<Map.Entry<ItemStack, Integer>>>> futures) {
        double d;
        HashMap<ItemStackWrapper, MutableLong> result = new HashMap<ItemStackWrapper, MutableLong>();
        for (Future<Iterable<Map.Entry<ItemStack, Integer>>> future : futures) {
            try {
                Iterable<Map.Entry<ItemStack, Integer>> partialResult = future.get();
                for (Map.Entry<ItemStack, Integer> entry : partialResult) {
                    ItemStackWrapper key = new ItemStackWrapper(entry.getKey());
                    MutableLong amount = (MutableLong)result.get(key);
                    if (amount == null) {
                        amount = new MutableLong();
                        result.put(key, amount);
                    }
                    amount.add((Number)entry.getValue());
                }
            }
            catch (Exception e) {
                IC2.log.warn(LogCategory.Uu, e, "Scan failed.");
            }
        }
        TreeSet<Map.Entry<ItemStackWrapper, MutableLong>> counts = new TreeSet<Map.Entry<ItemStackWrapper, MutableLong>>(new Comparator<Map.Entry<ItemStackWrapper, MutableLong>>(){

            @Override
            public int compare(Map.Entry<ItemStackWrapper, MutableLong> a, Map.Entry<ItemStackWrapper, MutableLong> b) {
                long ret = b.getValue().getValue() - a.getValue().getValue();
                return ret > 0L ? 1 : (ret < 0L ? -1 : 0);
            }
        });
        counts.addAll(result.entrySet());
        ItemStackWrapper cobblestone = new ItemStackWrapper(new ItemStack(Blocks.cobblestone));
        ItemStackWrapper netherrack = new ItemStackWrapper(new ItemStack(Blocks.netherrack));
        if (!result.containsKey(cobblestone)) {
            if (!result.containsKey(netherrack)) {
                IC2.log.warn(LogCategory.Uu, "UU scan failed, there was no cobblestone or netherrack dropped");
                return;
            }
            d = ((MutableLong)result.get(netherrack)).getValue().longValue();
        } else {
            d = ((MutableLong)result.get(cobblestone)).getValue().longValue();
            if (result.containsKey(netherrack)) {
                d = Math.max(d, (double)((MutableLong)result.get(netherrack)).getValue().longValue());
            }
        }
        Config config = MainConfig.get().getSub("balance/uu-values/world scan");
        if (config == null) {
            config = MainConfig.get().getSub("balance/uu-values").addSub("world scan", "Initial uu values from scanning the world.\nRun /ic2 uu-world-scan <small|medium|large> to calibrate them for your world.\nDelete this whole section to revert to the default predefined values.");
        }
        IC2.log.info(LogCategory.Uu, "total");
        for (Map.Entry entry : counts) {
            ItemStack stack = ((ItemStackWrapper)entry.getKey()).stack;
            long count = ((MutableLong)entry.getValue()).getValue();
            IC2.log.info(LogCategory.Uu, "%d %s", count, stack.getItem().getItemStackDisplayName(stack));
            config.set(ConfigUtil.fromStack(stack), d / (double)count);
        }
        MainConfig.save();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Iterable<Map.Entry<ItemStack, Integer>> scanArea(World originalWorld, int xStart, int zStart, int range) {
        IChunkProvider generator;
        DummyWorld world = new DummyWorld(originalWorld, range);
        world.getChunkProvider().generator = generator = world.provider.createChunkGenerator();
        ArrayList<Chunk> chunks = new ArrayList<Chunk>(Util.square(range));
        ArrayList<Chunk> pendingChunks = new ArrayList<Chunk>(Util.square(range - 2));
        Iterator iterator = generatorLock;
        synchronized (iterator) {
            for (int x = xStart; x < xStart + range; ++x) {
                for (int z = zStart; z < zStart + range; ++z) {
                    Chunk chunk = generator.provideChunk(x, z);
                    chunks.add(chunk);
                    if (x == xStart || x == xStart + range || z == zStart || z == zStart + range) continue;
                    pendingChunks.add(chunk);
                }
            }
            world.getChunkProvider().setChunks(chunks, xStart, zStart);
            for (Chunk chunk : pendingChunks) {
                chunk.populateChunk((IChunkProvider)world.getChunkProvider(), (IChunkProvider)world.getChunkProvider(), chunk.xPosition, chunk.zPosition);
            }
            world.getChunkProvider().disableGenerate();
        }
        this.player = new Ic2Player(world);
        for (Chunk chunk : pendingChunks) {
            this.scanChunk(world, chunk);
        }
        world.getChunkProvider().clear();
        ArrayList<Map.Entry<ItemStack, Integer>> counts = new ArrayList<Map.Entry<ItemStack, Integer>>(this.drops.size());
        for (Map.Entry<ItemStackWrapper, MutableInt> entry : this.drops.entrySet()) {
            counts.add(new AbstractMap.SimpleImmutableEntry<ItemStack, Integer>(entry.getKey().stack, entry.getValue().getValue()));
        }
        Collections.sort(counts, new Comparator<Map.Entry<ItemStack, Integer>>(){

            @Override
            public int compare(Map.Entry<ItemStack, Integer> a, Map.Entry<ItemStack, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });
        return counts;
    }

    private void scanChunk(DummyWorld world, Chunk chunk) {
        assert (world.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition) == chunk);
        int xMax = (chunk.xPosition + 1) * 16;
        int yMax = world.getHeight();
        int zMax = (chunk.zPosition + 1) * 16;
        for (int y = 0; y < yMax; ++y) {
            for (int z = chunk.zPosition * 16; z < zMax; ++z) {
                for (int x = chunk.xPosition * 16; x < xMax; ++x) {
                    Block block = chunk.getBlock(x & 0xF, y, z & 0xF);
                    if (block == Blocks.air) continue;
                    int meta = chunk.getBlockMetadata(x & 0xF, y, z & 0xF);
                    for (ItemStack drop : this.getDrops(world, x, y, z, block, meta)) {
                        this.addDrop(drop);
                    }
                }
            }
        }
    }

    private List<ItemStack> getDrops(DummyWorld world, int x, int y, int z, Block block, int meta) {
        BlockDesc key = new BlockDesc(block, meta);
        DropDesc typicalDrop = (DropDesc)typicalDrops.get(key);
        if (typicalDrop == null || typicalDrop.dropCount.get() < 1000) {
            DropDesc prevValue;
            block.onBlockHarvested((World)world, x, y, z, meta, this.player);
            if (block.removedByPlayer((World)world, this.player, x, y, z, false)) {
                block.onBlockDestroyedByPlayer((World)world, x, y, z, meta);
                block.dropBlockAsItem((World)world, x, y, z, meta, 0);
            } else {
                IC2.log.info(LogCategory.Uu, "Can't harvest %s.", block);
            }
            ArrayList<ItemStack> drops = new ArrayList<ItemStack>(world.spawnedEntities.size());
            for (Entity entity : world.spawnedEntities) {
                if (!(entity instanceof EntityItem)) continue;
                drops.add(((EntityItem)entity).getEntityItem());
            }
            world.spawnedEntities.clear();
            if (typicalDrop == null && (prevValue = typicalDrops.putIfAbsent(key, typicalDrop = new DropDesc(drops))) != null) {
                typicalDrop = prevValue;
            }
            if (typicalDrop.dropCount.get() >= 0) {
                boolean equal;
                boolean bl = equal = typicalDrop.drops.size() == drops.size();
                if (equal) {
                    Iterator it = drops.iterator();
                    Iterator<ItemStack> it2 = typicalDrop.drops.iterator();
                    while (it.hasNext()) {
                        ItemStack b;
                        ItemStack a = (ItemStack)it.next();
                        if (ItemStack.areItemStacksEqual((ItemStack)a, (ItemStack)(b = it2.next()))) continue;
                        equal = false;
                        break;
                    }
                }
                if (equal) {
                    int prev = typicalDrop.dropCount.incrementAndGet();
                    if (prev < 0) {
                        typicalDrop.dropCount.set(Integer.MIN_VALUE);
                    }
                } else {
                    typicalDrop.dropCount.set(Integer.MIN_VALUE);
                }
            }
            return drops;
        }
        return typicalDrop.drops;
    }

    private void addDrop(ItemStack stack) {
        ItemStackWrapper key = new ItemStackWrapper(stack);
        MutableInt amount = this.drops.get(key);
        if (amount == null) {
            amount = new MutableInt();
            this.drops.put(key, amount);
        }
        amount.add(stack.stackSize);
    }

    private static final class DropDesc {
        List<ItemStack> drops;
        AtomicInteger dropCount = new AtomicInteger();

        DropDesc(List<ItemStack> drops) {
            this.drops = drops;
        }
    }

    private static final class BlockDesc {
        private final Block block;
        private final int meta;

        BlockDesc(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        public int hashCode() {
            return this.block.hashCode() * 31 + this.meta;
        }

        public boolean equals(Object obj) {
            if (obj instanceof BlockDesc) {
                return ((BlockDesc)obj).block == this.block && ((BlockDesc)obj).meta == this.meta;
            }
            return false;
        }
    }

    static class DummyChunkProvider
    implements IChunkProvider {
        private final World world;
        private final Chunk emptyChunk;
        private final Map<Long, Chunk> extraChunks = new HashMap<Long, Chunk>();
        private final Chunk[] chunks;
        private final int range;
        private int xStart;
        private int zStart;
        private boolean disableGenerate;
        public IChunkProvider generator;

        public DummyChunkProvider(World world, int range) {
            this.world = world;
            this.emptyChunk = new EmptyChunk(world, 0, 0);
            this.chunks = new Chunk[Util.square(range)];
            this.range = range;
        }

        public void setChunks(List<Chunk> newChunks, int xStart, int zStart) {
            this.clear();
            this.xStart = xStart;
            this.zStart = zStart;
            for (Chunk chunk : newChunks) {
                int index = this.getIndex(chunk.xPosition, chunk.zPosition);
                if (index < 0) {
                    throw new IllegalArgumentException("out of range");
                }
                this.chunks[index] = chunk;
            }
        }

        public void disableGenerate() {
            this.disableGenerate = true;
        }

        public void clear() {
            this.extraChunks.clear();
            Arrays.fill(this.chunks, null);
        }

        public boolean canSave() {
            return false;
        }

        public boolean chunkExists(int x, int z) {
            int index = this.getIndex(x, z);
            if (index >= 0) {
                return this.chunks[index] != null;
            }
            return this.extraChunks.containsKey(ChunkCoordIntPair.chunkXZ2Int((int)x, (int)z));
        }

        public ChunkPosition func_147416_a(World world1, String s, int i, int j, int k) {
            return null;
        }

        public int getLoadedChunkCount() {
            return 1;
        }

        public List getPossibleCreatures(EnumCreatureType enumcreaturetype, int i, int j, int k) {
            return null;
        }

        public Chunk loadChunk(int i, int j) {
            return null;
        }

        public String makeString() {
            return "Dummy";
        }

        public void populate(IChunkProvider provider, int x, int z) {
            Chunk chunk = this.provideChunk(x, z);
            if (!chunk.isTerrainPopulated) {
                chunk.isTerrainPopulated = true;
                if (this.generator != null) {
                    this.generator.populate(provider, x, z);
                    GameRegistry.generateWorld((int)x, (int)z, (World)this.world, (IChunkProvider)this.generator, (IChunkProvider)provider);
                    chunk.setChunkModified();
                }
            }
        }

        public Chunk provideChunk(int x, int z) {
            int index = this.getIndex(x, z);
            Chunk ret = index >= 0 ? this.chunks[index] : this.extraChunks.get(ChunkCoordIntPair.chunkXZ2Int((int)x, (int)z));
            if (ret == null) {
                if (this.disableGenerate) {
                    return this.emptyChunk;
                }
                ret = this.generator.provideChunk(x, z);
                if (index >= 0) {
                    this.chunks[index] = ret;
                } else {
                    this.extraChunks.put(ChunkCoordIntPair.chunkXZ2Int((int)x, (int)z), ret);
                }
            }
            return ret;
        }

        public void recreateStructures(int i, int j) {
        }

        public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
            return true;
        }

        public void saveExtraData() {
        }

        public boolean unloadQueuedChunks() {
            return false;
        }

        private int getIndex(int x, int z) {
            if ((x -= this.xStart) < 0 || x >= this.range || (z -= this.zStart) < 0 || z >= this.range) {
                return -1;
            }
            return x * this.range + z;
        }
    }

    static class DummyWorld
    extends World {
        List<Entity> spawnedEntities = new ArrayList<Entity>();

        public DummyWorld(World world, int range) {
            super(world.getSaveHandler(), "DummyWorld", new WorldSettings(world.getWorldInfo()), WorldProvider.getProviderForDimension((int)world.provider.dimensionId), world.theProfiler);
            this.chunkProvider = new DummyChunkProvider(this, range);
            this.difficultySetting = world.difficultySetting;
        }

        public DummyChunkProvider getChunkProvider() {
            return (DummyChunkProvider)super.getChunkProvider();
        }

        protected IChunkProvider createChunkProvider() {
            return null;
        }

        public Entity getEntityByID(int i) {
            return null;
        }

        public boolean setBlock(int x, int y, int z, Block block, int meta, int flags) {
            if (y >= 256 || y < 0) {
                return false;
            }
            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            return chunk.func_150807_a(x & 0xF, y, z & 0xF, block, meta);
        }

        public boolean setBlockMetadataWithNotify(int x, int y, int z, int meta, int flags) {
            if (y >= 256 || y < 0) {
                return false;
            }
            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            return chunk.setBlockMetadata(x & 0xF, y, z & 0xF, meta);
        }

        public boolean updateLightByType(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
            return true;
        }

        public boolean spawnEntityInWorld(Entity entity) {
            this.spawnedEntities.add(entity);
            return true;
        }

        protected int func_152379_p() {
            return 3;
        }
    }

    static class Calculation
    implements Callable<Iterable<Map.Entry<ItemStack, Integer>>>,
    PriorityExecutor.CustomPriority {
        private final World world;
        private final int x;
        private final int z;
        private final int range;

        Calculation(World world1, int x1, int z1, int range1) {
            this.world = world1;
            this.x = x1;
            this.z = z1;
            this.range = range1;
        }

        @Override
        public Iterable<Map.Entry<ItemStack, Integer>> call() throws Exception {
            Iterable ret;
            String threadName = Thread.currentThread().getName();
            Thread.currentThread().setName("Server thread");
            try {
                ret = new DropScan().scanArea(this.world, this.x, this.z, this.range);
            }
            finally {
                Thread.currentThread().setName(threadName);
            }
            int done = uuScanProgress.incrementAndGet();
            if (done % 50 == 0) {
                IC2.log.info(LogCategory.Uu, "World scan progress: %d%%.", 100 * done / uuScanAmount);
            }
            return ret;
        }

        @Override
        public PriorityExecutor.Priority getPriority() {
            return PriorityExecutor.Priority.Low;
        }
    }
}

