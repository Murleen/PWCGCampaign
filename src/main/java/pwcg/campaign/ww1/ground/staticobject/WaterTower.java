package pwcg.campaign.ww1.ground.staticobject;

import pwcg.campaign.api.ICountry;
import pwcg.core.exception.PWCGException;


public class WaterTower extends StaticObject
{
	// German
	private String[] waterTower = 
	{
		"watertower_01", "watertower_02", "watertower_03"
	};
	

	public WaterTower(ICountry country) 
	{
		super(country);
		
		this.country = country;
		
		String waterTowerId= "";
		
		// we will just use one kind of water tower
		int selectedWaterTower = 1;
		waterTowerId = waterTower[selectedWaterTower];
		displayName = "Water Tower";
		
		vehicleType = waterTowerId;
		script = "LuaScripts\\WorldObjects\\" + waterTowerId + ".txt";
		model = "graphics\\blocks\\" + waterTowerId + ".mgm";
	}

	public WaterTower copy () throws PWCGException 
	{
		WaterTower clonedObject = new WaterTower(country);
		
		super.makeCopy(clonedObject);
		
		return clonedObject;
	}
}
