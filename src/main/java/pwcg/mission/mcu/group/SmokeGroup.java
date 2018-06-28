package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.plane.Plane;
import pwcg.mission.mcu.McuActivate;
import pwcg.mission.mcu.McuCheckZone;
import pwcg.mission.mcu.McuDeactivate;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.effect.Effect;
import pwcg.mission.mcu.effect.EffectCommand;
import pwcg.mission.mcu.effect.SmokeCity;
import pwcg.mission.mcu.effect.SmokeCitySmall;
import pwcg.mission.mcu.effect.SmokeVillage;

public class SmokeGroup
{
    private MissionBeginUnit missionBeginUnit = new MissionBeginUnit();
    
    private McuTimer smokeActivateTriggeredTimer = new McuTimer();
    private McuTimer smokeInitiateTimer = new McuTimer();
    private McuTimer smokeDectivateTriggeredTimer = new McuTimer();
    private McuTimer smokeStartTimer = new McuTimer();
    private McuTimer smokeStopTimer = new McuTimer();
    private McuTimer smokeLoopTimer = new McuTimer();
    private McuTimer deactivateCzTimer = new McuTimer();
    
    private McuCheckZone activateCheckZone;
    private McuCheckZone deactivateCheckZone;
    
    private McuActivate activateSmoke = new McuActivate();
    private McuDeactivate deactivateSmoke = new McuDeactivate();
    
    private List<Effect> smokeEffects = new ArrayList<>();
    private EffectCommand startSmokeEffectCommand = new EffectCommand(EffectCommand.START_EFFECT);
    private EffectCommand stopSmokeEffectCommand = new EffectCommand(EffectCommand.STOP_EFFECT);
    
    private Coordinate position;

    public SmokeGroup()
    {
    }

    public void buildSmokeGroup(Mission mission, Coordinate smokeEffectPosition, SmokeEffect requestedSmokeEffect) throws PWCGException 
    {
        this.position = smokeEffectPosition;
        missionBeginUnit.initialize(position.copy());

        Flight playerFlight = mission.getMissionFlightBuilder().getPlayerFlight();
        Plane plane = playerFlight.getPlayerPlane();
        
        addSmokeEffect(requestedSmokeEffect, smokeEffectPosition);
        
        buildActivate(plane);
        buildDeactivate(plane);
        setTimers();
        setTargetAssociations();
        setObjectAssociations();
        setPositions();
        setNames();
    }

    private void addSmokeEffect(SmokeEffect requestedSmokeEffect, Coordinate smokeEffectPosition) throws PWCGException
    {
        Effect smokeEffect = null;
        if (requestedSmokeEffect == SmokeEffect.SMOKE_CITY)
        {
            smokeEffect = new SmokeCity();
        }
        else  if (requestedSmokeEffect == SmokeEffect.SMOKE_CITY_SMALL)
        {
            smokeEffect = new SmokeCitySmall();
        }
        else
        {
            smokeEffect = new SmokeVillage();
        }
        
        smokeEffect.setPosition(smokeEffectPosition);
        
        smokeEffect.populateEntity();
        smokeEffects.add(smokeEffect);
    }
    
    private void setPositions()
    {
        smokeActivateTriggeredTimer.setPosition(position.copy());
        smokeInitiateTimer.setPosition(position.copy());
        smokeDectivateTriggeredTimer.setPosition(position.copy());
        smokeStartTimer.setPosition(position.copy());
        smokeStopTimer.setPosition(position.copy());
        smokeLoopTimer.setPosition(position.copy());
        deactivateCzTimer.setPosition(position.copy());
        activateCheckZone.setPosition(position.copy());
        deactivateCheckZone.setPosition(position.copy());
        activateSmoke.setPosition(position.copy());
        deactivateSmoke.setPosition(position.copy());
        startSmokeEffectCommand.setPosition(position.copy());
        stopSmokeEffectCommand.setPosition(position.copy());

        for (Effect smokeEffect : smokeEffects)
        {
            smokeEffect.setPosition(position.copy());
        }
    }
    
    private void setNames()
    {
        smokeActivateTriggeredTimer.setName("smokeTriggeredTimer");
        smokeInitiateTimer.setName("smokeInitiateTimer");
        smokeDectivateTriggeredTimer.setName("smokeDectivateTimer");
        smokeStartTimer.setName("smokeStartTimer");
        smokeStopTimer.setName("smokeStopTimer");
        smokeLoopTimer.setName("smokeLoopTimer");
        deactivateCzTimer.setName("deactivateCzTimer");
        activateCheckZone.setName("activateCheckZone");
        deactivateCheckZone.setName("deactivateCheckZone");
        activateSmoke.setName("activateSmoke");
        deactivateSmoke.setName("deactivateSmoke");
        startSmokeEffectCommand.setName("startSmokeEffectCommand");
        stopSmokeEffectCommand.setName("stopSmokeEffectCommand");

        for (Effect smokeEffect : smokeEffects)
        {
            smokeEffect.setName("smokeEffect");
        }
    }

    private void setTimers()
    {
        smokeActivateTriggeredTimer.setTimer(1);
        smokeInitiateTimer.setTimer(1);
        smokeDectivateTriggeredTimer.setTimer(1);
        smokeLoopTimer.setTimer(14);
        smokeStartTimer.setTimer(1);
        smokeStopTimer.setTimer(1);        
    }

    private void setTargetAssociations()
    {        
        // Activate smoke loop
        missionBeginUnit.linkToMissionBegin(activateCheckZone.getIndex());
        activateCheckZone.setTarget(smokeActivateTriggeredTimer.getIndex());
        smokeActivateTriggeredTimer.setTarget(activateSmoke.getIndex());
        activateSmoke.setTarget(smokeLoopTimer.getIndex());

        // Initiate Smoke
        smokeActivateTriggeredTimer.setTarget(smokeInitiateTimer.getIndex());
        smokeInitiateTimer.setTarget(smokeStopTimer.getIndex());
        
        // Smoke loop
        smokeStopTimer.setTarget(smokeStartTimer.getIndex());
        smokeStartTimer.setTarget(smokeLoopTimer.getIndex());
        smokeLoopTimer.setTarget(smokeStopTimer.getIndex());

        // Deactivate
        activateCheckZone.setTarget(deactivateCzTimer.getIndex());
        deactivateCzTimer.setTarget(deactivateCheckZone.getIndex());
        deactivateCheckZone.setTarget(smokeDectivateTriggeredTimer.getIndex());
        smokeDectivateTriggeredTimer.setTarget(deactivateSmoke.getIndex());
        deactivateSmoke.setTarget(smokeLoopTimer.getIndex());
        
        // Smoke stop and start effect commands
        smokeStopTimer.setTarget(stopSmokeEffectCommand.getIndex());
        smokeStartTimer.setTarget(startSmokeEffectCommand.getIndex());
                
    }

    private void setObjectAssociations()
    {
        for (Effect smokeEffect : smokeEffects)
        {
            startSmokeEffectCommand.setObject(smokeEffect.getLinkTrId());
            stopSmokeEffectCommand.setObject(smokeEffect.getLinkTrId());
        }
    }

    private void buildDeactivate(Plane plane)
    {
        deactivateCheckZone = new McuCheckZone(plane.getLinkTrId());
        deactivateCheckZone.setCloser(0);
        deactivateCheckZone.setZone(25000);
    }

    private void buildActivate(Plane plane)
    {
        activateCheckZone = new McuCheckZone(plane.getLinkTrId());
        activateCheckZone.setCloser(1);
        activateCheckZone.setZone(20000);
    }

    public void write(BufferedWriter writer) throws PWCGException 
    {
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
    
            missionBeginUnit.write(writer);
    
            smokeActivateTriggeredTimer.write(writer);
            smokeInitiateTimer.write(writer);
            smokeDectivateTriggeredTimer.write(writer);
            smokeStartTimer.write(writer);
            smokeStopTimer.write(writer);
            smokeLoopTimer.write(writer);
            deactivateCzTimer.write(writer);
            activateCheckZone.write(writer);
            deactivateCheckZone.write(writer);
            activateSmoke .write(writer);
            deactivateSmoke.write(writer);
            startSmokeEffectCommand.write(writer);
            stopSmokeEffectCommand.write(writer);
    
            for (Effect smokeEffect : smokeEffects)
            {
                smokeEffect.write(writer);
            }

            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    public Coordinate getPosition()
    {
        return position;
    }
}
