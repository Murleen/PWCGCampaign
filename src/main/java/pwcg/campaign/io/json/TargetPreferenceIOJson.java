package pwcg.campaign.io.json;

import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.target.locator.targettype.TargetPreferenceSet;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;

public class TargetPreferenceIOJson 
{
	public static TargetPreferenceSet readJson(String mapName) throws PWCGException, PWCGIOException
	{
		JsonObjectReader<TargetPreferenceSet> jsonReader = new JsonObjectReader<>(TargetPreferenceSet.class);
		TargetPreferenceSet targetPreferenceSet = jsonReader.readJsonFile(PWCGContext.getInstance().getDirectoryManager().getPwcgInputDir() + mapName + "\\", "TargetPreferences.json");
		return targetPreferenceSet;
	}
}
