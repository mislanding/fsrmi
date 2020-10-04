package com.sict.fsrmi.server.annotation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author lyy
 * @version 1.0
 * @data 2020/10/4
 * 工具类，用于扫描对应注解类并获取属性
 */
public class AnnotationScannerUtils {
    private static final String EXT = "class";

    /**
     * 根据包名获取包的URL
     *
     * @param pkgName com.demo.controller
     * @return
     */
    public static String getPkgPath(String pkgName) {
        String pkgDirName = pkgName.replace('.', '/');
        System.out.println(pkgDirName);
        URL url = Thread.currentThread().getContextClassLoader().getResource(pkgDirName);

        return url == null ? null : url.getFile();
    }

    /**
     * 获取指定包下所有类对象的集合
     *
     * @param pkgName   包名(com.sict.fsrmi.server)
     * @param pkgPath   包路径(/Users/xxx/workspace/java/project/out/production/classes/com/demo/controller)
     * @param recursive 是否递归遍历子目录
     * @return 类集合
     */
    public static Set<Class<?>> scanClasses(String pkgName, String pkgPath, final boolean recursive) {
        Set<Class<?>> classesSet = new HashSet<>();

        Collection<File> allClassFile = getAllClassFile(pkgPath, recursive);

        for (File curFile : allClassFile) {
            try {
                classesSet.add(getClassObj(curFile, pkgPath, pkgName));
            } catch (ClassNotFoundException e) {
                System.out.println("加载包失败");
                e.printStackTrace();
            }
        }

        return classesSet;
    }

    /**
     * 获取指定包下包含指定注解的所有类对象的集合
     *
     * @param pkgName           包名(com.sict.fsrmi.server)
     * @param pkgPath           包路径()
     * @param recursive         是否递归遍历子目录
     * @param annotation 指定注解
     * @return 以注解和对应类集合构成的键值对
     */
    public static Map<Class<? extends Annotation>, Set<Class<?>>> scanClassesByAnnotations(
            String pkgName, String pkgPath, final boolean recursive, Class<? extends Annotation> annotation) {
        Map<Class<? extends Annotation>, Set<Class<?>>> resultMap = new HashMap<>(16);

        Collection<File> allClassFile = getAllClassFile(pkgPath, recursive);

        for (File curFile : allClassFile) {
            try {
                Class<?> curClass = getClassObj(curFile, pkgPath, pkgName);

                if (curClass.isAnnotationPresent(annotation)) {
                    if (!resultMap.containsKey(annotation)) {
                        resultMap.put(annotation, new HashSet<Class<?>>());
                    }
                    resultMap.get(annotation).add(curClass);
                }
            } catch (ClassNotFoundException e) {
                System.out.println("加载类失败");
                e.printStackTrace();
            }
        }

        return resultMap;
    }

    /**
     * 加载类
     *
     * @param file
     * @param pkgPath
     * @param pkgName
     * @return
     * @throws ClassNotFoundException
     */
    private static Class<?> getClassObj(File file, String pkgPath, String pkgName) throws ClassNotFoundException {
        // 考虑class文件在子目录中的情况
        String absPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - EXT.length() - 1);
        String className = absPath.substring(pkgPath.length()).replace(File.separatorChar, '.');
        className = className.startsWith(".") ? pkgName + className : pkgName + "." + className;

        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    /**
     * 遍历指定目录下所有扩展名为class的文件
     *
     * @param pkgPath   包目录
     * @param recursive 是否递归遍历子目录
     * @return
     */
    private static Collection<File> getAllClassFile(String pkgPath, boolean recursive) {
        System.out.println(pkgPath);

        File fPkgDir = new File(pkgPath);

        if (!(fPkgDir.exists() && fPkgDir.isDirectory())) {
            System.out.println(pkgPath + "为空");
            return null;
        }

        return FileUtils.listFiles(fPkgDir, new String[]{EXT}, recursive);
    }

    /**
     * 查找指定注解的Method
     *
     * @param classes           查找范围
     * @param targetAnnotations 指定的注解
     * @return 以注解和对应Method类集合构成的键值对
     */
    public static Map<Class<? extends Annotation>, Set<Method>> scanMethodsByAnnotations(Set<Class<?>> classes,
                                                                                         List<Class<? extends Annotation>> targetAnnotations) {
        Map<Class<? extends Annotation>, Set<Method>> resultMap = new HashMap<>(16);

        for (Class<?> cls : classes) {
            Method[] methods = cls.getMethods();

            for (Class<? extends Annotation> annotation : targetAnnotations) {
                for (Method method : methods) {
                    if (method.isAnnotationPresent(annotation)) {
                        if (!resultMap.containsKey(annotation)) {
                            resultMap.put(annotation, new HashSet<Method>());
                        }
                        resultMap.get(annotation).add(method);
                    }
                }
            }
        }

        return resultMap;
    }
}
