package cc.uraniummc;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;

import org.spigotmc.RestartCommand;

import cpw.mods.fml.common.FMLCommonHandler;

public class Uranium {
    public static final ThreadGroup sKCauldronThreadGroup = new ThreadGroup("Uranium");
    public static final String name="Uranium";
    private static boolean sManifestParsed = false;
    private static void parseManifest() {
        if (sManifestParsed)
            return;
        sManifestParsed = true;

        try {
            Enumeration<URL> resources = Uranium.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            Properties manifest = new Properties();
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                manifest.load(url.openStream());
                String version = manifest.getProperty("Uranium-Version");
                if (version != null) {
                    String path = url.getPath();
                    String jarFilePath = path.substring(path.indexOf(":") + 1,
                            path.indexOf("!"));
                    jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                    sServerLocation = new File(jarFilePath);

                    sCurrentVersion = version;
                    sGroup = manifest.getProperty("Uranium-Group");
                    sBranch = manifest.getProperty("Uranium-Branch");
                    sChannel = manifest.getProperty("Uranium-Channel");
                    sLegacy = Boolean.parseBoolean(manifest.getProperty("Uranium-Legacy"));
                    sOfficial = Boolean.parseBoolean(manifest.getProperty("Uranium-Official"));
                    break;
                }
                manifest.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sCurrentVersion;

    public static String getCurrentVersion() {
        parseManifest();
        return sCurrentVersion;
    }

    private static File sServerLocation;

    public static File getServerLocation() {
        parseManifest();
        return sServerLocation;
    }

    private static File sServerHome;

    public static File getServerHome() {
        if (sServerHome == null) {
            String home = System.getenv("KCAULDRON_HOME");
            if (home != null) {
                sServerHome = new File(home);
            } else {
                parseManifest();
                sServerHome = sServerLocation.getParentFile();
            }
        }
        return sServerHome;
    }

    private static String sGroup;

    public static String getGroup() {
        parseManifest();
        return sGroup;
    }

    private static String sBranch;

    public static String getBranch() {
        parseManifest();
        return sBranch;
    }

    private static String sChannel;

    public static String getChannel() {
        parseManifest();
        return sChannel;
    }
    
    private static boolean sLegacy, sOfficial;
    
    public static boolean isLegacy() {
        parseManifest();
        return sLegacy;
    }
    
    public static boolean isOfficial() {
        parseManifest();
        return sOfficial;
    }

    public static File sNewServerLocation;
    public static String sNewServerVersion;
    public static boolean sUpdateInProgress;

    public static void restart() {
        RestartCommand.restart(true);
    }
    
    private static int sForgeRevision = 0;

    public static int lookupForgeRevision() {
        if (sForgeRevision != 0) return sForgeRevision;
        int revision = Integer.parseInt(System.getProperty("uranium.forgeRevision", "0"));
        if (revision != 0) return sForgeRevision = revision;
        try {
            Properties p = new Properties();
            p.load(Uranium.class
                    .getResourceAsStream("/fmlversion.properties"));
            revision = Integer.parseInt(String.valueOf(p.getProperty(
                    "fmlbuild.build.number", "0")));
        } catch (Exception e) {
        }
        if (revision == 0) {
            ULog.get().warning("Uranium: could not parse forge revision, critical error");
            FMLCommonHandler.instance().exitJava(1, false);
        }
        return sForgeRevision = revision;
    }
}
