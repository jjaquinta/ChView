package jo.d2k.stars.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import jo.d2k.stars.data.StarsInFiction;
import jo.util.utils.io.FileUtils;
import jo.util.utils.io.StreamUtils;
import jo.util.utils.obj.StringUtils;

public class StarsInFictionOverrides
{
    private static final List<StarsInFiction>   mStars = new ArrayList<StarsInFiction>();
    
    public static final void main(String[] argv)
    {
        try
        {
            readList();
            System.out.println(mStars.size()+" references");
            findSinbadNumbers();
            System.out.println(mStars.size()+" with sinbad numbers");
            writeOutput();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private static void findSinbadNumbers() throws IOException
    {
        for (Iterator<StarsInFiction> i = mStars.iterator(); i.hasNext(); )
        {
            StarsInFiction star = i.next();
            String ohtml = getHTML("http://en.wikipedia.org"+star.getWikiURL(), null);
            /*
            String html = ohtml;
            for (;;)
            {
                int o = html.indexOf("http://simbad.u-strasbg.fr/simbad/sim-id?Ident=");
                if (o < 0)
                    break;
                html = html.substring(o);
                o = html.indexOf("\"");
                if (o < 0)
                    break;
                String link = html.substring(47, o);
                html = html.substring(o+1);
                o = link.indexOf('&');
                if (o >= 0)
                    link = link.substring(0, o);
                link = link.replace("+", " ");
                link = link.replace("%20", " ");
                link = link.replace("%2B", "+");
                o = link.indexOf("<");
                if (o >= 0)
                    link = link.substring(0, o);
                if (!star.getSimbadURLs().contains(link))
                    star.getSimbadURLs().add(link);
            }
            */
            if (star.getSimbadURLs().size() == 0)
            {
                String html = ohtml;
                int o = html.indexOf("<a href=\"/wiki/Star_catalogue\" title=\"Star catalogue\">Other designations</a>");
                if (o >= 0)
                {
                    html = html.substring(o + 76);
                    o = html.indexOf("</tbody>");
                    if (o < 0)
                        o = html.indexOf("</table>");
                    if (o >= 0)
                    {
                        html = html.substring(0, o);
                        html = html.replaceAll("<[^>]*>", "").trim();
                        html = html.replaceAll("\\[[0-9]*\\]", "").trim();
                        html = StringUtils.removeEntities(html);
                        if (html.endsWith("."))
                            html = html.substring(0, html.length() - 1);
                        o = html.indexOf('\r');
                        if (o >= 0)
                            html = html.substring(0, o);
                        o = html.indexOf('\n');
                        if (o >= 0)
                            html = html.substring(0, o);
                        for (StringTokenizer st = new StringTokenizer(html, ","); st.hasMoreTokens(); )
                        {
                            String desig = st.nextToken().trim();
                            if (!star.getSimbadURLs().contains(desig))
                                star.getSimbadURLs().add(desig);
                        }
                    }
                }
            }
//            System.out.println(star.getPopularName()+": "+star.getSimbadURLs().size());
//            for (String link : star.getSimbadURLs())
//                System.out.println("  "+link);
            if (star.getSimbadURLs().size() == 0)
            {
                System.out.println("No designations for "+star.getPopularName());
                i.remove();
            }
        }
    }
    
    public static String getHTML(String path, String referer) throws UnsupportedEncodingException
    {
        if (path == null)
            return null;
        String html = null;
        try
        {
            URL u = new URL(path);
            InputStream is = u.openStream();
            html = StreamUtils.readStreamAsString(is);
            is.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return html;
        /*
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HTTPThread getThread = new HTTPThread(os, path, referer, null);
        getThread.start();
        long killTime = System.currentTimeMillis() + 300000;
        while (!getThread.isDone())
        {
            if (System.currentTimeMillis() > killTime)
            {
                getThread.setDone(true);
                getThread.setError("timeout");
                break;
            }
        }
        if (getThread.isDone() && (getThread.getError() == null))
        {
            String html = new String(os.toByteArray(), "utf-8");
            return html;
        }
        return null;
        */
    }

    private static void readList() throws IOException
    {
        String html = FileUtils.readFileAsString("c:\\temp\\data\\chview2\\simbad\\Stars_and_planetary_systems_in_fiction.htm", "utf-8");
        
        for (;;)
        {
            int o = html.indexOf("<span class=\"mw-headline\"");
            if (o < 0)
                break;
            html = html.substring(o);
            o = html.indexOf("</span>");
            if (o < 0)
                break;
            String headline = html.substring(0, o);
            html = html.substring(o + 7);
            findLinksIn(headline);
        }        
    }
    
    private static void writeOutput() throws IOException
    {
        File f = new File("c:\\temp\\data\\chview2\\simbad\\stars_in_fiction.txt");
        BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
        wtr.write('\ufeff');
        for (StarsInFiction star : mStars)
        {
            wtr.write(star.getPopularName());
            wtr.write("\t");
            wtr.write(star.getWikiURL());
            for (String desig : star.getSimbadURLs())
            {
                wtr.write("\t");
                wtr.write(desig);
            }
            wtr.newLine();
        }        
        wtr.close();
    }

    private static void findLinksIn(String html)
    {
        for (;;)
        {
            int o = html.indexOf("<a ");
            if (o < 0)
                break;
            html = html.substring(o);
            o = html.indexOf("</a>");
            if (o < 0)
                break;
            String link = html.substring(0, o + 4);
            html = html.substring(o + 4);
            link = link.substring(0, link.length() - 4); // chop last tag
            o = link.indexOf('>');
            if (o < 0)
                continue;
            String text = link.substring(o + 1); // text of tag
            if (text.indexOf('<') >= 0)
                continue; // complicated link, not what we're looking for
            if (text.indexOf("ISBN") >= 0)
                continue; // book reference
            link = link.substring(0, o+1); // just the link bit
            o = link.indexOf("href=\"");
            if (o < 0)
                continue; // not a real link
            String href = link.substring(o + 6);
            o = href.indexOf("\"");
            if (o < 0)
                continue; // not a real link at all
            href = href.substring(0, o);
            if (href.indexOf(':') >= 0)
                continue; // complex link
            if (href.indexOf('#') >= 0)
                continue; // complex link
            if (!href.startsWith("/wiki/"))
                continue; // not to a wiki article
            if (href.endsWith("_in_fiction"))
                continue; // not a star article
            StarsInFiction star = new StarsInFiction();
            star.setPopularName(text);
            star.setWikiURL(href);
            mStars.add(star);
        }
    }
}
