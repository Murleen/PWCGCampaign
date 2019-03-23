package pwcg.gui.utils;

import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.core.exception.PWCGException;

public class ContextSpecificImages
{
    public static String menuPathForNation() throws PWCGException 
    {
		SquadronMember referencePlayer = PWCGContextManager.getInstance().getReferencePlayer();
        String nationality = referencePlayer.determineSquadron().getCountry().getNationality();
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Menus\\" + nationality + "\\";
        
        return picPath;
    }

    public static String menuPathForGeneric() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Menus\\Generic\\";
        
        return picPath;
    }

    public static String menuPathMain() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Menus\\Main\\";
        
        return picPath;
    }


    public static String imagesMaps() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Maps\\";
        
        return picPath;
    }

    public static String imagesMedals() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Medals\\";
        
        return picPath;
    }

    public static String imagesMisc() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Misc\\";
        
        return picPath;
    }

    public static String imagesNational() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "National\\";
        
        return picPath;
    }

    public static String imagesNewspaper() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Newspaper\\";
        
        return picPath;
    }

    public static String imagesPilotPictures() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "PilotPictures\\";
        
        return picPath;
    }

    public static String imagesPlanes() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Planes\\";
        
        return picPath;
    }

    public static String imagesProfiles() 
    {
        String picPath = PWCGContextManager.getInstance().getDirectoryManager().getPwcgImagesDir() + "Profiles\\";
        
        return picPath;
    }

}
