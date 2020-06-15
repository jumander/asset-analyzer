package trading;


import assetAnalyzer.AssetGraph;
import financial.Asset;
import financial.Time;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by johannes on 17/06/17.
 */
public class AlgorithmImporter {
    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    public List<Object> getAlgorithms(String scannedPackage, String superClass)
    {
        List<Object> algorithms= getClasses(scannedPackage, superClass);

        return algorithms;
    }


    public static List<Object> getClasses(String scannedPackage, String superClass) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            System.err.println("Cannot find algorithm path.");
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }

        classes.removeIf(c -> !c.getSuperclass().getSimpleName().contains(superClass));

        List<Object> objects = new ArrayList<>();
        for(int i = 0; i < classes.size(); i++)
        {
            try {
                objects.add(classes.get(i).newInstance());
            } catch (InstantiationException e) {
                System.err.println("Could not create instance of trader");
            } catch (IllegalAccessException e) {
                System.err.println("Could not access trader class");
            }
        }

        return objects;
    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }


}