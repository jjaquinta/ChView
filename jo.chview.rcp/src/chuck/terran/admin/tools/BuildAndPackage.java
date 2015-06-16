package chuck.terran.admin.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import jo.util.html.HTTPThread;
import jo.util.utils.DebugUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.ZipUtils;
import jo.util.utils.io.FileUtils;
import jo.util.utils.io.StreamUtils;
import jo.util.utils.obj.StringUtils;

public class BuildAndPackage
{
    private String[] mArgs;
    
    private File    mWorkspace;
    private File    mEclipseDirectory;
    private File    mBuildDirectory;
    private File    mConfigurationDirectory;
    private List<String> mPlugins = new ArrayList<>(); 
    private List<String> mFeatures = new ArrayList<>(); 
    private File    mJREDir;
    private String  mPathToProduct;
    private String  mArchivePrefix;
    
    private boolean mNoCopy = false;

    private File mOrgEclipsePdeBuild = null;
    private File mOrgEclipseEquinoxLauncher = null;
    private List<File> mFilesToCopy = new ArrayList<>();

    private File    mBuildProperties;
    
    public BuildAndPackage(String[] args)
    {
        mArgs = args;
        mWorkspace = new File("C:\\Users\\IBM_ADMIN\\Documents\\ws\\jo");
        mEclipseDirectory = new File("C:\\Program Files\\eclipseLunaRCP");
        mPlugins.add("jo.chview.util");
        mPlugins.add("jo.chview.util.core");
        mPlugins.add("jo.chview.core");
        mPlugins.add("jo.chview.rcp");
        mBuildDirectory = new File("c:\\temp\\chuck_build");
        mConfigurationDirectory = new File("c:\\temp\\chuck_config");
        mPathToProduct = "/chuck.terran.admin/chuck.terran.admin.product";
        mArchivePrefix = "chuck";
        mJREDir = new File("C:\\Program Files\\Java\\jdk1.8.0_40");
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            updateVersions();
            findEclipseArtifacts();
            createBuildDirectory();
            copyPlugins();
            copyFeatures();
            createConfigDirectory();
            editBuildProperties();
            runBuild();
            unzipTarget();
            updateTarget();
            zipTarget();
            createManifest();
            if (!mNoCopy)
                copyToServer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-nocopy".equalsIgnoreCase(mArgs[i]))
                mNoCopy = true;
            else if ("-workspace".equalsIgnoreCase(mArgs[i]))
                mWorkspace = new File(mArgs[++i]);
            else if ("-EclipseDirectory".equalsIgnoreCase(mArgs[i]))
                mEclipseDirectory = new File(mArgs[++i]);
            else if ("-plugin".equalsIgnoreCase(mArgs[i]))
                mPlugins.add(mArgs[++i]);
            else if ("-BuildDirectory".equalsIgnoreCase(mArgs[i]))
                mBuildDirectory = new File(mArgs[++i]);
            else if ("-ConfigurationDirectory".equalsIgnoreCase(mArgs[i]))
                mConfigurationDirectory = new File(mArgs[++i]);
            else if ("-PathToProduct".equalsIgnoreCase(mArgs[i]))
                mPathToProduct = mArgs[++i];
            else if ("-ArchivePrefix".equalsIgnoreCase(mArgs[i]))
                mArchivePrefix = mArgs[++i];
            else if ("-JREDirectory".equalsIgnoreCase(mArgs[i]))
                mJREDir = new File(mArgs[++i]);
    }
    
    private void copyToServer() throws IOException
    {
        DebugUtils.info("Copying to server:");
        String cmd = "\"c:\\Program Files (x86)\\WinSCP\\WinSCP.com\" ";
        cmd += "/command ";
        cmd += "\"option batch abort\" ";
        cmd += "\"option confirm off\" ";
        cmd += "\"open ftp://chview\\@ocean-of-storms.com:s0!stati0n@ocean-of-storms.com/\" ";
        cmd += "\"cd dist\" ";
        for (File f : mFilesToCopy)
            cmd += "\"put "+f.toString()+"\" ";
        cmd += "\"exit\"";;
        DebugUtils.info("  "+cmd);
        exec(cmd);
    }
    
    private void createManifest() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HTTPThread getThread = new HTTPThread(baos, "http://www.ocean-of-storms.com/chview/dist/", null, null);
        getThread.start();
        while (!getThread.isDone())
            try
            {
                Thread.sleep(250);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        String html = baos.toString();
        Map<String,String> plugins = new HashMap<String, String>();
        for (;;)
        {
            int o = html.indexOf("href=\"");
            if (o < 0)
                break;
            html = html.substring(o + 6);
            o = html.indexOf("\"");
            if (o < 0)
                break;
            String plugin = html.substring(0, o);
            o = plugin.indexOf("_");
            if (o < 0)
                continue;
            String name = plugin.substring(0, o);
            String vers = plugin.substring(o + 1);
            if (!plugins.containsKey(name))
                plugins.put(name, vers);
            else if (vers.compareTo(plugins.get(name)) > 0)
                plugins.put(name, vers);
        }
        StringBuffer mf = new StringBuffer();
        for (String name : plugins.keySet())
            mf.append(name+"_"+plugins.get(name)+"\r\n");
        File pluginDir = new File(mBuildDirectory, mArchivePrefix+"/plugins");
        File[] pluginJars = pluginDir.listFiles();
        for (String plugin : mPlugins)
        {
            for (File jar : pluginJars)
                if (jar.getName().startsWith(plugin+"_"))
                {
                    mFilesToCopy.add(jar);
                    break;
                }
        }
        File manifestFile = new File(pluginDir, "manifest.txt");
        FileUtils.writeFile(mf.toString(), manifestFile);
        mFilesToCopy.add(manifestFile);
        /*
        StringBuffer mf = new StringBuffer();
        File pluginDir = new File(mBuildDirectory, mArchivePrefix+"/plugins");
        File[] pluginJars = pluginDir.listFiles();
        for (String plugin : mPlugins)
        {
            for (File jar : pluginJars)
                if (jar.getName().startsWith(plugin+"_"))
                {
                    mFilesToCopy.add(jar);
                    mf.append(jar.getName()+"\r\n");
                    break;
                }
        }
        File manifestFile = new File(pluginDir, "manifest.txt");
        FileUtils.writeFile(mf.toString(), manifestFile);
        mFilesToCopy.add(manifestFile);
        */
    }
    
    private void updateVersions() throws IOException
    {
        DebugUtils.info("Updating version:");
        for (String plugin : mPlugins)
        {
            File root = new File(mWorkspace, plugin);
            File manifestMF = new File(root, "META-INF/MANIFEST.MF");
            File pluginXML = new File(root, "plugin.xml");
            String version = FormatUtils.formatDateVersion(System.currentTimeMillis());
            DebugUtils.info("  "+plugin+": "+version);
            
            String txt = FileUtils.readFileAsString(manifestMF.toString());
            txt = StringUtils.replace(txt, "Bundle-Version: ", "\n", "1.0.0."+version);
            FileUtils.writeFile(txt, manifestMF);
            
            if (pluginXML.exists())
            {
                txt = FileUtils.readFileAsString(pluginXML.toString());
                txt = StringUtils.replace(txt, "Version ", "\"", "1.0.0."+version);
                FileUtils.writeFile(txt, pluginXML);
            }
            
            for (File productXML : root.listFiles())
                if (productXML.getName().endsWith(".product"))
                {
                    txt = FileUtils.readFileAsString(productXML.toString());
                    txt = StringUtils.replace(txt, "Version ", "\n", "1.0.0."+version);
                    txt = StringUtils.replace(txt, ".application\" version=\"", "\"", "1.0.0."+version);
                    FileUtils.writeFile(txt, productXML);
                }
            
            File splashBMP = new File(root, "splash.bmp");
            if (splashBMP.exists())
            {
                File aboutPNG = new File(root, "images/about.png");
                BufferedImage img = ImageIO.read(splashBMP);
                Image i = img.getScaledInstance(250, 330, BufferedImage.SCALE_SMOOTH);
                img = new BufferedImage(250, 330, BufferedImage.TYPE_INT_ARGB);
                Graphics g = img.createGraphics();
                g.drawImage(i, 0, 0, null);
                g.setColor(Color.yellow);
                g.drawString("Version: "+version, 8, img.getHeight() - 12);
                g.dispose();
                ImageIO.write(img, "PNG", aboutPNG);
            }
        }
    }
    
    private void zipTarget() throws IOException
    {
        File targetZip = new File(mBuildDirectory, mArchivePrefix+".zip");
        File targetDir = new File(mBuildDirectory, mArchivePrefix);
        FileOutputStream os = new FileOutputStream(targetZip);
        ZipUtils.zip(targetDir, os);
        os.close();
        mFilesToCopy.add(targetZip);
    }
    
    private void updateTarget() throws IOException
    {
        // copy exe
        File fromEXE = new File(mEclipseDirectory, "eclipse.exe");
        File toEXE = new File(mBuildDirectory, mArchivePrefix+"\\"+mArchivePrefix+".exe");
        FileUtils.copy(fromEXE, toEXE);
        // copy jre
        File fromJRE = new File(mJREDir, "jre");
        File toJRE = new File(mBuildDirectory, mArchivePrefix+"\\jre");
        FileUtils.copyDir(fromJRE, toJRE);
    }
    
    private void unzipTarget() throws IOException
    {
        File targetZip = new File(mBuildDirectory, "I.TestBuild\\TestBuild-win32.win32.x86_64.zip");
        File targetDir = mBuildDirectory;
        FileInputStream is = new FileInputStream(targetZip);
        ZipUtils.unzip(targetDir, is);
        is.close();
    }
    
    private void runBuild() throws IOException
    {
        String cmd = "java -jar ";
        cmd += "\""+mOrgEclipseEquinoxLauncher.getAbsolutePath()+"\" ";
        cmd += "-application org.eclipse.ant.core.antRunner ";
        cmd += "-buildfile \""+mOrgEclipsePdeBuild.getAbsolutePath()+"\\scripts\\productBuild\\productBuild.xml\" ";
        cmd += "-Dbuilder=\""+mConfigurationDirectory.getAbsolutePath()+"\"";
        DebugUtils.info(cmd);
        exec(cmd);
    }

    private void exec(String cmd) throws IOException
    {
        Process p = Runtime.getRuntime().exec(cmd);
        final InputStream stdout = p.getInputStream();
        final InputStream stderr = p.getErrorStream();
        Thread stdoutReader = new Thread() { public void run() { try { StreamUtils.copy(stdout, System.out); } catch (IOException e) { } } };
        stdoutReader.start();
        Thread stderrReader = new Thread() { public void run() { try { StreamUtils.copy(stderr, System.out); } catch (IOException e) { } } };
        stderrReader.start();
        int ec = -1;
        try
        {
            ec = p.waitFor();
        }
        catch (InterruptedException e)
        {
        }
        DebugUtils.info("Exit Code: "+ec);
        if (ec != 0)
            System.exit(1);
    }
    
    private void findEclipseArtifacts()
    {
        File pluginDir = new File(mEclipseDirectory, "plugins");
        for (File plugin : pluginDir.listFiles())
            if (plugin.getName().startsWith("org.eclipse.pde.build"))
                mOrgEclipsePdeBuild = plugin;
            else if (plugin.getName().startsWith("org.eclipse.equinox.launcher_"))
                mOrgEclipseEquinoxLauncher = plugin;
    }
    
    private void editBuildProperties() throws IOException
    {
        DebugUtils.info("Editing "+mBuildProperties);
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(mBuildProperties);
        props.load(fis);
        fis.close();
        props.setProperty("product", mPathToProduct);
        props.setProperty("base", mEclipseDirectory.getParentFile().toString());
        props.setProperty("baseLocation", mEclipseDirectory.toString());
        props.setProperty("buildDirectory", mBuildDirectory.toString());
        props.setProperty("configs", "win32,win32,x86_64");
        props.setProperty("archivePrefix", mArchivePrefix);
        props.setProperty("JavaSE-1.8", mJREDir.toString()+"/jre/lib/rt.jar;"+mJREDir.toString()+"/jre/lib/jsse.jar");        
        FileOutputStream fos = new FileOutputStream(mBuildProperties);
        props.store(fos, null);
        fos.close();
    }
    
    private void createBuildDirectory()
    {
        DebugUtils.info("Creating build directory - "+mBuildDirectory);
        if (mBuildDirectory.exists())
            FileUtils.rmdir(mBuildDirectory);
        mBuildDirectory.mkdirs();
    }
    
    private void createConfigDirectory() throws IOException
    {
        DebugUtils.info("Creating config directory - "+mConfigurationDirectory);
        if (mConfigurationDirectory.exists())
            FileUtils.rmdir(mConfigurationDirectory);
        mConfigurationDirectory.mkdirs();
        File buildPropSrc = new File(mOrgEclipsePdeBuild, "templates/headless-build/build.properties");
        mBuildProperties = new File(mConfigurationDirectory, "build.properties");
        DebugUtils.info("  copying - "+buildPropSrc);
        FileUtils.copy(buildPropSrc, mBuildProperties);
        File buildXMLSrc = new File(mOrgEclipsePdeBuild, "scripts/build.xml");
        File buildXMLDest = new File(mConfigurationDirectory, "build.xml");
        DebugUtils.info("  copying - "+buildXMLSrc);
        FileUtils.copy(buildXMLSrc, buildXMLDest);
    }
    
    private void copyPlugins() throws IOException
    {
        DebugUtils.info("Copying plugins");
        File pluginDir = new File(mBuildDirectory, "plugins");
        pluginDir.mkdirs();
        for (String plugin : mPlugins)
        {
            DebugUtils.info("  "+plugin);
            File pluginSrc = new File(mWorkspace, plugin);
            File pluginDest = new File(pluginDir, plugin);
            FileUtils.copyDir(pluginSrc, pluginDest);
        }
    }
    
    private void copyFeatures() throws IOException
    {
        DebugUtils.info("Copying features");
        File featureDir = new File(mBuildDirectory, "features");
        featureDir.mkdirs();
        for (String feature : mFeatures)
        {
            DebugUtils.info("  "+feature);
            File featureSrc = new File(mWorkspace, feature);
            File featureDest = new File(featureDir, feature);
            FileUtils.copyDir(featureSrc, featureDest);
        }
    }
    
    public static void main(String[] argv)
    {
        BuildAndPackage app = new BuildAndPackage(argv);
        app.run();
    }
}
