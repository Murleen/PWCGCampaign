package pwcg.gui.helper;

import pwcg.campaign.personnel.SquadronMemberFilter;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.plane.Equipment;
import pwcg.campaign.plane.EquippedPlane;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.Mission;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;

public class BriefingDataInitializer
{
    private Mission mission;
    
	public BriefingDataInitializer(Mission mission)
	{
        this.mission = mission;
	}
	
	public BriefingAssignmentData initializeFromMission(Squadron squadron) throws PWCGException
	{	    
	    BriefingAssignmentData briefingAssignmentData = new BriefingAssignmentData();
	    briefingAssignmentData.setSquadron(squadron);
        
	    SquadronPersonnel playerPersonnel = mission.getCampaign().getPersonnelManager().getSquadronPersonnel(squadron.getSquadronId());
        SquadronMembers squadronMembers = SquadronMemberFilter.filterActiveAIAndPlayerAndAcesNoWounded(
        		playerPersonnel.getSquadronMembersWithAces().getSquadronMemberCollection(), (mission.getCampaign().getDate()));
        for (SquadronMember squadronMember : squadronMembers.getSquadronMemberCollection().values())
        {
            briefingAssignmentData.addPilot(squadronMember);
        }
        
        Equipment squadronPlanes = mission.getCampaign().getEquipmentManager().getEquipmentForSquadron(squadron.getSquadronId());
        for (EquippedPlane squadronPlane : squadronPlanes.getActiveEquippedPlanes().values())
        {
            briefingAssignmentData.addPlane(squadronPlane);
        }
	    
        IFlight playerFlight = mission.getMissionFlightBuilder().getPlayerFlightForSquadron(squadron.getSquadronId());
	    for (PlaneMcu plane : playerFlight.getFlightData().getFlightPlanes().getPlanes())
	    {
	        briefingAssignmentData.assignPilot(plane.getPilot().getSerialNumber(), plane.getSerialNumber());
	    }
	    
	    return briefingAssignmentData;
	}
}
