package jihuayu.patchoulitask.worldstorage;

import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
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
        nbt.put("pt_teams", teams);
        return nbt;
    }

    public static TeamWorldSavedData getData(ServerWorld world) {
        return world.getSavedData().getOrCreate(TeamWorldSavedData::new, ID);
    }

    public static CompoundNBT getTeam(ServerWorld world, String team) {
        return world.getSavedData().getOrCreate(TeamWorldSavedData::new, ID).teams.getCompound(team);
    }

    public static ListNBT getTeamPlayers(ServerPlayerEntity playerEntity) {
        String name = NBTHelper.of(playerEntity.getPersistentData()).getString("patchouliquests.team.name");
        if (name != null && !name.isEmpty()) {
            CompoundNBT i = ((ServerWorld) playerEntity.world).getSavedData().getOrCreate(TeamWorldSavedData::new, ID).teams.getCompound(name);
            INBT j = i.get("members");
            if (j == null) {
                ListNBT list = new ListNBT();
                i.put("member", list);
                list.add(StringNBT.of(playerEntity.getUniqueID().toString()));
                return list;
            }
            return (ListNBT) j;
        }
        ListNBT list = new ListNBT();
        list.add(StringNBT.of(playerEntity.getUniqueID().toString()));
        return list;
    }

    public static String createTeam(ServerPlayerEntity playerEntity,String team){
        if(!((ServerWorld)playerEntity.world).getSavedData().getOrCreate(TeamWorldSavedData::new, ID).teams.getCompound(team).isEmpty()){
            return "Team name already exist!";
        }
        if (NBTHelper.of(playerEntity.getPersistentData()).getString("patchouliquests.team.name")!=null){
            return "You already in a team!";
        }
        NBTHelper.of(playerEntity.getPersistentData()).setString("patchouliquests.team.name",team);
        ((ServerWorld)playerEntity.world).getSavedData().getOrCreate(TeamWorldSavedData::new, ID).teams.put(team,playerEntity.getPersistentData().getCompound("patchouliquests"));
        return null;
    }
}
