package pwcg.aar.outofmission.phase1.elapsedtime;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.aar.awards.PromotionEventHandler;
import pwcg.aar.awards.PromotionEventHandlerFighter;
import pwcg.aar.awards.PromotionEventHandlerRecon;
import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMemberVictories;
import pwcg.campaign.squadmember.Victory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.VictoryMaker;

@RunWith(MockitoJUnitRunner.class)
public class PromotionEventHandlerTest
{
    private Campaign campaign;
    
    @Mock
    private SquadronMember squadronMember;
    
    @Mock
    private Squadron squadron;

    
    @Mock
    private SquadronMemberVictories squadronMemberVictories;

    @Before
    public void setupForTestEnvironment() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.FC);
        campaign = CampaignCache.makeCampaign(SquadronTestProfile.ESC_103_PROFILE);
        Mockito.when(squadronMember.getSquadronMemberVictories()).thenReturn(squadronMemberVictories);
    }

    @Test
    public void promoteCorporalToSergentRecon () throws PWCGException
    {     
        Mockito.when(squadronMember.determineService(ArgumentMatchers.<Date>any())).thenReturn(campaign.determinePlayerSquadrons().get(0).determineServiceForSquadron(campaign.getDate()));
        Mockito.when(squadronMember.getMissionFlown()).thenReturn(PromotionEventHandlerRecon.PilotRankMedMinMissions);
        Mockito.when(squadronMember.getRank()).thenReturn("Corporal");
        Mockito.when(squadronMember.determineSquadron()).thenReturn(squadron);
        Mockito.when(squadron.determineSquadronPrimaryRole(ArgumentMatchers.<Date>any())).thenReturn(Role.ROLE_RECON);

        PromotionEventHandler promotionEventHandler = new PromotionEventHandler(campaign);
        String promotion = promotionEventHandler.promoteNonHistoricalPilots(squadronMember);

        assert (promotion.equals("Sergent"));
    }

    @Test
    public void promoteCorporalToSergentFighter () throws PWCGException
    {     
        List<Victory> victories = VictoryMaker.makeMultipleAlliedVictories(PromotionEventHandlerFighter.PilotRankMedVictories, campaign.getDate());
        Mockito.when(squadronMemberVictories.getAirToAirVictories()).thenReturn(victories.size());

        Mockito.when(squadronMember.determineService(ArgumentMatchers.<Date>any())).thenReturn(campaign.determinePlayerSquadrons().get(0).determineServiceForSquadron(campaign.getDate()));
        Mockito.when(squadronMember.getMissionFlown()).thenReturn(PromotionEventHandlerFighter.PilotRankMedMinMissions);
        Mockito.when(squadronMember.getRank()).thenReturn("Corporal");
        Mockito.when(squadronMember.determineSquadron()).thenReturn(squadron);
        Mockito.when(squadron.determineSquadronPrimaryRole(ArgumentMatchers.<Date>any())).thenReturn(Role.ROLE_FIGHTER);

        PromotionEventHandler promotionEventHandler = new PromotionEventHandler(campaign);
        String promotion = promotionEventHandler.promoteNonHistoricalPilots(squadronMember);

        assert (promotion.equals("Sergent"));
    }

    @Test
    public void promoteCorporalToSergentFailedDueToDifferentRole () throws PWCGException
    {     
        Mockito.when(squadronMember.determineService(ArgumentMatchers.<Date>any())).thenReturn(campaign.determinePlayerSquadrons().get(0).determineServiceForSquadron(campaign.getDate()));
        Mockito.when(squadronMember.getMissionFlown()).thenReturn(PromotionEventHandlerFighter.PilotRankMedMinMissions);
        Mockito.when(squadronMember.getRank()).thenReturn("Corporal");
        Mockito.when(squadronMember.determineSquadron()).thenReturn(squadron);
        Mockito.when(squadron.determineSquadronPrimaryRole(ArgumentMatchers.<Date>any())).thenReturn(Role.ROLE_RECON);

        PromotionEventHandler promotionEventHandler = new PromotionEventHandler(campaign);
        String promotion = promotionEventHandler.promoteNonHistoricalPilots(squadronMember);

        assert (promotion.equals(PromotionEventHandler.NO_PROMOTION));
    }

}
