package cn.sky.luckypillar.utils.classutils;

import cn.sky.luckypillar.utils.chat.CC;
import com.google.common.collect.ImmutableSet;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {
    private ClassUtil() {
        throw new RuntimeException("工具类不允许实例化");
    }

    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        JarFile jarFile;
        ArrayList<Class<?>> classes = new ArrayList<>();
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath();
        resPath = resPath.replace("%20", " ");
        resPath = resPath.replace("%5B", "[");
        resPath = resPath.replace("%5D", "]");
        resPath = resPath.replace("%23", "#");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        try {
            jarFile = new JarFile(jarPath);
        }
        catch (IOException e) {
            throw new RuntimeException("读取 JAR 文件时发生异常: " + jarPath, e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;
            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }
            if (className == null) continue;
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            }
            catch (Throwable e) {
                // empty catch block
            }
            if (className.contains("cn.sky.bedwars.nms") || clazz == null) continue;
            classes.add(clazz);
        }
        try {
            jarFile.close();
        }
        catch (IOException e) {
            CC.sendError("&c关闭 JAR 文件失败", e);
        }
        return ImmutableSet.copyOf(classes);
    }

    public static String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            return new File(dirURL.toURI()).list();
        }
        if (dirURL == null) {
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();
            HashSet<String> result = new HashSet<>();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (!name.startsWith(path)) continue;
                String entry = name.substring(path.length());
                int checkSubdir = entry.indexOf("/");
                if (checkSubdir >= 0) {
                    entry = entry.substring(0, checkSubdir);
                }
                result.add(entry);
            }
            return result.toArray(new String[result.size()]);
        }
        throw new UnsupportedOperationException("无法列出该 URL 对应文件: " + dirURL);
    }
}
