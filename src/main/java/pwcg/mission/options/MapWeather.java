package pwcg.mission.options;

import java.util.ArrayList;
import java.util.Date;

import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.options.MapSeasonalParameters.Season;

public abstract class MapWeather 
{    
    protected String weatherDescription = "";   // Altitude
    protected String cloudConfig = "Oops";
    
	protected int cloudLevel = 2100;	// Altitude
	protected int cloudDensity = 600;	// Thickness
	protected int precLevel = 0;
	protected int precType = 0;
	protected int turbulence = 1;
	protected int tempPressLevel = 0;
	protected int temperature = 15;
	protected int pressure = 760;
	protected int windDirection = 0;
	
	protected Flight playerFlight = null;
	
	protected ArrayList<WindLayer> windLayers = new ArrayList<WindLayer> ();
	
    public MapWeather ()
    {
    }
    	
    public void createWindDirection()
    {
		// Prevailing direction is west to east
		int eastOrWest = RandomNumberGenerator.getRandom(100);
		if (eastOrWest > 80)
		{
			windDirection = 180 + RandomNumberGenerator.getRandom(180);
		}
		else
		{
			windDirection = RandomNumberGenerator.getRandom(180);
		}
    }

	public void createMissionWeather(Flight playerFlight) throws PWCGException 
	{		
	    this.playerFlight = playerFlight;
	    
	    int weatherSeverity = createCloud(playerFlight);
        
        createWind(weatherSeverity);
    }

    protected abstract String getClearSkys() throws PWCGException;
    protected abstract String getLightSkys() throws PWCGException;
    protected abstract String getAverageSkys() throws PWCGException;
    protected abstract String getHeavySkys() throws PWCGException;
    protected abstract String getOvercastSkys() throws PWCGException;
    public abstract Season getSeason(Date date);

    protected int createCloud(Flight playerFlight) throws PWCGException 
    {
        int weatherSeverity = 1;
        
        if (playerFlight.isNightFlight())
        {
            weatherSeverity = createNightCloud(playerFlight);
        }
        else
        {
            weatherSeverity = createDayCloud(playerFlight);
        }

        weatherDescription += "\n    Cloud layer is " + cloudLevel + " meters.";
        
        return weatherSeverity;
    }

	protected int createDayCloud(Flight playerFlight) throws PWCGException 
	{
        int weatherSeverity = 1;

        ConfigManagerCampaign configManager = playerFlight.getCampaign().getCampaignConfigManager();
        
        int weatherOverCastClouds = configManager.getIntConfigParam(ConfigItemKeys.WeatherOvercastCloudsKey);
        int weatherHeavyClouds = weatherOverCastClouds + configManager.getIntConfigParam(ConfigItemKeys.WeatherHeavyCloudsKey);
        int weatherAverageClouds = weatherHeavyClouds + configManager.getIntConfigParam(ConfigItemKeys.WeatherAverageCloudsKey);
        int weatherLightsClouds = weatherAverageClouds + configManager.getIntConfigParam(ConfigItemKeys.WeatherLightCloudsKey);
        int weatherClearClouds = weatherLightsClouds + configManager.getIntConfigParam(ConfigItemKeys.WeatherClearCloudsKey);

        FlightTypes playerFlightType= playerFlight.getFlightType();

        int sky = RandomNumberGenerator.getRandom(weatherClearClouds);
        if (sky < weatherOverCastClouds)  
        {
            weatherSeverity = setWeatherForOvercast(playerFlightType);
        }
        else if (sky < weatherHeavyClouds)  
        {
            weatherSeverity = setWeatherForHeavy(playerFlightType);
        }
        else if (sky < weatherAverageClouds)  
        {
            weatherSeverity = averageWeather();
        }
        else if (sky < weatherLightsClouds) 
        {
            weatherSeverity = lightWeather();
        }
        else
        {
            weatherSeverity = clearWeather();
        }

        setTurbulence(configManager);
		
		return weatherSeverity;
	}

    private void setTurbulence(ConfigManagerCampaign configManager) throws PWCGException
    {
        int maxTurbulence = configManager.getIntConfigParam(ConfigItemKeys.MaxTurbulenceKey);
        if (turbulence > maxTurbulence)
        {
            turbulence = maxTurbulence;
        }
    }

    private int setWeatherForHeavy(FlightTypes playerFlightType) throws PWCGException
    {
        int weatherSeverity;
        if (playerFlightType == FlightTypes.RECON           ||
            playerFlightType == FlightTypes.STRATEGIC_BOMB  ||
            playerFlightType == FlightTypes.DIVE_BOMB)
        {
            weatherSeverity = lightWeather();
        }
        else
        {
            weatherSeverity = heavyWeather();
        }
        return weatherSeverity;
    }

    private int setWeatherForOvercast(FlightTypes playerFlightType) throws PWCGException
    {
        int weatherSeverity;
        if (playerFlightType == FlightTypes.RECON           ||
            playerFlightType == FlightTypes.STRATEGIC_BOMB  ||
            playerFlightType == FlightTypes.GROUND_ATTACK   ||
            playerFlightType == FlightTypes.LOW_ALT_BOMB   ||
            playerFlightType == FlightTypes.DIVE_BOMB)
        {
            weatherSeverity = lightWeather();
        }
        else
        {
            weatherSeverity = overcastWeather();
        }
        return weatherSeverity;
    }

    protected int createNightCloud(Flight playerFlight) throws PWCGException 
    {
        int weatherSeverity = clearWeather();
        turbulence = 1;
        
        return weatherSeverity;
    }

    protected int overcastWeather() throws PWCGException
    {
        int weatherSeverity;
        cloudConfig = getOvercastSkys();
        precType = 0;
        precLevel = 0;
        turbulence = 1 + RandomNumberGenerator.getRandom(2);
        setCloudLevel(1000, 1000);
        cloudDensity = 300 + RandomNumberGenerator.getRandom(600);   
        weatherSeverity = 2;

        if (cloudLevel < 1500)
        {
            weatherDescription = "Medium cloud cover, low altitude with fog.";
        }
        else if (cloudLevel < 3000)
        {
            weatherDescription = "Medium cloud cover, medium altitude with fog.";
        }
        else
        {
            weatherDescription = "Medium cloud cover, high altitude with fog.";
        }
        return weatherSeverity;
    }

    private int heavyWeather() throws PWCGException
    {
        int weatherSeverity;
        cloudConfig = getHeavySkys();
        precType = 0;
        precLevel = 0;
        turbulence = 1 + RandomNumberGenerator.getRandom(2);
        setCloudLevel(1000, 2000);
        cloudDensity = 400 + RandomNumberGenerator.getRandom(600);   
        weatherSeverity = 2;

        if (cloudLevel < 1500)
        {
            weatherDescription = "Heavy cloud cover, low altitude.";
        }
        else if (cloudLevel < 3000)
        {
            weatherDescription = "Heavy cloud cover, medium altitude.";
        }
        else
        {
            weatherDescription = "Heavy cloud cover, high altitude.";
        }
        return weatherSeverity;
    }

    private int averageWeather() throws PWCGException
    {
        int weatherSeverity;
        cloudConfig = getAverageSkys();
        precType = 0;
        precLevel = 0;
        turbulence = 1 + RandomNumberGenerator.getRandom(2);
        setCloudLevel(1500, 3000);
        cloudDensity = 300 + RandomNumberGenerator.getRandom(600);   
        weatherSeverity = 2;

        if (cloudLevel < 1500)
        {
            weatherDescription = "Medium cloud cover, low altitude.";
        }
        else if (cloudLevel < 3000)
        {
            weatherDescription = "Medium cloud cover, medium altitude.";
        }
        else
        {
            weatherDescription = "Medium cloud cover, high altitude.";
        }
        return weatherSeverity;
    }

    private int lightWeather() throws PWCGException
    {
        int weatherSeverity;
        cloudConfig = getLightSkys();
        precType = 0;
        precLevel = 0;
        turbulence = 1 + RandomNumberGenerator.getRandom(2);
        setCloudLevel(2000, 3000);
        cloudDensity = 200 + RandomNumberGenerator.getRandom(300);
        weatherSeverity = 1;

        if (cloudLevel < 1500)
        {
            weatherDescription = "Light cloud cover, low altitude.";
        }
        else if (cloudLevel < 3000)
        {
            weatherDescription = "Light cloud cover, medium altitude.";
        }
        else
        {
            weatherDescription = "Light cloud cover, high altitude.";
        }
        return weatherSeverity;
    }

    private int clearWeather() throws PWCGException
    {
        int weatherSeverity;
        cloudConfig = getClearSkys();
        precType = 0;
        precLevel = 0;
        turbulence = 1 + RandomNumberGenerator.getRandom(2);
        setCloudLevel(2500, 5000);
        cloudDensity = 200 + RandomNumberGenerator.getRandom(200);
        weatherSeverity = 1;
        
        if (cloudLevel < 1500)
        {
            weatherDescription = "Mostly clear, some low altitude clouds.";
        }
        else if (cloudLevel < 4000)
        {
            weatherDescription = "Mostly clear, some medium altitude clouds.";
        }
        else
        {
            weatherDescription = "Mostly clear, some high altitude clouds.";
        }
        return weatherSeverity;
    }

    /**
     * @param base
     * @param random
     */
    private void setCloudLevel(int base, int random)
    {
        FlightTypes playerFLightType= playerFlight.getFlightType();
        if (playerFLightType == FlightTypes.RECON ||
            playerFLightType == FlightTypes.STRATEGIC_BOMB)
        {
            cloudLevel = 6000 + RandomNumberGenerator.getRandom(2000);
        }
        else if (playerFLightType == FlightTypes.DIVE_BOMB ||
                 playerFLightType == FlightTypes.BOMB)
        {
            cloudLevel = 4000 + RandomNumberGenerator.getRandom(3000);
        }
        else
        {
            cloudLevel = base + RandomNumberGenerator.getRandom(random);
        }
    }
	
	
	/**
	 * @throws PWCGException 
	 * 
	 */
    protected void createWind(int weatherSeverity) throws PWCGException  
	{
	    int windSpeedModifier = 1;
	    if (weatherSeverity <= 1)
	    {
	        windSpeedModifier += 1;
	    }
	    else if (weatherSeverity == 2)
	    {
            windSpeedModifier += 4;
	    }
	    else if (weatherSeverity >= 3)
	    {
            windSpeedModifier += 6;
	    }

	    // Wind conditions
		int turbulenceFactor = 2 * turbulence;

		// Night missions are flown with less turbulence
		int weatherDivisor = 1;
		if (playerFlight.isNightFlight())
		{
			weatherDivisor = 2;
		}
		

		WindLayer windLayer0 = new WindLayer();
		windLayer0.layer = 0;
		windLayer0.direction = windDirection;
		windLayer0.speed = 0 + RandomNumberGenerator.getRandom(3) / weatherDivisor;
		
		WindLayer windLayer500 = new WindLayer();
		windLayer500.layer = 500;
		windLayer500.direction = windDirection;
		windLayer500.speed = 1 + turbulenceFactor + RandomNumberGenerator.getRandom(3 + windSpeedModifier) / weatherDivisor;
		
		WindLayer windLayer1000 = new WindLayer();
		windLayer1000.layer = 1000;
		windLayer1000.direction = windDirection;
		windLayer1000.speed = 1 + turbulenceFactor + RandomNumberGenerator.getRandom(4 + windSpeedModifier) / weatherDivisor;
		
		WindLayer windLayer3000 = new WindLayer();
		windLayer3000.layer = 3000;
		windLayer3000.direction = windDirection;
		windLayer3000.speed = 1 + turbulenceFactor +  RandomNumberGenerator.getRandom(5 + windSpeedModifier) / weatherDivisor;

		
		WindLayer windLayer5000 = new WindLayer();
		windLayer5000.layer = 5000;
		windLayer5000.direction = windDirection;
		windLayer5000.speed = 1 + turbulenceFactor + RandomNumberGenerator.getRandom(5 + windSpeedModifier) / weatherDivisor;	
		
		windLayers.clear();
		windLayers.add(windLayer0);
		windLayers.add(windLayer500);
		windLayers.add(windLayer1000);
		windLayers.add(windLayer3000);
		windLayers.add(windLayer5000);
		
        ConfigManagerCampaign configManager = playerFlight.getCampaign().getCampaignConfigManager();
        int maxWind = configManager.getIntConfigParam(ConfigItemKeys.MaxWindKey);
		for (WindLayer windLayer : windLayers)
		{
		    if (windLayer.speed > maxWind)
		    {
		        windLayer.speed = maxWind;
		    }
		}
	}
	
	public int getCloudLevel() {
		return cloudLevel;
	}

	public int getCloudHeight() {
		return cloudDensity;
	}

	public int getPrecLevel() {
		return precLevel;
	}

	public int getPrecType() {
		return precType;
	}

	public String getCloudConfig() {
		return cloudConfig;
	}

	public int getTurbulence() {
		return turbulence;
	}
	
	public String getWeatherDescription() {
		return weatherDescription;
	}

	public ArrayList<WindLayer> getWindLayers() {
		return windLayers;
	}

	public int getTempPressLevel() {
		return tempPressLevel;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getPressure() {
		return pressure;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

    public class WindLayer
    {
        public int layer;
        public int direction;
        public int speed;
    }
}
