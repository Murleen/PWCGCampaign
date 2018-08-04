package pwcg.campaign.io.json;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.api.IArmedServiceManager;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.factory.ArmedServiceFactory;
import pwcg.campaign.personnel.PersonnelReplacementsService;
import pwcg.campaign.plane.Equipment;
import pwcg.campaign.plane.EquippedPlane;
import pwcg.campaign.squadmember.Ace;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.ww1.country.RoFServiceManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.CampaignRemover;
import pwcg.core.utils.DateUtils;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.CampaignCacheBase;
import pwcg.testutils.CampaignCacheRoF;

@RunWith(MockitoJUnitRunner.class)
public class CampaignIOJsonTest
{    
    @Test
    public void campaignIOJsonTest() throws PWCGException
    {
        PWCGContextManager.setRoF(true);

        deleteCampaign();
        writeCampaign();
        readCampaign();
        deleteCampaign();
    }

    private void writeCampaign() throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(CampaignCacheRoF.JASTA_11_PROFILE);
        CampaignIOJson.writeJson(campaign);
    }

    private void readCampaign() throws PWCGException
    {
        Campaign campaign = new Campaign();
        campaign.open(CampaignCacheBase.TEST_CAMPAIGN_NAME);
        PWCGContextManager.getInstance().setCampaign(campaign);

        validateCoreCampaign(campaign);        
        validateFighterSquadronMembers(campaign);        
        validateReconSquadronMembers(campaign);        
    	validatePersonnelReplacements(campaign);
    	validateFighterEquipment(campaign);
    	validateReconEquipment(campaign);
    }

    private void validateCoreCampaign(Campaign campaign) throws PWCGException
    {
        assert (campaign.getPlayer().getSerialNumber() >= SerialNumber.PLAYER_STARTING_SERIAL_NUMBER && campaign.getPlayer().getSerialNumber() < SerialNumber.AI_STARTING_SERIAL_NUMBER);
        assert (campaign.getDate().equals(DateUtils.getDateYYYYMMDD("19170501")));
        assert (campaign.getSquadronId() == 501011);
        assert (campaign.getCampaignData().getName().equals(CampaignCacheBase.TEST_CAMPAIGN_NAME));
        assert (campaign.getPlayer().getName().equals(CampaignCacheBase.TEST_PLAYER_NAME));
    }

    private void validatePersonnelReplacements(Campaign campaign) throws PWCGException
    {
        IArmedServiceManager armedServiceManager = ArmedServiceFactory.createServiceManager();
    	ArmedService germanArmedService = armedServiceManager.getArmedServiceByName(RoFServiceManager.DEUTSCHE_LUFTSTREITKRAFTE_NAME, campaign.getDate());
        PersonnelReplacementsService germanReplacements = campaign.getPersonnelManager().getPersonnelReplacementsService(germanArmedService.getServiceId());
        assert(germanReplacements.getReplacements().getActiveCount(campaign.getDate()) == 20);
        assert(germanReplacements.getDailyReplacementRate() == 15);
        assert(germanReplacements.getLastReplacementDate().equals(campaign.getDate()));

        ArmedService belgianArmedService = armedServiceManager.getArmedServiceByName(RoFServiceManager.AVIATION_MILITAIRE_BELGE_NAME, campaign.getDate());
        PersonnelReplacementsService belgianReplacements = campaign.getPersonnelManager().getPersonnelReplacementsService(belgianArmedService.getServiceId());
        assert(belgianReplacements.getReplacements().getActiveCount(campaign.getDate()) == 20);
        assert(belgianReplacements.getDailyReplacementRate() == 1);
    }

    private void validateReconSquadronMembers(Campaign campaign) throws PWCGException
    {
        Map<Integer, SquadronMember> reconSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(101002).getActiveSquadronMembers().getSquadronMemberCollection();
        assert (reconSquadronPersonnel.size() == 12);
        for (SquadronMember squadronMember : reconSquadronPersonnel.values())
        {
            assert (squadronMember.getSerialNumber() > SerialNumber.AI_STARTING_SERIAL_NUMBER);
            assert (squadronMember.getMissionFlown() > 0);
        }
    }

    private void validateFighterSquadronMembers(Campaign campaign) throws PWCGException
    {
        Map<Integer, SquadronMember> fighterSquadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(501011).getActiveSquadronMembersWithAces().getSquadronMemberCollection();
        assert (campaign.getSerialNumber().getNextPilotSerialNumber() > SerialNumber.AI_STARTING_SERIAL_NUMBER + 100);
        assert (fighterSquadronPersonnel.size() >= 12);
        for (SquadronMember squadronMember : fighterSquadronPersonnel.values())
        {
            if (squadronMember.isPlayer())
            {
                assert (squadronMember.getSerialNumber() >= SerialNumber.PLAYER_STARTING_SERIAL_NUMBER);
                assert (squadronMember.getMissionFlown() == 0);
            }
            else if (squadronMember instanceof Ace)
            {
                assert (squadronMember.getSerialNumber() >= SerialNumber.ACE_STARTING_SERIAL_NUMBER);
                assert (squadronMember.getMissionFlown() > 0);
            }
            else
            {
                assert (squadronMember.getSerialNumber() > SerialNumber.AI_STARTING_SERIAL_NUMBER);
                assert (squadronMember.getMissionFlown() > 0);
            }
        }
    }

    private void validateFighterEquipment(Campaign campaign) throws PWCGException
    {
        Equipment fighterSquadronEquipment = campaign.getEquipmentManager().getEquipmentForSquadron(501011);
        assert (campaign.getSerialNumber().getNextPlaneSerialNumber() > SerialNumber.PLANE_STARTING_SERIAL_NUMBER + 100);
        assert (fighterSquadronEquipment.getActiveEquippedPlanes().size() >= 14);
        for (EquippedPlane equippedPlane : fighterSquadronEquipment.getActiveEquippedPlanes().values())
        {
            assert (equippedPlane.getSerialNumber() > SerialNumber.PLANE_STARTING_SERIAL_NUMBER);
            assert (equippedPlane.getArchType().equals("albatrosd"));
        }
    }

    private void validateReconEquipment(Campaign campaign) throws PWCGException
    {
        Equipment reconSquadronEquipment = campaign.getEquipmentManager().getEquipmentForSquadron(101002);
        assert (campaign.getSerialNumber().getNextPlaneSerialNumber() > SerialNumber.PLANE_STARTING_SERIAL_NUMBER + 100);
        assert (reconSquadronEquipment.getActiveEquippedPlanes().size() >= 14);
        for (EquippedPlane equippedPlane : reconSquadronEquipment.getActiveEquippedPlanes().values())
        {
            assert (equippedPlane.getSerialNumber() > SerialNumber.PLANE_STARTING_SERIAL_NUMBER);
            assert (equippedPlane.getArchType().equals("re8") || equippedPlane.getArchType().equals("sopstr"));
        }
    }

    private void deleteCampaign()
    {
        CampaignRemover campaignRemover = new CampaignRemover();
        campaignRemover.deleteCampaign(CampaignCacheBase.TEST_CAMPAIGN_NAME);
    }
}
