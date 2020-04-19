package pwcg.product.bos.map.east1945;

import pwcg.mission.options.MapSeasonalParameters;
import pwcg.mission.options.MissionOptions;


public class East1945MissionOptions extends MissionOptions
{
    public East1945MissionOptions()
    {
        super();

    	makeWinter();
    	makeSpring();
    	makeSummer();
        makeAutumn();
    }

	private void makeWinter()
	{
		winter.setHeightMap("graphics\\LANDSCAPE_Rheinland_wi\\height.hini");
    	winter.setTextureMap("graphics\\LANDSCAPE_Rheinland_wi\\textures.tini");
    	winter.setForrestMap("graphics\\LANDSCAPE_Rheinland_wi\\trees\\woods.wds");
    	winter.setGuiMap("rheinland-winter");
    	winter.setSeasonAbbreviation(MapSeasonalParameters.WINTER_ABREV);
    	winter.setSeason(MapSeasonalParameters.WINTER_STRING);
	}

	private void makeSpring()
	{
	    spring.setHeightMap("graphics\\LANDSCAPE_Rheinland_sp\\height.hini");
        spring.setTextureMap("graphics\\LANDSCAPE_Rheinland_sp\\textures.tini");
        spring.setForrestMap("graphics\\LANDSCAPE_Rheinland_sp\\trees\\woods.wds");
    	spring.setGuiMap("rheinland-spring");
        // Yes, SUMMER is correct!!
    	spring.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	spring.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeSummer()
	{
	    summer.setHeightMap("graphics\\LANDSCAPE_Rheinland_su\\height.hini");
        summer.setTextureMap("graphics\\LANDSCAPE_Rheinland_su\\textures.tini");
        summer.setForrestMap("graphics\\LANDSCAPE_Rheinland_su\\trees\\woods.wds");
    	summer.setGuiMap("rheinland-summer");
    	summer.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	summer.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}

	private void makeAutumn()
	{
	    autumn.setHeightMap("graphics\\LANDSCAPE_Rheinland_au\\height.hini");
        autumn.setTextureMap("graphics\\LANDSCAPE_Rheinland_au\\textures.tini");
        autumn.setForrestMap("graphics\\LANDSCAPE_Rheinland_au\\trees\\woods.wds");
		autumn.setGuiMap("rheinland-autumn");
		// Yes, SUMMER is correct!!
    	autumn.setSeasonAbbreviation(MapSeasonalParameters.SUMMER_ABREV);
    	autumn.setSeason(MapSeasonalParameters.SUMMER_STRING);
	}
}
