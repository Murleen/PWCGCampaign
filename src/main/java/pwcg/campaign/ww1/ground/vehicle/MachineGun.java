package pwcg.campaign.ww1.ground.vehicle;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.Country;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.mcu.McuTREntity;

public class MachineGun extends Vehicle
{
	// German
	private String[][] germanMGs = 
	{
		{ "lmg08", "machineguns" },
	};
	
	// Allied
	private String[][] alliedMGs = 
	{
		{ "hotchkiss", "machineguns" },
	};

	public MachineGun(ICountry country) 
	{
        super(country);

		this.setEngageable(1);

		String mgId= "";
		String mgDir = "";
		
		if (country.getCountry() == Country.FRANCE)
		{
			int selectedMG = RandomNumberGenerator.getRandom(alliedMGs.length);
			mgId = alliedMGs[selectedMG] [0];
			mgDir = alliedMGs[selectedMG] [1];
			displayName = "French MG";
		}
		else if (country.getCountry() == Country.USA)
		{
			int selectedMG = RandomNumberGenerator.getRandom(alliedMGs.length);
			mgId = alliedMGs[selectedMG] [0];
			mgDir = alliedMGs[selectedMG] [1];
			displayName = "American MG";
		}
		else if (country.getCountry() == Country.GERMANY)
		{
			int selectedMG = RandomNumberGenerator.getRandom(germanMGs.length);
			mgId = germanMGs[selectedMG] [0];
			mgDir = germanMGs[selectedMG] [1];
			displayName = "German MG";
		}
		else
		{
			int selectedMG = RandomNumberGenerator.getRandom(alliedMGs.length);
			mgId = alliedMGs[selectedMG] [0];			
			mgDir = alliedMGs[selectedMG] [1];
			displayName = "British MG";
		}
		
		name = mgId;
		script = "LuaScripts\\WorldObjects\\" + mgId + ".txt";
		model = "graphics\\artillery\\" + mgDir + "\\" + mgId + ".mgm";
	}

	public MachineGun copy () 
	{
		MachineGun mg = new MachineGun(country);
		
		mg.index = IndexGenerator.getInstance().getNextIndex();
		
		mg.name = this.name;
		mg.displayName = this.displayName;
		mg.linkTrId = this.linkTrId;
		mg.script = this.script;
		mg.model = this.model;
		mg.Desc = this.Desc;
		mg.aiLevel = this.aiLevel;
		mg.numberInFormation = this.numberInFormation;
		mg.vulnerable = this.vulnerable;
		mg.engageable = this.engageable;
		mg.limitAmmo = this.limitAmmo;
		mg.damageReport = this.damageReport;
		mg.country = this.country;
		mg.damageThreshold = this.damageThreshold; 
		
		mg.position = new Coordinate();
		mg.orientation = new Orientation();
		
		mg.entity = new McuTREntity();
		
		mg.populateEntity();
		
		return mg;
	}

}
