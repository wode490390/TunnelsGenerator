package cn.wode490390.nukkit.tunnelsgen;

public class TunnelsGeneratorSettings {

    private final int blockId;
    private final int blockMeta;

    public TunnelsGeneratorSettings(int blockId, int blockMeta) {
        this.blockId = blockId;
        this.blockMeta = blockMeta;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public int getBlockMeta() {
        return this.blockMeta;
    }
}
