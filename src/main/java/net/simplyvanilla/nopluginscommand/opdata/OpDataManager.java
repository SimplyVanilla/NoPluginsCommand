package net.simplyvanilla.nopluginscommand.opdata;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OpDataManager {

    private final Path opFilePath;
    private final Gson gson;

    private final Map<UUID, OpData> opDataMap = new HashMap<>();

    public OpDataManager(Path opFilePath, Gson gson) {
        this.opFilePath = opFilePath;
        this.gson = gson;
    }

    private OpData getOpData(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isOp = player.isOp();
        boolean contains = opDataMap.containsKey(uuid);

        if (isOp && !contains) {
            loadOpData();
        } else if (!isOp && contains) {
            opDataMap.remove(uuid);
        }

        return opDataMap.get(uuid);
    }

    public int getPermissionLevel(Player player) {
        OpData opData = getOpData(player);
        if (opData == null) {
            return 0;
        } else {
            return opData.getLevel();
        }
    }

    private void loadOpData() {
        try (Reader reader = Files.newBufferedReader(opFilePath, StandardCharsets.UTF_8)) {
            List<OpData> opDataList = gson.fromJson(reader, new TypeToken<ArrayList<OpData>>(){}.getType());
            opDataList.forEach(opData -> opDataMap.put(opData.getUuid(), opData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
