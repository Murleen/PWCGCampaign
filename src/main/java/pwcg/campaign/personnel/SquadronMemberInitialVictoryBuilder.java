package pwcg.campaign.personnel;

import java.util.Calendar;
import java.util.Date;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.outofmission.BeforeCampaignVictimGenerator;
import pwcg.campaign.outofmission.OutOfMissionVictoryGenerator;
import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.Victory;
import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.ww2.country.BoSServiceManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.RandomNumberGenerator;

public class SquadronMemberInitialVictoryBuilder
{
    private Campaign campaign;
    private Squadron victorSquadron;
    private int minVictories;
    private int maxVictories;

    public SquadronMemberInitialVictoryBuilder(Campaign campaign, Squadron squadron)
    {
        this.campaign = campaign;
        this.victorSquadron = squadron;
    }
    
    public void createPilotVictories(SquadronMember newPilot, int rankPos) throws PWCGException
    {
        PlaneType squadPlane = victorSquadron.determineCurrentAircraftList(campaign.getDate()).get(0);

        if (!squadPlane.isRole(Role.ROLE_FIGHTER))
        {
            return;
        }
        
        if (rankPos > 3)
        {
            return;
        }

        initializeVictoriesFromRank(rankPos);
        factorServiceQuality(rankPos);
        factorSquadronQuality(rankPos);
        factorLuftwaffe(rankPos);
        resetForEarlyWWI(rankPos);
        
        if (victorSquadron.getSquadronId() == 102362377) 
        {
            System.out.println("max victories for rank " + rankPos + " is between " + minVictories + " and " + maxVictories);
        }

        int victories = calcNumberOfVictories(minVictories, maxVictories);
        addVictories(newPilot, victories);
    }

    private void initializeVictoriesFromRank(int rankPos) throws PWCGException
    {
        if (rankPos == 3)
        {
            minVictories = 0;
            maxVictories = 0;
        }
        else if (rankPos == 2)
        {
            minVictories = 0;
            maxVictories = 2;
        }
        else if (rankPos == 1)
        {
            minVictories = 2;
            maxVictories = 5;
        }
        else if (rankPos == 0)
        {
            minVictories = 3;
            maxVictories = 8;
        }
    }

    private void factorServiceQuality(int rankPos) throws PWCGException
    {
        ArmedService service = victorSquadron.determineServiceForSquadron(campaign.getDate());
        int serviceQuality = service.getServiceQuality().getQuality(campaign.getDate()).getQualityValue();

        int minAdjustment = 0;
        int maxAdjustment = 0;
        if (rankPos == 2)
        {
            minAdjustment = (serviceQuality / 10) - 10;
            maxAdjustment = (serviceQuality / 10) - 5;
        }
        else if (rankPos == 1)
        {
            minAdjustment = (serviceQuality / 10) - 8;
            maxAdjustment = (serviceQuality / 10) - 4;
        }
        else if (rankPos == 0)
        {
            minAdjustment = (serviceQuality / 10) - 5;
            maxAdjustment = (serviceQuality / 10) - 3;
        }

        minVictories += minAdjustment;
        maxVictories += maxAdjustment;
    }

    private void factorSquadronQuality(int rankPos) throws PWCGException
    {
        int squadronQuality = victorSquadron.determineSquadronSkill(campaign.getDate());
        
        int minAdjustment = 0;
        int maxAdjustment = 0;
        if (rankPos == 2)
        {
            minAdjustment = (squadronQuality / 10) - 8;
            maxAdjustment = (squadronQuality / 10) - 4;
        }
        else if (rankPos == 1)
        {
            minAdjustment = (squadronQuality / 10) - 7;
            maxAdjustment = (squadronQuality / 10) - 4;
        }
        else if (rankPos == 0)
        {
            minAdjustment = (squadronQuality / 10) - 5;
            maxAdjustment = (squadronQuality / 10) - 3;
        }

        minVictories += minAdjustment;
        maxVictories += maxAdjustment;
    }


    private void factorLuftwaffe(int rankPos) throws PWCGException
    {
        ArmedService service = victorSquadron.determineServiceForSquadron(campaign.getDate());
        int serviceQuality = service.getServiceQuality().getQuality(campaign.getDate()).getQualityValue();

        if (service.getServiceId() == BoSServiceManager.LUFTWAFFE)
        {
            if (serviceQuality > 80)
            {
                int extraLuftwaffeVictories = (serviceQuality - 80) * 2;
                int minAdjustment = 0;
                int maxAdjustment = 0;
                if (rankPos == 3)
                {
                    minAdjustment = 0;
                    maxAdjustment = extraLuftwaffeVictories / 6;
                }
                else if (rankPos == 2)
                {
                    minAdjustment = extraLuftwaffeVictories / 8;
                    maxAdjustment = extraLuftwaffeVictories / 6;
                }
                else if (rankPos == 1)
                {
                    minAdjustment = extraLuftwaffeVictories / 5;
                    maxAdjustment = extraLuftwaffeVictories / 3;
                }
                else if (rankPos == 0)
                {
                    minAdjustment = extraLuftwaffeVictories / 4;
                    maxAdjustment = extraLuftwaffeVictories / 2;
                }
                                
                minVictories += minAdjustment;
                maxVictories += maxAdjustment;
            }
        }
    }

    private void resetForEarlyWWI(int rankPos) throws PWCGException
    {
        Date startOfRealScoring = DateUtils.getDateWithValidityCheck("01/03/1917");
        if (campaign.getDate().before(startOfRealScoring))
        {
            if (rankPos >= 2)
            {
                minVictories = 0;
                maxVictories = 0;
            }
            else if (rankPos == 1)
            {
                minVictories = 0;
                maxVictories = 3;
            }
            else if (rankPos == 0)
            {
                minVictories = 1;
                maxVictories = 5;
            }
        }
    }

    private int calcNumberOfVictories(int minPossibleVictories, int maxAdditionalVictories)
    {
        int victories = RandomNumberGenerator.getRandom(maxVictories - minVictories) + minVictories;
        
        if (victories < 0)
        {
            victories = 0;
        }
        
        return victories;
    }

    private void addVictories(SquadronMember newPilot, int victories) throws PWCGException
    {
        for (int i = victories; i > 0; --i)
        {
            Date victoryDate = generateVictoryDate(i);
            
            EnemySquadronFinder enemySquadronFinder = new EnemySquadronFinder(campaign);
            Squadron victimSquadron = enemySquadronFinder.getRandomEnemyViableSquadron(victorSquadron, victoryDate);
            
            if (victimSquadron != null)
            {
                BeforeCampaignVictimGenerator beforeCampaignVictimGenerator = new BeforeCampaignVictimGenerator(campaign, victimSquadron, victoryDate);
                
                OutOfMissionVictoryGenerator outOfMissionVictoryGenerator = new OutOfMissionVictoryGenerator(victimSquadron, beforeCampaignVictimGenerator, newPilot);
                Victory victory = outOfMissionVictoryGenerator.generateOutOfMissionVictory(victoryDate);
                if (victory != null)
                {
                    newPilot.addVictory(victory);
                }
            }
        }
    }

    private Date generateVictoryDate(int i) throws PWCGException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(campaign.getDate());
        calendar.add(Calendar.DAY_OF_YEAR, (3 * i * -1));
        Date victoryDate = calendar.getTime();

        victoryDate = BeforeCampaignDateFinder.useEarliestPossibleDate(victoryDate);
        return victoryDate;
    }
}
