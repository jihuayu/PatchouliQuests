package jihuayu.patchoulitask.worldstorage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class TeamWorldSavedData extends WorldSavedData {
    public CompoundNBT teams = new CompoundNBT();
    public static final String ID = "pt_teams";

    public TeamWorldSavedData(String p_i2141_1_) {
        super(p_i2141_1_);
    }
    public TeamWorldSavedData() {
        super(ID);
    }

    @Override
    public void read(CompoundNBT nbt) {
        teams = nbt.getCompound("pt_teams");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("pt_teams",teams);
        return nbt;
    }

    public static TeamWorldSavedData getData(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(TeamWorldSavedData::new, ID);
    }

    public static CompoundNBT getTeam(ServerWorld world,String team)
    {
        return world.getSavedData().getOrCreate(TeamWorldSavedData::new, ID).teams.getCompound(team);
    }
}
