package pwcg.aar.inmission.phase2.logeval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.aar.inmission.phase1.parse.AARLogEventData;
import pwcg.aar.inmission.phase1.parse.event.IAType12;
import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogAIEntity;
import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogBalloon;
import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogGroundUnit;
import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogPlane;
import pwcg.aar.prelim.PwcgMissionDataEvaluator;
import pwcg.campaign.plane.Balloon;
import pwcg.campaign.plane.Role;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;
import pwcg.core.utils.Logger.LogLevel;
import pwcg.mission.data.PwcgGeneratedMissionPlaneData;

public class AARVehicleBuilder
{
    private AARBotVehicleMapper botPlaneMapper;
    private AARVehiclePlaneLanded landedMapper;
    private PwcgMissionDataEvaluator pwcgMissionDataEvaluator;
    
    private Map <String, LogPlane> logPlanes = new HashMap<>();
    private Map <String, LogBalloon> logBalloons = new HashMap<>();
    private Map <String, LogGroundUnit> logGroundUnits = new HashMap<>();

    public AARVehicleBuilder(
            AARBotVehicleMapper botPlaneMapper,
            AARVehiclePlaneLanded landedMapper, 
            PwcgMissionDataEvaluator pwcgMissionDataEvaluator)
    {
        this.botPlaneMapper = botPlaneMapper;
        this.landedMapper = landedMapper;
        this.pwcgMissionDataEvaluator = pwcgMissionDataEvaluator;
    }
    
    public void buildVehicleListsByVehicleType(AARLogEventData logEventData) throws PWCGException 
    {
        sortVehiclesByType(logEventData.getVehicles());
        botPlaneMapper.mapBotsToCrews(logPlanes);        
        landedMapper.buildLandedLocations(logPlanes);
    }

    public LogAIEntity getVehicle(String id) throws PWCGException
    {
        if (logGroundUnits.containsKey(id))
        {
            return logGroundUnits.get(id);
        }
        else if (logPlanes.containsKey(id))
        {
            return logPlanes.get(id);
        }
        else if (logBalloons.containsKey(id))
        {
            return logBalloons.get(id);
        }

        return null;
    }    
    
    public LogAIEntity getPlaneByName(Integer serialNumber) throws PWCGException
    {
        for (LogPlane logPlane : logPlanes.values())
        {
            if (logPlane.isCrewMember(serialNumber))
            {
                return logPlane;
            }
        }

        return null;
    }

    private void sortVehiclesByType(List<IAType12> vehicleList) throws PWCGException
    {        
        for (IAType12 atype12 : vehicleList)
        {
            if (pwcgMissionDataEvaluator.wasPilotAssignedToMissionByName(atype12.getName()))
            {
                createLogPlane(atype12);
            }
            else if (Balloon.isBalloonName(atype12.getName()))
            {
                createLogBalloon(atype12);
            }
            else
            {
                createLogGroundUNit(atype12);
            }
        }
        
        if (logPlanes.isEmpty())
        {
        	throw new PWCGException("No planes found in logs to associate with the latest mission");
        }
    }

    private void createLogPlane(IAType12 atype12) throws PWCGException
    {        
        LogPlane logPlane = makePlaneFromMissionAndLog(atype12);
                        
        logPlanes.put(atype12.getId(), logPlane);
        Logger.log(LogLevel.DEBUG, "Add Plane: " + atype12.getName() + " ID:" + atype12.getId() + " Type:" + atype12.getType());
    }

    private LogPlane makePlaneFromMissionAndLog(IAType12 atype12) throws PWCGException
    {
        PwcgGeneratedMissionPlaneData missionPlane = pwcgMissionDataEvaluator.getPlaneForPilotByName(atype12.getName());
        LogPlane logPlane = new LogPlane();
        logPlane.initializeEntityFromEvent(atype12);
        logPlane.initializeFromMissionPlane(missionPlane);
        return logPlane;
    }

    private void createLogBalloon(IAType12 atype12) throws PWCGException
    {
        LogAIEntity logEntity;
        logEntity = new LogBalloon();
        logEntity.initializeEntityFromEvent(atype12);
        logEntity.setRole(Role.ROLE_BALLOON);
        
        logBalloons.put(atype12.getId(), (LogBalloon)logEntity);
        Logger.log(LogLevel.DEBUG, "Add Plane: " + atype12.getName() + " ID:" + atype12.getId() + " Type:" + atype12.getType());
    }

    private void createLogGroundUNit(IAType12 atype12) throws PWCGException
    {
        LogAIEntity logEntity;
        logEntity = new LogGroundUnit();
        logEntity.initializeEntityFromEvent(atype12);
        
        logGroundUnits.put(atype12.getId(), (LogGroundUnit)logEntity);
        Logger.log(LogLevel.DEBUG, "Add Entity: " + atype12.getName() + " ID:" + atype12.getId() + " Type:" + atype12.getType());
    }
    
    public Map<String, LogPlane> getLogPlanes()
    {
        return logPlanes;
    }

    public Map<String, LogBalloon> getLogBalloons()
    {
        return logBalloons;
    }

    public Map<String, LogGroundUnit> getLogGroundUNits()
    {
        return logGroundUnits;
    }
}
