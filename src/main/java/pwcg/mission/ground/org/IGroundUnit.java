package pwcg.mission.ground.org;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.campaign.api.ICountry;
import pwcg.core.constants.AiSkillLevel;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.ground.vehicle.IVehicle;
import pwcg.mission.ground.vehicle.VehicleClass;
import pwcg.mission.target.TargetType;

public interface IGroundUnit
{

    void createGroundUnit() throws PWCGException;
    void write(BufferedWriter writer) throws PWCGException;

    boolean isUnitEngagedInCombat();
    boolean isUnitMobile();

    ICountry getCountry() throws PWCGException;
    String getName() throws PWCGException;
    List<IVehicle> getVehicles();
    VehicleClass getVehicleClass();
    int getEntryPoint();
    int getDeleteEntryPoint();
    GroundUnitType getGroundUnitType();

    Coordinate getPosition() throws PWCGException;
    void setAiLevel(AiSkillLevel aiLevel);
    void validate() throws PWCGException;
    TargetType getTargetType();
}