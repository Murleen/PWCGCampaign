package pwcg.mission
;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.mission.ambient.AmbientBattleBuilder2;
import pwcg.mission.ambient.AmbientTrainBuilder;
import pwcg.mission.ambient.AmbientTruckConvoyBuilder;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.ground.AAAManager;
import pwcg.mission.ground.IGroundUnitCollection;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.TestParticipatingHumanBuilder;

@RunWith(MockitoJUnitRunner.class)
public class AmbientBuilderTest
{    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
    }

    @Test
    public void createAmbientBattle () throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.STG77_PROFILE);
        MissionHumanParticipants participatingPlayers = TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign);
        CoordinateBox missionBorders = CoordinateBox.coordinateBoxFromCenter(new Coordinate(100000.0, 0.0, 100000.0), 75000);
        Mission mission = new Mission(campaign, participatingPlayers, missionBorders);
        mission.generate(FlightTypes.ANY);
        
        AmbientBattleBuilder2 ambientBattleBuilder = new AmbientBattleBuilder2(campaign, mission);
        List<AssaultDefinition> battles = ambientBattleBuilder.generateAmbientBattles();
        
        assert (battles.size() < 3);
        for (AssaultDefinition battle : battles)
        {
            assert(battle.getAggressor().getSide() != battle.getDefender().getSide());
            assert(battle.getGroundUnitCollection().getAllAlliedGroundUnits().size() > 0);
            assert(battle.getGroundUnitCollection().getAllAxisGroundUnits().size() > 0);
        }
    }

    @Test
    public void createAmbientTrucks () throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.STG77_PROFILE);
        MissionHumanParticipants participatingPlayers = TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign);
        CoordinateBox missionBorders = CoordinateBox.coordinateBoxFromCenter(new Coordinate(100000.0, 0.0, 100000.0), 75000);
        Mission mission = new Mission(campaign, participatingPlayers, missionBorders);
        mission.generate(FlightTypes.ANY);
        
        AmbientTruckConvoyBuilder ambientTruckConvoyBuilder = new AmbientTruckConvoyBuilder(campaign, mission);
        List<IGroundUnitCollection> ambientTrucks = ambientTruckConvoyBuilder.generateAmbientTrucks();

        assert (ambientTrucks.size() < 6);
        for (IGroundUnitCollection ambientTruck : ambientTrucks)
        {
            assert(ambientTruck.getCountry().getSide() == Side.ALLIED);
            assert(ambientTruck.getGroundUnit().getSpawners().size() > 1);
        }
    }

    @Test
    public void createAmbientTrains () throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.STG77_PROFILE);
        MissionHumanParticipants participatingPlayers = TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign);
        CoordinateBox missionBorders = CoordinateBox.coordinateBoxFromCenter(new Coordinate(100000.0, 0.0, 100000.0), 75000);
        Mission mission = new Mission(campaign, participatingPlayers, missionBorders);
        mission.generate(FlightTypes.ANY);
        
        AmbientTrainBuilder ambientTrainBuilder = new AmbientTrainBuilder(campaign, mission);
        List<IGroundUnitCollection> ambientTrains = ambientTrainBuilder.generateAmbientTrains();

        assert (ambientTrains.size() < 3);
        for (IGroundUnitCollection ambientTrain : ambientTrains)
        {
            assert(ambientTrain.getCountry().getSide() == Side.ALLIED);
            assert(ambientTrain.getGroundUnit().getSpawners().size() == 1);
        }
    }

    @Test
    public void createAmbientAAA () throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.STG77_PROFILE);
        MissionHumanParticipants participatingPlayers = TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign);
        CoordinateBox missionBorders = CoordinateBox.coordinateBoxFromCenter(new Coordinate(100000.0, 0.0, 100000.0), 75000);
        Mission mission = new Mission(campaign, participatingPlayers, missionBorders);
        mission.generate(FlightTypes.ANY);
        
        AAAManager aaaManager = new AAAManager(campaign, mission);
        List<IGroundUnitCollection> AAA = aaaManager.getAAAForMission();

        assert (AAA.size() > 10);
        for (IGroundUnitCollection aaaUnit : AAA)
        {
            assert(aaaUnit.getGroundUnit().getSpawners().size() > 0);
        }
    }
}
