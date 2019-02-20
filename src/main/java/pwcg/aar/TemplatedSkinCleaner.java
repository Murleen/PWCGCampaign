package pwcg.aar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import pwcg.campaign.context.PWCGContext;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DirectoryReader;
import pwcg.core.utils.FileUtils;
import pwcg.core.utils.Logger;

public class TemplatedSkinCleaner
{
    private DirectoryReader directoryReader = new DirectoryReader();
    private FileUtils fileUtils = new FileUtils();

    public int cleanSkinFiles() throws PWCGException
    {
        List<String> filesToDelete = getSkinFilesToDelete();
        fileUtils.deleteFilesByFileName(filesToDelete);

        return filesToDelete.size();
    }

    private List<String> getSkinFilesToDelete() throws PWCGException
    {
        Set<String> usedSkins = new TreeSet<>();
        Set<String> allGeneratedSkins = new TreeSet<>();
        Pattern pattern = Pattern.compile("  Skin = \"([^\"]+pwcg_[^\"]+.dds)\";", Pattern.CASE_INSENSITIVE);

        String missionDir = PWCGContext.getInstance().getDirectoryManager().getSimulatorDataDir() + "Missions\\";
        directoryReader.sortilesInDir(missionDir);
        for (String filename : directoryReader.getFiles())
        {
            if (filename.endsWith(".mission"))
            {
                try (BufferedReader reader = new BufferedReader(new FileReader(missionDir + "\\" + filename)))
                {
                    for (String line : reader.lines().filter(pattern.asPredicate()).collect(Collectors.toList())) {
                        Matcher m = pattern.matcher(line);
                        if (m.matches())
                            usedSkins.add(m.group(1).toLowerCase());
                    }
                } catch (IOException e) {
                    Logger.logException(e);
                }
            }
        }
        File skinDir = new File(PWCGContext.getInstance().getDirectoryManager().getSimulatorDataDir() + "graphics\\skins\\");
        if (skinDir.isDirectory())
        {
            for (File plane : skinDir.listFiles())
            {
                if (plane.isDirectory())
                {
                    for (File skin : plane.listFiles())
                    {
                        if (skin.getName().toLowerCase().startsWith("pwcg_"))
                            allGeneratedSkins.add(plane.getName().toLowerCase() + "/" + skin.getName().toLowerCase());
                    }
                }
            }
        }
        System.out.println("Used skins:");
        for (String skin : usedSkins) {
            System.out.println(skin);
        }
        System.out.println("All skins:");
        for (String skin : allGeneratedSkins) {
            System.out.println(skin);
        }
        System.out.println("Skins to delete:");
        Set<String> skinsToDelete = new TreeSet<>(allGeneratedSkins);
        skinsToDelete.removeAll(usedSkins);
        for (String skin : skinsToDelete) {
            assert(!usedSkins.contains(skin));
            System.out.println(skin);
        }
        return skinsToDelete.stream().map(skin -> skinDir.getPath() + "\\" + skin.replace("/",  "\\")).collect(Collectors.toList());
    }

     public FileUtils getFileUtils()
    {
        return fileUtils;
    }

    public void setFileUtils(FileUtils fileUtils)
    {
        this.fileUtils = fileUtils;
    }
}

