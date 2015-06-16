package jo.d2k.admin.rcp.sys.ui;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.sys.ui.schema.StarSchemaUIController;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.StarSchemaLogic;
import jo.util.ui.act.GenericAction;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class StarPanel extends Composite implements IToText
{
    private StarBean                mStar;
    private boolean                 mReadOnly;
    
    private Text    mName;
    private Text    mCommonName;
    private Text    mHIPName;
    private Text    mGJName;
    private Text    mHDName;
    private Text    mHRName;
    private Text    mSAOName;
    private Text    mTwoMassName;
    private Text    mQuadrant;
    private Text    mCoordsX;
    private Text    mCoordsY;
    private Text    mCoordsZ;
    private Text    mSpectra;
    private Text    mAbsMag;
    private Control mSimbad;
    private Control mWikipedia;
    private Text    mGenerated;
    private StarSchemaUIController  mSchemaController;

    public StarPanel(Composite parent, int style)
    {
        super(parent, style);
        int editStyle = (style&SWT.READ_ONLY);
        mReadOnly = editStyle != 0;
        setLayout(new GridLayout(7, false));
        GridUtils.makeLabel(this, "Name:", "");
        mName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Common Name:", "");
        mCommonName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Hipparcos:", "");
        mHIPName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Gliese-Jahreiﬂ:", "");
        mGJName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Henry Draper:", "");
        mHDName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Harvard Revised:", "");
        mHRName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "SAO:", "");
        mSAOName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "2Mass:", "");
        mTwoMassName = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Quadrant:", "");
        mQuadrant = GridUtils.makeText(this, SWT.READ_ONLY, "6x1 fill=h");
        GridUtils.makeLabel(this, "Coords:", "");
        mCoordsX = GridUtils.makeText(this, editStyle, "");
        GridUtils.makeLabel(this, ",", "");
        mCoordsY = GridUtils.makeText(this, editStyle, "");
        GridUtils.makeLabel(this, ",", "");
        mCoordsZ = GridUtils.makeText(this, editStyle, "");
        GridUtils.makeLabel(this, "", "fill=h");
        GridUtils.makeLabel(this, "Spectrum:", "");
        mSpectra = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Abs Mag:", "");
        mAbsMag = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Simbad:", "");
        if (mReadOnly)
        {
            mSimbad = new Link(this, SWT.NULL);
            GridUtils.setLayoutData(mSimbad, "6x1 fill=h");
            ((Link)mSimbad).addListener(SWT.Selection, new Listener() {                
                @Override
                public void handleEvent(Event e)
                {
                    doLink(e.text);
                }
            });
        }
        else
            mSimbad = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Wikipedia:", "");
        if (mReadOnly)
        {
            mWikipedia = new Link(this, SWT.NULL);
            GridUtils.setLayoutData(mWikipedia, "6x1 fill=h");
            ((Link)mWikipedia).addListener(SWT.Selection, new Listener() {                
                @Override
                public void handleEvent(Event e)
                {
                    doLink(e.text);
                }
            });
        }
        else
            mWikipedia = GridUtils.makeText(this, editStyle, "6x1 fill=h");
        GridUtils.makeLabel(this, "Generated:", "");
        mGenerated = GridUtils.makeText(this, SWT.READ_ONLY, "6x1 fill=h");
        mSchemaController = new StarSchemaUIController(this, editStyle);
    }

    public StarBean getStar()
    {
        if (!mReadOnly && (mStar != null))
        {
            mStar.setName(mName.getText());
            mStar.setCommonName(mCommonName.getText());
            mStar.setHIPName(mHIPName.getText());
            mStar.setGJName(mGJName.getText());
            mStar.setHDName(mHDName.getText());
            mStar.setHRName(mHRName.getText());
            mStar.setSAOName(mSAOName.getText());
            mStar.setTwoMassName(mTwoMassName.getText());
            mStar.setX(DoubleUtils.parseDouble(mCoordsX.getText()));
            mStar.setY(DoubleUtils.parseDouble(mCoordsY.getText()));
            mStar.setZ(DoubleUtils.parseDouble(mCoordsZ.getText()));
            mStar.setSpectra(mSpectra.getText());
            mStar.setAbsMag(DoubleUtils.parseDouble(mAbsMag.getText()));
            mStar.setSimbadURL(ControlUtils.getText(mSimbad));
            mStar.setWikipediaURL(ControlUtils.getText(mWikipedia));
            mSchemaController.getStar();
        }
        return mStar;
    }

    public void setStar(StarBean star)
    {
        mStar = star;
        if (mStar == null)
        {
            mName.setText("");
            mCommonName.setText("");
            mHIPName.setText("");
            mGJName.setText("");
            mHDName.setText("");
            mHRName.setText("");
            mSAOName.setText("");
            mTwoMassName.setText("");
            mQuadrant.setText("");
            mCoordsX.setText("");
            mCoordsY.setText("");
            mCoordsZ.setText("");
            mSpectra.setText("");
            mAbsMag.setText("");
            ControlUtils.setText(mSimbad, "");
            ControlUtils.setText(mWikipedia, "");
            mGenerated.setText("");
        }
        else
        {
            ControlUtils.setText(mName, mStar.getName());
            ControlUtils.setText(mCommonName, mStar.getCommonName());
            ControlUtils.setText(mHIPName, mStar.getHIPName());
            ControlUtils.setText(mGJName, mStar.getGJName());
            ControlUtils.setText(mHDName, mStar.getHDName());
            ControlUtils.setText(mHRName, mStar.getHRName());
            ControlUtils.setText(mSAOName, mStar.getSAOName());
            ControlUtils.setText(mTwoMassName, mStar.getTwoMassName());
            ControlUtils.setText(mQuadrant, mStar.getQuadrant());
            mCoordsX.setText(String.format("%.2f", mStar.getX()));
            mCoordsY.setText(String.format("%.2f", mStar.getY()));
            mCoordsZ.setText(String.format("%.2f", mStar.getZ()));
            ControlUtils.setText(mSpectra, mStar.getSpectra());
            mAbsMag.setText(String.format("%.2f", mStar.getAbsMag()));
            ControlUtils.setText(mSimbad, mStar.getSimbadURL());
            ControlUtils.setText(mWikipedia, mStar.getWikipediaURL());
            mGenerated.setText(String.valueOf(mStar.isGenerated()));
            mSchemaController.setStar(mStar);
        }
    }
    
    private void doLink(String url)
    {
        if (StringUtils.isTrivial(url))
            return;
        try
        {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public String toText()
    {
        if (mStar == null)
            return null;
        List<String> labels = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        getTextRepresentation(labels, values);
        int width = labels.get(0).length();
        for (int i = 1; i < labels.size(); i++)
            width = Math.max(width, labels.get(i).length());
        width++;
        StringBuffer sb = new StringBuffer();
        String nl = System.getProperty("line.separator");
        for (int i = 0; i < labels.size(); i++)
        {
            sb.append(FormatUtils.leftJustify(labels.get(i), width));
            sb.append(StringUtils.safe(values.get(i)));
            sb.append(nl);
        }
        return sb.toString();
    }

    @Override
    public String toHTML()
    {
        if (mStar == null)
            return null;
        List<String> labels = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        getTextRepresentation(labels, values);
        StringBuffer sb = new StringBuffer();
        String nl = System.getProperty("line.separator");
        sb.append("<table>");
        for (int i = 0; i < labels.size(); i++)
        {
            sb.append("<tr><td>");
            sb.append(labels.get(i));
            sb.append("</td><td>");
            String value = StringUtils.safe(values.get(i));
            if (value.startsWith("http"))
                value = "<a href=\""+value+"\">"+value+"</a>";
            sb.append(value);
            sb.append("</td>/<tr>");
            sb.append(nl);
        }
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public void doEdit()
    {
        DlgStarEdit dlg = new DlgStarEdit(getShell());
        dlg.setStar(mStar);
        if (dlg.open() != Dialog.OK)
            return;
        StarBean star = dlg.getStar();
        ChViewVisualizationLogic.updateStar(star);
        setStar(star);
    }

    @Override
    public void doDel()
    {
        if (!GenericAction.openQuestion("Routes", "Delete "+ChViewRenderLogic.getStarName(mStar)+"?"))
            return;
        List<StarBean> stars = new ArrayList<>();
        stars.add(mStar);
        ChViewVisualizationLogic.deleteStars(stars);
        setStar(null);
    }

    public void getTextRepresentation(List<String> labels, List<String> values)
    {
        labels.add("Name:");
        values.add(mStar.getName());
        labels.add("Common Name:");
        values.add(mStar.getCommonName());
        labels.add("Hipparcos:");
        values.add(mStar.getHIPName());
        labels.add("Gliese-Jahreiﬂ:");
        values.add(mStar.getGJName());
        labels.add("Henry Draper:");
        values.add(mStar.getHDName());
        labels.add("Harvard Revised:");
        values.add(mStar.getHRName());
        labels.add("SAO:");
        values.add(mStar.getSAOName());
        labels.add("2Mass:");
        values.add(mStar.getTwoMassName());
        labels.add("Quadrant:");
        values.add(mStar.getQuadrant());
        labels.add("Coords:");
        values.add(String.format("%.2f", mStar.getX())+","+String.format("%.2f", mStar.getY())+","+String.format("%.2f", mStar.getZ()));
        labels.add("Spectrum:");
        values.add(mStar.getSpectra());
        labels.add("Abs Mag:");
        values.add(String.format("%.2f", mStar.getAbsMag()));
        labels.add("Simbad:");
        values.add(mStar.getSimbadURL());
        labels.add("Wikipedia:");
        values.add("http://en.wikipedia.org/"+mStar.getWikipediaURL());
        labels.add("Generated:");
        values.add(String.valueOf(mStar.isGenerated()));
        // metadata
        Map<String,String> md = mStar.getMetadata();
        for (StarSchemaBean schema : StarSchemaLogic.getSchemas())
        {
            labels.add(schema.getTitle()+":");
            String value = md.get(schema.getMetadataID());
            values.add(StringUtils.safe(value));
        }
    }
}
