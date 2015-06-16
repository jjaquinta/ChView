package chuck.terran.admin.handlers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLEncoder;

import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.ui.act.GenericAction;
import jo.util.utils.ZipUtils;
import jo.util.utils.io.FileUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class HandlerReportBug extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doReportBug();
        return null;
    }

    public static void doReportBug()
    {
        boolean anyMail = Desktop.getDesktop().isSupported(Desktop.Action.MAIL);
        int op = -1;
        if (anyMail)
        {
            MessageDialog md = new MessageDialog(Display.getDefault().getActiveShell(), 
                    "Report a Bug", null, "To zip up log files and prepare a mail to support, click below", 
                    MessageDialog.QUESTION, 
                    new String[] { "Open Mail Client", "Open Text Editor", "Cancel" }, 
                    0);
            op = md.open();
            if (op == 2)
                op = -1;
        }
        else
        {
            MessageDialog md = new MessageDialog(Display.getDefault().getActiveShell(), 
                    "Report a Bug", null, "To zip up log files and prepare a mail to support, click below", 
                    MessageDialog.QUESTION, 
                    new String[] { "Open Text Editor", "Cancel" }, 
                    0);
            op = md.open();
            if (op == 1)
                op = -1;
            else if (op == 0)
                op = 1;
        }
        if (op == -1)
            return;
        try
        {
            String to = MetadataLogic.getValue("db.info", -1, "contact");
            if (StringUtils.isTrivial(to))
                to = "jo_auto@111george.com";
            String name = RuntimeLogic.getInstance().getDataSource().getName();
            File zipFile = File.createTempFile("chvBuf", ".zip");
            FileOutputStream fos = new FileOutputStream(zipFile);
            File ws = Platform.getLocation().toFile();
            File from = new File(ws, ".metadata");
            ZipUtils.zip(from, fos);
            fos.close();
            if (op == 0)
            {
                String url = "mailto:"+to;
                url += "?subject="+URLEncoder.encode("Bug report for "+name, "utf-8");
                url += "&body="+URLEncoder.encode("(Please describe bug and attach "+zipFile.getCanonicalPath()+")", "utf-8");
                URI mailtoURI = new URI(url);
                Desktop.getDesktop().mail(mailtoURI);
            }
            else
            {
                StringBuffer sb = new StringBuffer();
                sb.append("To: "+to+System.getProperty("line.separator"));
                sb.append("Subject: Bug report for "+name+System.getProperty("line.separator"));
                sb.append(System.getProperty("line.separator"));
                sb.append("(Please describe bug and attach "+zipFile.getCanonicalPath()+")"+name+System.getProperty("line.separator"));
                File txtFile = File.createTempFile("chvBuf", ".txt");
                FileUtils.writeFile(sb.toString(), txtFile);
                Desktop.getDesktop().open(txtFile);
            }
        }
        catch (Exception e)
        {
            GenericAction.openError("Report A Bug", "Bug while reporting bug!", e);
        }
    }

}
