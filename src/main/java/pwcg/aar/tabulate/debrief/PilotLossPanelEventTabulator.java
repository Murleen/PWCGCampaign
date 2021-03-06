package pwcg.aar.tabulate.debrief;

import java.util.HashMap;
import java.util.Map;

import pwcg.aar.data.AARContext;
import pwcg.aar.ui.display.model.AARPilotLossPanelData;
import pwcg.aar.ui.events.PilotStatusEventGenerator;
import pwcg.aar.ui.events.model.PilotStatusEvent;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;

public class PilotLossPanelEventTabulator
{
    private Campaign campaign;
    private AARContext aarContext;

    private AARPilotLossPanelData pilotLossPanelData = new AARPilotLossPanelData();

    public PilotLossPanelEventTabulator (Campaign campaign,AARContext aarContext)
    {
        this.campaign = campaign;
        this.aarContext = aarContext;
    }
        
    public AARPilotLossPanelData tabulateForAARPilotLossPanel() throws PWCGException
    {
        PilotStatusEventGenerator pilotLossEventGenerator = new PilotStatusEventGenerator(campaign);
        Map<Integer, PilotStatusEvent> allPilotsLost = determineAllPilotsLostInMission(pilotLossEventGenerator);
        pilotLossPanelData.setSquadMembersLost(allPilotsLost);
        return pilotLossPanelData;
    }

    private Map<Integer, PilotStatusEvent> determineAllPilotsLostInMission(PilotStatusEventGenerator pilotLossEventGenerator) throws PWCGException
    {
        Map<Integer, PilotStatusEvent> allPilotsLost = new HashMap<>();
        Map<Integer, PilotStatusEvent> pilotsLostInMission = pilotLossEventGenerator.createPilotLossEvents(aarContext.getReconciledInMissionData().getPersonnelLossesInMission());
        allPilotsLost.putAll(pilotsLostInMission);
        
        Map<Integer, PilotStatusEvent> pilotsLostElapsedTime = pilotLossEventGenerator.createPilotLossEvents(aarContext.getReconciledOutOfMissionData().getPersonnelLossesOutOfMission());
        allPilotsLost.putAll(pilotsLostElapsedTime);
        return allPilotsLost;
    }
}
