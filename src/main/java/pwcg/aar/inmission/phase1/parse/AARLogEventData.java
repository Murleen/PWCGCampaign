package pwcg.aar.inmission.phase1.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.aar.inmission.phase1.parse.event.IAType12;
import pwcg.aar.inmission.phase1.parse.event.IAType17;
import pwcg.aar.inmission.phase1.parse.event.IAType18;
import pwcg.aar.inmission.phase1.parse.event.IAType2;
import pwcg.aar.inmission.phase1.parse.event.IAType3;
import pwcg.aar.inmission.phase1.parse.event.IAType6;
import pwcg.aar.inmission.phase1.parse.event.IATypeBase;

public class AARLogEventData
{
    private List<IATypeBase> chronologicalATypes = new ArrayList<>();
    private List<IAType2> damageEvents = new ArrayList<>();
    private List<IAType3> destroyedEvents = new ArrayList<>();
    private List<IAType6> landingEvents = new ArrayList<>();
    private List<IAType17> waypointEvents = new ArrayList<>();
    private List<IAType18> bailoutEvents = new ArrayList<>();
    private Map<String, IAType12> bots = new HashMap<>();
    private Map<String, IAType12> vehicles = new HashMap<>();
    private Map<String, IAType12> turrets = new HashMap<>();
    
    private void addChronologicalAType(IATypeBase chronologicalAType)
    {
        this.chronologicalATypes.add(chronologicalAType);
    }
    
    public void addDamageEvent(IAType2 damageEvent)
    {
        this.damageEvents.add(damageEvent);
        addChronologicalAType(damageEvent);
    }
    
    public void addDestroyedEvent(IAType3 destroyedEvent)
    {
        this.destroyedEvents.add(destroyedEvent);
        addChronologicalAType(destroyedEvent);
    }
    
    public void addLandingEvent(IAType6 landingEvent)
    {
        this.landingEvents.add(landingEvent);
        addChronologicalAType(landingEvent);
    }
    
    public void addWaypointEvent(IAType17 waypointEvent)
    {
        this.waypointEvents.add(waypointEvent);
        addChronologicalAType(waypointEvent);
    }
    
    public void addBailoutEvent(IAType18 bailoutEvent)
    {
        this.bailoutEvents.add(bailoutEvent);
        addChronologicalAType(bailoutEvent);
    }

    public void addBot(String logId, IAType12 bot)
    {
        this.bots.put(logId, bot);
    }

    public void addVehicle(String logId, IAType12 vehicle)
    {
        this.vehicles.put(logId, vehicle);
    }

    public void addTurret(String logId, IAType12 turret)
    {
        this.turrets.put(logId, turret);
    }

    public List<IATypeBase> getChronologicalATypes()
    {
        return chronologicalATypes;
    }

    public List<IAType2> getDamageEvents()
    {
        return damageEvents;
    }

    public List<IAType3> getDestroyedEvents()
    {
        return destroyedEvents;
    }

    public List<IAType6> getLandingEvents()
    {
        return landingEvents;
    }

    public List<IAType17> getWaypointEvents()
    {
        return waypointEvents;
    }
    
    public List<IAType18> getBailoutEvents()
    {
        return bailoutEvents;
    }

    public List<IAType12> getBots()
    {
        return new ArrayList<IAType12>(bots.values());
    }

    public List<IAType12> getVehicles()
    {
        return new ArrayList<IAType12>(vehicles.values());
    }
    
    public List<IAType12> getTurrets()
    {
        return new ArrayList<IAType12>(turrets.values());
    }

    public IAType12 getBot(String id)
    {
        return bots.get(id);
    }

    public IAType12 getVehicle(String id)
    {
        return vehicles.get(id);
    }

    public IAType12 getTurret(String id)
    {
        return turrets.get(id);
    }

    public boolean isVehicle(String id)
    {
        if (vehicles.containsKey(id))
        {
            return true;
        }
        
        return false;
    }

    public List<IAType2> getDamageForBot(String id)
    {
        List<IAType2> damageEventsForBot =  new ArrayList<IAType2>();

        for (IAType2 damageEvent : damageEvents)
        {
            if (damageEvent.getVictim().equals(id))
            {
                damageEventsForBot.add(damageEvent);
            }
        }
        
        return damageEventsForBot;
    }

    public IAType3 getDestroyedEvent(String id)
    {
        for (IAType3 destroyedEvent : destroyedEvents)
        {
            if (destroyedEvent.getVictim().equals(id))
            {
                return destroyedEvent;
            }
        }
        
        return null;
    }

    public IAType3 getDestroyedEventForPlaneByBot(String botId)
    {
        IAType12 botSpawn = bots.get(botId);
        if (botSpawn != null)
        {
            IAType12 planeSpawn = vehicles.get(getPlaneIdByBot(botSpawn));
            if (planeSpawn != null)
            {
                return getDestroyedEvent(planeSpawn.getId());
            }
        }
        
        return null;
    }

    public String getPlaneIdByBot(IAType12 atype12Bot) {
        String planeId = atype12Bot.getPid();

        // Work around a case where BoX might not give the AType12 event for a bot until after it has bailed out,
        // meaning the PID field is -1. Try to associate with a bailout event in this case
        if (planeId.equals(AARLogParser.UNKNOWN_MISSION_LOG_ENTITY))
        {
            for (IAType18 bailoutEvent : bailoutEvents)
            {
                if (bailoutEvent.getBotId().equals(atype12Bot.getId()))
                {
                    planeId = bailoutEvent.getVehicleId();
                    break;
                }
            }
        }

        return planeId;
    }

    public void setBots(Map<String, IAType12> bots)
    {
        this.bots = bots;
    }
}
