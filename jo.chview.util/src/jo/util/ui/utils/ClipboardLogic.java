package jo.util.ui.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

public class ClipboardLogic
{
    public static String getAsText()
    {
        return getAsText(Display.getCurrent());
    }
    public static String getAsText(Display d)
    {
        Clipboard cp = new Clipboard(d);
        Object txt = cp.getContents(TextTransfer.getInstance());
        if ((txt != null) && (txt instanceof String))
            return (String)txt;
        return null;
    }
    public static String getAsHTML()
    {
        return getAsHTML(Display.getCurrent());
    }
    public static String getAsHTML(Display d)
    {
        Clipboard cp = new Clipboard(d);
        Object txt = cp.getContents(HTMLTransfer.getInstance());
        if ((txt != null) && (txt instanceof String))
            return (String)txt;
        return null;
    }
    public static String getAsRTF()
    {
        return getAsRTF(Display.getCurrent());
    }
    public static String getAsRTF(Display d)
    {
        Clipboard cp = new Clipboard(d);
        Object txt = cp.getContents(RTFTransfer.getInstance());
        if ((txt != null) && (txt instanceof String))
            return (String)txt;
        return null;
    }
    
    public static void setAsText(String txt)
    {
        setAsText(txt, Display.getCurrent());
    }
    
    public static void setAsText(String txt, Display d)
    {
        Clipboard cb = new Clipboard(d);
        cb.setContents(new Object[] { txt }, new Transfer[] { TextTransfer.getInstance() });        
    }
    
    public static void setAsHTML(String txt)
    {
        if (!txt.trim().toLowerCase().startsWith("<html"))
            txt = "<html><body>"+txt+"</body></html>";
        Transferable t = new HtmlSelection(txt);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);        
    }
    
    public static void setAsTextHTML(String txt, String html)
    {
        if (txt == null)
            setAsHTML(html);
        else if (html == null)
            setAsText(txt);
        else
        {
            Clipboard cb = new Clipboard(Display.getCurrent());
            cb.setContents(new Object[] { txt, html }, 
                    new Transfer[] { TextTransfer.getInstance(), HTMLTransfer.getInstance() });
        }
    }

    private static class HtmlSelection implements Transferable {
        private static ArrayList<DataFlavor> htmlFlavors = new ArrayList<DataFlavor>();

        static {
            try {
                htmlFlavors.add(new DataFlavor("text/html;class=java.lang.String"));
                htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));
                htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        
        private String mHTML;

        public HtmlSelection(String html)
        {
            this.mHTML = html;
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            return (DataFlavor[])htmlFlavors.toArray(new DataFlavor[htmlFlavors
                    .size()]);
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return htmlFlavors.contains(flavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException
        {
            if (String.class.equals(flavor.getRepresentationClass()))
            {
                return mHTML;
            }
            else if (Reader.class.equals(flavor.getRepresentationClass()))
            {
                return new StringReader(mHTML);
            }
            else if (InputStream.class.equals(flavor.getRepresentationClass()))
            {
                return new ByteArrayInputStream(mHTML.getBytes());
            }
            throw new UnsupportedFlavorException(flavor);
        }

    }
}


/*
 *$Log: ClipboardLogic.java,v $
 *Revision 1.1  2011/11/03 14:07:28  jo
 **** empty log message ***
 *
 *Revision 1.4  2011/05/05 18:23:49  jgrant
 *Table editing added to Plain Text Editor. [19246]
 *
 *Revision 1.3  2011/05/05 16:14:02  jgrant
 *AutoSpellCheck added to Plain Text Editor. [19246]
 *Table editing added to Plain Text Editor. [19246]
 *Default Text size for editor set to 100%. [19246]
 *
 *Revision 1.2  2010/10/19 17:16:32  loredo
 *[15770] Appending Log comment block to source code
 *
 */
