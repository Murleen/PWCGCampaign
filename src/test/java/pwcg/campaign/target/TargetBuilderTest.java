package pwcg.campaign.target;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.Country;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.config.ConfigSimple;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.DateUtils;
import pwcg.mission.Mission;
import pwcg.mission.MissionGroundUnitResourceManager;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.ground.builder.BalloonUnitBuilder;
import pwcg.mission.ground.factory.TargetFactory;
import pwcg.mission.ground.org.GroundUnitType;
import pwcg.mission.ground.org.IGroundUnit;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.ground.vehicle.VehicleClass;
import pwcg.mission.target.TacticalTarget;
import pwcg.mission.target.TargetDefinition;

@RunWith(MockitoJUnitRunner.class)
public class TargetBuilderTest
{
    @Mock private TargetDefinition targetDefinition;
    @Mock private Campaign campaign;
    @Mock private Squadron squadron;
    @Mock private Mission mission;
    @Mock private ConfigManagerCampaign configManager;
    @Mock private FlightInformation flightInformation;
    
    private MissionGroundUnitResourceManager groundUnitResourceManager = new MissionGroundUnitResourceManager();

    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.KUBAN_MAP);
        
        ICountry friendlyCountry = CountryFactory.makeCountryByCountry(Country.GERMANY);
        ICountry enemyCountry = CountryFactory.makeCountryByCountry(Country.RUSSIA);
                
        Date date = DateUtils.getDateYYYYMMDD("1943030");
        Mockito.when(campaign.getCampaignConfigManager()).thenReturn(configManager);
        Mockito.when(campaign.getDate()).thenReturn(date);
        Mockito.when(targetDefinition.getTargetCountry()).thenReturn(enemyCountry);
        Mockito.when(targetDefinition.getAttackingCountry()).thenReturn(friendlyCountry);
        Mockito.when(targetDefinition.getTargetPosition()).thenReturn(new Coordinate(216336, 0, 184721));
        Mockito.when(targetDefinition.getTargetOrientation()).thenReturn(new Orientation(90));
        Mockito.when(configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigGroundKey)).thenReturn(ConfigSimple.CONFIG_LEVEL_MED);
        Mockito.when(mission.getMissionGroundUnitManager()).thenReturn(groundUnitResourceManager);
        Mockito.when(mission.getCampaign()).thenReturn(campaign);
        Mockito.when(flightInformation.getMission()).thenReturn(mission);
        Mockito.when(flightInformation.getSquadron()).thenReturn(squadron);
        Mockito.when(flightInformation.getCampaign()).thenReturn(campaign);
        Mockito.when(flightInformation.getTargetDefinition()).thenReturn(targetDefinition);
    }
    
    @Test
    public void createShippingTest() throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_SHIPPING);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.ANTI_SHIPPING_BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.TRANSPORT_UNIT);
        
        boolean shipUnitFound = false;
        if(groundUnit.getVehicleClass() == VehicleClass.ShipCargo || 
           groundUnit.getVehicleClass() == VehicleClass.ShipWarship || 
           groundUnit.getVehicleClass() == VehicleClass.Submarine)
        {
            shipUnitFound = true;
        }
        assert(shipUnitFound);
    }
    
    @Test
    public void createAssaultTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_ASSAULT);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() > 0);
        
        boolean tankUnitFound = false;
        boolean antitankUnitFound = false;
        for (IGroundUnit groundUnit : groundUnits.getGroundUnits())
        {
            if (groundUnit.getVehicleClass() == VehicleClass.Tank)
            {
                tankUnitFound = true;
            }
            if (groundUnit.getVehicleClass() == VehicleClass.ArtilleryAntiTank)
            {
                antitankUnitFound = true;
            }
        }
        
        assert(tankUnitFound);
        assert(antitankUnitFound);
    }
    
    @Test
    public void createTroopConcentrationTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_AAA);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.LOW_ALT_BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.AAA_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.AAAArtillery);
    }
    
    @Test
    public void createTransportTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_TRANSPORT);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 2);
        
        boolean truckUnitFound = false;
        boolean aatruckUnitFound = false;
        for (IGroundUnit groundUnit : groundUnits.getGroundUnits())
        {
            assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
            if (groundUnit.getVehicleClass() == VehicleClass.Truck)
            {
                truckUnitFound = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.TruckAAA)
            {
                aatruckUnitFound = true;
            }
        }
        assert(truckUnitFound);
        assert(aatruckUnitFound);
    }
    
    @Test
    public void createTrainTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_TRAIN);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.TRANSPORT_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.TrainLocomotive);
    }
    
    @Test
    public void createArtilleryBatteryTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_ARTILLERY);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.GROUND_ATTACK);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.ARTILLERY_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.ArtilleryHowitzer);
    }
    
    @Test
    public void createAirfieldTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_AIRFIELD);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.TRANSPORT_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.Truck);
    }
    
    @Test
    public void createBalloonDefenseTest()  throws PWCGException
    {
        BalloonUnitBuilder groundUnitBuilderBalloonDefense = new BalloonUnitBuilder(flightInformation.getCampaign(), flightInformation.getTargetDefinition());
        IGroundUnitCollection balloonUnit = groundUnitBuilderBalloonDefense.createBalloonUnit(CountryFactory.makeCountryByCountry(Country.GERMANY));
        
        assert(balloonUnit.getGroundUnits().size() == 3);
        boolean balloonUnitFound = false;
        boolean aaaMgUnitFound = false;
        boolean aaaArtilleryUnitFound = false;
        for (IGroundUnit groundUnit : balloonUnit.getGroundUnits())
        {
            assert(groundUnit.getCountry().getCountry() == Country.GERMANY);

            if (groundUnit.getVehicleClass() == VehicleClass.Balloon)
            {
                balloonUnitFound = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.AAAArtillery)
            {
                aaaArtilleryUnitFound = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.AAAMachineGun)
            {
                aaaMgUnitFound = true;
            }
        }
        assert(balloonUnitFound);
        assert(aaaMgUnitFound);
        assert(aaaArtilleryUnitFound);
    }
    
    @Test
    public void createBalloonBustTest()  throws PWCGException
    {
        BalloonUnitBuilder groundUnitBuilderBalloonDefense = new BalloonUnitBuilder(flightInformation.getCampaign(), flightInformation.getTargetDefinition());
        IGroundUnitCollection balloonUnit = groundUnitBuilderBalloonDefense.createBalloonUnit(CountryFactory.makeCountryByCountry(Country.RUSSIA));

        assert(balloonUnit.getGroundUnits().size() == 3);
        boolean balloonUnitFound = false;
        boolean aaaMgUnitFound = false;
        boolean aaaArtilleryUnitFound = false;
        for (IGroundUnit groundUnit : balloonUnit.getGroundUnits())
        {
            assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);

            if (groundUnit.getVehicleClass() == VehicleClass.Balloon)
            {
                balloonUnitFound = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.AAAArtillery)
            {
                aaaArtilleryUnitFound = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.AAAMachineGun)
            {
                aaaMgUnitFound = true;
            }
        }
        assert(balloonUnitFound);
        assert(aaaMgUnitFound);
        assert(aaaArtilleryUnitFound);
    }
    
    @Test
    public void createDrifterTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_DRIFTER);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.DIVE_BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 2);
        boolean drifter = false;
        boolean aaaDrifter = false;
        for (IGroundUnit groundUnit : groundUnits.getGroundUnits())
        {
            assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);

            if (groundUnit.getVehicleClass() == VehicleClass.Drifter)
            {
                drifter = true;
            }
            
            if (groundUnit.getVehicleClass() == VehicleClass.DrifterAAA)
            {
                aaaDrifter = true;
            }
        }
        assert(drifter);
        assert(aaaDrifter);
    }
    
    @Test
    public void createPortTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_PORT);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.AAA_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.AAAArtillery);
     }

    
    @Test
    public void createRailTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_RAIL);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.AAA_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.AAAArtillery);
    }

    @Test
    public void createFactoryTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_FACTORY);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.AAA_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.AAAArtillery);
    }
    
    @Test
    public void createCityTest()  throws PWCGException
    {
        Mockito.when(targetDefinition.getTargetType()).thenReturn(TacticalTarget.TARGET_CITY);
        Mockito.when(flightInformation.getFlightType()).thenReturn(FlightTypes.BOMB);
        TargetFactory targetBuilder = new TargetFactory(flightInformation);
        targetBuilder.buildTarget();
        IGroundUnitCollection groundUnits = targetBuilder.getGroundUnits();
        assert(groundUnits.getGroundUnits().size() == 1);
        IGroundUnit groundUnit = groundUnits.getGroundUnits().get(0);
        assert(groundUnit.getCountry().getCountry() == Country.RUSSIA);
        assert(groundUnit.getGroundUnitType() == GroundUnitType.AAA_UNIT);
        assert(groundUnit.getVehicleClass() == VehicleClass.AAAArtillery);
    }
}
