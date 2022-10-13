package pl.jordii.mcauth.bungee.database;

import pl.jordii.mcauth.bungee.database.model.SimpleSector;
import pl.jordii.mcauth.common.database.DataAccessObject;

import java.util.List;

public abstract class SectorDatabase implements DataAccessObject {

    public abstract List<SimpleSector> getSectors();

}
