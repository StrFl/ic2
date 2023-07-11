/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.versioning.ComparableVersion
 *  net.minecraft.launchwrapper.LaunchClassLoader
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.init;

import cpw.mods.fml.common.versioning.ComparableVersion;
import ic2.core.coremod.IC2core;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableObject;

public class Libraries {
    public static void init(File mcDir, String mcVersion) {
        File destination = new File(new File(mcDir, "mods"), "ic2");
        if (!destination.exists()) {
            destination.mkdir();
        }
        if (!destination.exists() || !destination.isDirectory()) {
            throw new RuntimeException("can't create mods/ic2 dir");
        }
        try {
            Libraries.extractFiles(mcDir, mcVersion, destination);
        }
        catch (Exception e) {
            throw new RuntimeException("library/mod extraction failed", e);
        }
        try {
            Libraries.loadFiles(destination);
        }
        catch (Exception e) {
            throw new RuntimeException("library loading failed", e);
        }
    }

    private static void extractFiles(File mcDir, String mcVersion, File destination) throws IOException, URISyntaxException {
        File dstFile;
        Object source;
        URL location = Libraries.class.getProtectionDomain().getCodeSource().getLocation();
        String protocol = location.getProtocol();
        HashSet<String> validLibFiles = new HashSet<String>();
        if (protocol.equals("file")) {
            source = new File(location.toURI());
            for (int i = Libraries.class.getPackage().getName().replaceAll("[^\\.]", "").length() + 1; i >= 0; --i) {
                source = ((File)source).getParentFile();
            }
            File[] files = new File((File)source, "lib").listFiles();
            if (files == null) {
                IC2core.log.warn("The ic2/lib directory doesn't exist.");
            } else {
                for (File srcFile : files) {
                    dstFile = new File(destination, srcFile.getName());
                    if (!dstFile.exists() || dstFile.length() != srcFile.length()) {
                        FileUtils.copyFile((File)srcFile, (File)dstFile);
                        IC2core.log.info("Extracted library " + srcFile.getName() + ".");
                    }
                    validLibFiles.add(srcFile.getName());
                }
            }
            files = new File((File)source, "mod").listFiles();
            if (files == null) {
                IC2core.log.warn("The ic2/mod directory doesn't exist.");
            } else {
                for (File srcFile : files) {
                    dstFile = Libraries.prepareModExtraction(mcDir, mcVersion, srcFile.getName());
                    if (dstFile != null) {
                        FileUtils.copyFile((File)srcFile, (File)dstFile);
                        IC2core.log.info("Extracted mod " + srcFile.getName() + ".");
                    }
                    validLibFiles.add(srcFile.getName());
                }
            }
        } else if (protocol.equals("jar")) {
            source = ((JarURLConnection)location.openConnection()).getJarFile();
            Enumeration<JarEntry> e = ((JarFile)source).entries();
            while (e.hasMoreElements()) {
                String fileName;
                JarEntry entry = e.nextElement();
                String name = entry.getName();
                if (entry.isDirectory()) continue;
                String path = FilenameUtils.getPathNoEndSeparator((String)name);
                if (path.equals("lib")) {
                    fileName = FilenameUtils.getName((String)name);
                    dstFile = new File(destination, fileName);
                    if (!dstFile.exists() || dstFile.length() != entry.getSize()) {
                        FileUtils.copyInputStreamToFile((InputStream)((JarFile)source).getInputStream(entry), (File)dstFile);
                        IC2core.log.info("Extracted library " + fileName + ".");
                    }
                    validLibFiles.add(fileName);
                    continue;
                }
                if (!path.equals("mod") || (dstFile = Libraries.prepareModExtraction(mcDir, mcVersion, fileName = FilenameUtils.getName((String)name))) == null) continue;
                FileUtils.copyInputStreamToFile((InputStream)((JarFile)source).getInputStream(entry), (File)dstFile);
                IC2core.log.info("Extracted mod " + fileName + ".");
            }
        } else {
            throw new RuntimeException("invalid protocol (" + location + ").");
        }
        for (File file : destination.listFiles()) {
            if (validLibFiles.contains(file.getName())) continue;
            if (file.delete()) {
                IC2core.log.info("Removed old library " + file.getName() + ".");
                continue;
            }
            IC2core.log.warn("Can't remove old library " + file.getName() + ".");
        }
    }

    private static void loadFiles(File dir) throws MalformedURLException {
        LaunchClassLoader classLoader = (LaunchClassLoader)Libraries.class.getClassLoader();
        File[] files = dir.listFiles();
        if (files == null) {
            IC2core.log.warn("The directory " + dir + " doesn't exist, can't load libraries.");
        } else {
            for (File file : files) {
                classLoader.addURL(file.toURI().toURL());
                IC2core.log.info("Loaded library " + file.getName() + ".");
            }
        }
    }

    private static String[] splitVersion(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) continue;
            String[] ret = new String[]{str.substring(0, i), Character.isDigit(c) ? str.substring(i) : str.substring(i + 1)};
            return ret;
        }
        return null;
    }

    private static File prepareModExtraction(File mcDir, String mcVersion, String name) {
        String[] nameParts = Libraries.splitVersion(FilenameUtils.getBaseName((String)name));
        if (nameParts == null) {
            throw new RuntimeException("invalid bundled mod filename: " + name);
        }
        File modsDir = new File(mcDir, "mods");
        File modsVersionDir = new File(modsDir, mcVersion);
        if (!modsVersionDir.exists()) {
            modsVersionDir.mkdir();
        }
        String prefix = nameParts[0].toLowerCase(Locale.ENGLISH);
        ComparableVersion version = new ComparableVersion(nameParts[1]);
        MutableObject oldFile = new MutableObject();
        boolean inModsDir = Libraries.checkDestination(modsDir, prefix, name, version, (MutableObject<File>)oldFile);
        boolean inModsVersionDir = Libraries.checkDestination(modsVersionDir, prefix, name, version, (MutableObject<File>)oldFile);
        if (inModsDir || inModsVersionDir) {
            return null;
        }
        if (oldFile.getValue() != null) {
            if (((File)oldFile.getValue()).delete()) {
                IC2core.log.info("Removed old mod " + ((File)oldFile.getValue()).getName());
            } else {
                IC2core.log.warn("Can't remove old mod " + ((File)oldFile.getValue()).getName());
            }
        }
        return new File(modsVersionDir, name);
    }

    private static boolean checkDestination(File destination, final String prefix, String name, ComparableVersion newVersion, MutableObject<File> oldFile) {
        boolean found = false;
        for (File dstFile : destination.listFiles(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return !file.isDirectory() && file.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix);
            }
        })) {
            if (found) {
                return true;
            }
            found = true;
            if (dstFile.getName().equalsIgnoreCase(name)) {
                return true;
            }
            String[] dstNameParts = Libraries.splitVersion(FilenameUtils.getBaseName((String)dstFile.getName()));
            if (dstNameParts == null) {
                return true;
            }
            ComparableVersion dstVersion = new ComparableVersion(dstNameParts[1]);
            if (dstVersion.compareTo(newVersion) < 0) {
                if (oldFile.getValue() != null) {
                    return true;
                }
            } else {
                return true;
            }
            oldFile.setValue((Object)dstFile);
        }
        return false;
    }
}

