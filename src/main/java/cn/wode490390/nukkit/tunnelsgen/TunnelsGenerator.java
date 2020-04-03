package cn.wode490390.nukkit.tunnelsgen;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.function.IntPredicate;

public class TunnelsGenerator extends Generator {

    protected static final BitSet TUNNNEL_N = new BitSet();
    protected static final BitSet TUNNNEL_S = new BitSet();
    protected static final BitSet TUNNNEL_W = new BitSet();
    protected static final BitSet TUNNNEL_E = new BitSet();
    protected static final BitSet TUNNNEL_U = new BitSet();
    protected static final BitSet TUNNNEL_D = new BitSet();
    protected static final BitSet CHAMBER = new BitSet();

    static {
        for (int i = 6; i < 10; ++i) {
            for (int j = 6; j < 10; ++j) {
                for (int k = 0; k <= 8; ++k) {
                    TUNNNEL_N.set(sectionPosToInt(i, j, k));
                    TUNNNEL_S.set(sectionPosToInt(i, j, 15 - k));
                    TUNNNEL_W.set(sectionPosToInt(k, j, i));
                    TUNNNEL_E.set(sectionPosToInt(15 - k, j, i));
                    TUNNNEL_U.set(sectionPosToInt(i, 15 - k, j));
                    TUNNNEL_D.set(sectionPosToInt(i, k, j));
                }
            }
        }

        for (int l = 5; l < 11; ++l) {
            for (int m = 5; m < 11; ++m) {
                for (int n = 5; n < 11; ++n) {
                    CHAMBER.set(sectionPosToInt(l, m, n));
                }
            }
        }
    }

    protected ChunkManager level;
    protected TunnelsGeneratorSettings settings;

    public TunnelsGenerator() {
        this(null);
    }

    public TunnelsGenerator(Map<String, Object> options) {
        this.settings = TunnelsGeneratorPlugin.getInstance().getSettings();
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for (int chunkY = 0; chunkY < 16; ++chunkY) {
            int cx = chunkX << 1;
            int cy = chunkY << 1;
            int cz = chunkZ << 1;

            NukkitRandom random = new NukkitRandom();
            IntPredicate predicate = i -> false;
            predicate = appendSide(random, cx + 1, cy, cz, predicate, TUNNNEL_E);
            predicate = appendSide(random, cx - 1, cy, cz, predicate, TUNNNEL_W);
            predicate = appendSide(random, cx, cy, cz + 1, predicate, TUNNNEL_S);
            predicate = appendSide(random, cx, cy, cz - 1, predicate, TUNNNEL_N);
            predicate = appendSide(random, cx, cy + 1, cz, predicate, TUNNNEL_U);
            predicate = appendSide(random, cx, cy - 1, cz, predicate, TUNNNEL_D);
            if (predicate.test(sectionPosToInt(8, 8, 8))) {
                predicate = predicate.or(CHAMBER::get);
            }

            int baseY = chunkY << 4;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        if (!predicate.test(sectionPosToInt(x, y, z))) {
                            chunk.setBlock(x, baseY + y, z, this.settings.getBlockId(), this.settings.getBlockMeta());
                        }
                    }
                    if (chunkY == 0) {
                        chunk.setBiome(x, z, EnumBiome.PLAINS.biome);
                    }
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 256, 0.5);
    }

    protected static IntPredicate appendSide(NukkitRandom random, int x, int y, int z, IntPredicate intPredicate, BitSet bitSet) {
        if (y < 0) {
            return intPredicate;
        }

        random.setSeed(x * 0x4f9939f508L + z * 0x1ef1565bd5L);
        random.setSeed(random.nextInt() * 0x4f9939f508L + y * 0x1ef1565bd5L);

        if (random.nextBoolean()) {
            return intPredicate.or(bitSet::get);
        }

        return intPredicate;
    }

    protected static int sectionPosToInt(int x, int y, int z) {
        return x << 8 | y << 4 | z;
    }
}
