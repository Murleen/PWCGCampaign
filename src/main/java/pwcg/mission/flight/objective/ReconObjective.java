package pwcg.mission.flight.objective;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.recon.ReconFlight;
import pwcg.mission.flight.recon.ReconFlight.ReconFlightTypes;

public class ReconObjective
{
    static String getMissionObjective(ReconFlight flight) throws PWCGException 
    {
        String objective = "";

        String objectiveName =  MissionObjective.formMissionObjectiveLocation(flight.getFlightInformation().getTargetPosition().copy());
        if (flight.getReconFlightType() == ReconFlightTypes.RECON_FLIGHT_TRANSPORT)
        {
            if (!objectiveName.isEmpty())
            {
                objective = "Perform reconnaissance at the specified transport hubs near " + objectiveName + 
                                ".  Photograph any troop movements or other items of interest.";
            }
            else
            {
                objective = "Perform reconnaissance at the specified transport hubs" + 
                                ".  Photograph any troop movements or other items of interest.";                
            }
        }
        else if (flight.getReconFlightType() == ReconFlightTypes.RECON_FLIGHT_AIRFIELD)
        {
            if (!objectiveName.isEmpty())
            {
                objective = "Perform reconnaissance at the specified airfields near " + objectiveName + 
                                ".  Photograph any aerial activity.";
            }
            else
            {
                objective = "Perform reconnaissance at the specified airfields" + 
                                ".  Photograph any aerial activity.";
            }
        }
        else
        {
            if (!objectiveName.isEmpty())
            {
                objective = "Perform reconnaissance near " + objectiveName + 
                                ".  Photograph any troop concentrations or other items of interest.";
            }
            else
            {
                objective = "Perform reconnaissance at the specified front location.  " + 
                                "Photograph any troop concentrations or other items of interest.";              
            }
        }
        
        return objective;
    }

}