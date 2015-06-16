package jo.d2k.admin.rcp.viz.chview.prefs;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.util.ui.utils.ColorUtils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/*
 * This extends the context with visualization specific values.
 * Some may be persisted in different forms.
 */
public class ChViewPreferencesBean extends ChViewContextBean implements ISelectionProvider
{
    public static final int FOCUS_DIAMOND = 0;
    public static final int FOCUS_SQUARE = 1;
    public static final int FOCUS_CROSS = 2;
    public static final int FOCUS_X = 3;
    public static final int FOCUS_ARROW = 4;
    public static final int FOCUS_STAR = 5;
    public static final String[][] FOCUS_SHAPE_LABEL_MAP = {
        { "Diamond", String.valueOf(FOCUS_DIAMOND), },
        { "Square", String.valueOf(FOCUS_SQUARE), },
        { "Cross", String.valueOf(FOCUS_CROSS), },
        { "X", String.valueOf(FOCUS_X), },
        { "Arrow", String.valueOf(FOCUS_ARROW), },
        { "Star", String.valueOf(FOCUS_STAR), },
    };

    private int mScopeGap;
    private int mScopeHeight1;
    private int mScopeHeight2;
    private int mScopeHeight3;
    private double mGridGap;
    private Color mLinkColor1;
    private Color mLinkColor2;
    private Color mLinkColor3;
    private LineAttributes mLinkStyle1;
    private LineAttributes mLinkStyle2;
    private LineAttributes mLinkStyle3;
    private String mLinkFont;
    private Color mGridColor;
    private LineAttributes mGridStyle;
    private LineAttributes mGridStemStyle;
    private String mStarFont;
    private Color mStarFontColor;
    private Color mScopeColor;
    private LineAttributes mScopeStyle;
    private Color mStarOColor;
    private Color mStarBColor;
    private Color mStarAColor;
    private Color mStarFColor;
    private Color mStarGColor;
    private Color mStarKColor;
    private Color mStarMColor;
    private Color mStarLColor;
    private Color mStarTColor;
    private Color mStarYColor;
    private Color mSelectColor;
    private int   mSelectShape;
    private Color mFocusColor;
    private int   mFocusShape;
    private Color mRoute1Color;
    private Color mRoute2Color;
    private Color mRoute3Color;
    private Color mRoute4Color;
    private Color mRoute5Color;
    private Color mRoute6Color;
    private Color mRoute7Color;
    private Color mRoute8Color;
    private LineAttributes mRoute1Style;
    private LineAttributes mRoute2Style;
    private LineAttributes mRoute3Style;
    private LineAttributes mRoute4Style;
    private LineAttributes mRoute5Style;
    private LineAttributes mRoute6Style;
    private LineAttributes mRoute7Style;
    private LineAttributes mRoute8Style;

    private Point mMouseDown;
    private Rectangle mSelectionBand;
    
    private List<ISelectionChangedListener> mListeners;

    public ChViewPreferencesBean()
    {
        mScopeGap     = 10;
        mScopeHeight1 = 5;
        mScopeHeight2 = 10;
        mScopeHeight3 = 20;
        mGridGap      = 3.26;
        mLinkColor1 = ColorUtils.getColor("0,255,0");
        mLinkColor2 = ColorUtils.getColor("0,192,0");
        mLinkColor3 = ColorUtils.getColor("0,128,0");
        mGridColor = ColorUtils.getColor("blue");
        mScopeColor = ColorUtils.getColor("blue");
        mStarFont = "1|Segoe UI|10.25|0|WINDOWS|1|-11|0|0|0|400|0|0|0|0|3|2|1|34|Segoe UI;";
        mStarFontColor = ColorUtils.getColor("white");
        mLinkFont = "1|Segoe UI|8.25|0|WINDOWS|1|-11|0|0|0|400|0|0|0|0|3|2|1|34|Segoe UI;";
        mStarOColor = ColorUtils.getColor("155,176,255");
        mStarBColor = ColorUtils.getColor("187,204,255");
        mStarAColor = ColorUtils.getColor("226,231,255");
        mStarFColor = ColorUtils.getColor("255,248,248");
        mStarGColor = ColorUtils.getColor("255,240,227");
        mStarKColor = ColorUtils.getColor("255,152,51");
        mStarMColor = ColorUtils.getColor("210,0,51");
        mStarLColor = ColorUtils.getColor("204,0,153");
        mStarTColor = ColorUtils.getColor("153,102,51");
        mStarYColor = ColorUtils.getColor("102,51,0");
        mSelectColor = ColorUtils.getColor("255,255,0");
        mSelectShape = FOCUS_SQUARE;
        mFocusColor = ColorUtils.getColor("255,255,224");
        mFocusShape = FOCUS_DIAMOND;
        mRoute1Color = ColorUtils.getColor("255,0,0");
        mRoute2Color = ColorUtils.getColor("0,255,0");
        mRoute3Color = ColorUtils.getColor("0,0,255");
        mRoute4Color = ColorUtils.getColor("255,255,0");
        mRoute5Color = ColorUtils.getColor("255,0,255");
        mRoute6Color = ColorUtils.getColor("0,255,255");
        mRoute7Color = ColorUtils.getColor("255,255,255");
        mRoute8Color = ColorUtils.getColor("192,192,192");
        mListeners = new ArrayList<ISelectionChangedListener>();
        mSelectionBand = null;
    }

    public int getScopeGap()
    {
        return mScopeGap;
    }

    public void setScopeGap(int scopeGap)
    {
        queuePropertyChange("scopeGap", mScopeGap, scopeGap);
        mScopeGap = scopeGap;
        firePropertyChange();
    }

    public int getScopeHeight1()
    {
        return mScopeHeight1;
    }

    public void setScopeHeight1(int scopeHeight1)
    {
        queuePropertyChange("scopeHeight1", mScopeHeight1, scopeHeight1);
        mScopeHeight1 = scopeHeight1;
        firePropertyChange();
    }

    public int getScopeHeight2()
    {
        return mScopeHeight2;
    }

    public void setScopeHeight2(int scopeHeight2)
    {
        queuePropertyChange("scopeHeight2", mScopeHeight2, scopeHeight2);
        mScopeHeight2 = scopeHeight2;
        firePropertyChange();
    }

    public int getScopeHeight3()
    {
        return mScopeHeight3;
    }

    public void setScopeHeight3(int scopeHeight3)
    {
        queuePropertyChange("scopeHeight3", mScopeHeight3, scopeHeight3);
        mScopeHeight3 = scopeHeight3;
        firePropertyChange();
    }

    public double getGridGap()
    {
        return mGridGap;
    }

    public void setGridGap(double gridGap)
    {
        queuePropertyChange("gridGap", mGridGap, gridGap);
        mGridGap = gridGap;
        firePropertyChange();
    }

    public Color getGridColor()
    {
        return mGridColor;
    }

    public void setGridColor(Color gridColor)
    {
        queuePropertyChange("gridColor", mGridColor, gridColor);
        mGridColor = gridColor;
        firePropertyChange();
    }

    public Color getScopeColor()
    {
        return mScopeColor;
    }

    public void setScopeColor(Color scopeColor)
    {
        queuePropertyChange("scopeColor", mScopeColor, scopeColor);
        mScopeColor = scopeColor;
        firePropertyChange();
    }

    public String getLinkFont()
    {
        return mLinkFont;
    }

    public void setLinkFont(String linkFont)
    {
        queuePropertyChange("linkFont", mLinkFont, linkFont);
        mLinkFont = linkFont;
        firePropertyChange();
    }

    public Color getStarOColor()
    {
        return mStarOColor;
    }

    public void setStarOColor(Color starOColor)
    {
        queuePropertyChange("starOColor", mStarOColor, starOColor);
        mStarOColor = starOColor;
        firePropertyChange();
    }

    public Color getStarBColor()
    {
        return mStarBColor;
    }

    public void setStarBColor(Color starBColor)
    {
        queuePropertyChange("starBColor", mStarBColor, starBColor);
        mStarBColor = starBColor;
        firePropertyChange();
    }

    public Color getStarAColor()
    {
        return mStarAColor;
    }

    public void setStarAColor(Color starAColor)
    {
        queuePropertyChange("starAColor", mStarAColor, starAColor);
        mStarAColor = starAColor;
        firePropertyChange();
    }

    public Color getStarFColor()
    {
        return mStarFColor;
    }

    public void setStarFColor(Color starFColor)
    {
        queuePropertyChange("starFColor", mStarFColor, starFColor);
        mStarFColor = starFColor;
        firePropertyChange();
    }

    public Color getStarGColor()
    {
        return mStarGColor;
    }

    public void setStarGColor(Color starGColor)
    {
        queuePropertyChange("starGColor", mStarGColor, starGColor);
        mStarGColor = starGColor;
        firePropertyChange();
    }

    public Color getStarKColor()
    {
        return mStarKColor;
    }

    public void setStarKColor(Color starKColor)
    {
        queuePropertyChange("starKColor", mStarKColor, starKColor);
        mStarKColor = starKColor;
        firePropertyChange();
    }

    public Color getStarMColor()
    {
        return mStarMColor;
    }

    public void setStarMColor(Color starMColor)
    {
        queuePropertyChange("starMColor", mStarMColor, starMColor);
        mStarMColor = starMColor;
        firePropertyChange();
    }

    public Color getStarLColor()
    {
        return mStarLColor;
    }

    public void setStarLColor(Color starLColor)
    {
        queuePropertyChange("starLColor", mStarLColor, starLColor);
        mStarLColor = starLColor;
        firePropertyChange();
    }

    public Color getStarTColor()
    {
        return mStarTColor;
    }

    public void setStarTColor(Color starTColor)
    {
        queuePropertyChange("starTColor", mStarTColor, starTColor);
        mStarTColor = starTColor;
        firePropertyChange();
    }

    public Color getStarYColor()
    {
        return mStarYColor;
    }

    public void setStarYColor(Color starYColor)
    {
        queuePropertyChange("starYColor", mStarYColor, starYColor);
        mStarYColor = starYColor;
        firePropertyChange();
    }

    public String getStarFont()
    {
        return mStarFont;
    }

    public void setStarFont(String starFont)
    {
        queuePropertyChange("starFont", mStarFont, starFont);
        mStarFont = starFont;
        firePropertyChange();
    }

    public Color getSelectColor()
    {
        return mSelectColor;
    }

    public void setSelectColor(Color selectColor)
    {
        queuePropertyChange("selectColor", mSelectColor, selectColor);
        mSelectColor = selectColor;
        firePropertyChange();
    }

    public Color getFocusColor()
    {
        return mFocusColor;
    }

    public void setFocusColor(Color focusColor)
    {
        queuePropertyChange("focusColor", mFocusColor, focusColor);
        mFocusColor = focusColor;
        firePropertyChange();
    }

    public Color getRoute1Color()
    {
        return mRoute1Color;
    }

    public void setRoute1Color(Color route1Color)
    {
        queuePropertyChange("route1Color", mRoute1Color, route1Color);
        mRoute1Color = route1Color;
        firePropertyChange();
    }

    public Color getRoute2Color()
    {
        return mRoute2Color;
    }

    public void setRoute2Color(Color route2Color)
    {
        queuePropertyChange("route2Color", mRoute2Color, route2Color);
        mRoute2Color = route2Color;
        firePropertyChange();
    }

    public Color getRoute3Color()
    {
        return mRoute3Color;
    }

    public void setRoute3Color(Color route3Color)
    {
        queuePropertyChange("route3Color", mRoute3Color, route3Color);
        mRoute3Color = route3Color;
        firePropertyChange();
    }

    public Color getRoute4Color()
    {
        return mRoute4Color;
    }

    public void setRoute4Color(Color route4Color)
    {
        queuePropertyChange("route4Color", mRoute4Color, route4Color);
        mRoute4Color = route4Color;
        firePropertyChange();
    }

    public Color getRoute5Color()
    {
        return mRoute5Color;
    }

    public void setRoute5Color(Color route5Color)
    {
        queuePropertyChange("route5Color", mRoute5Color, route5Color);
        mRoute5Color = route5Color;
        firePropertyChange();
    }

    public Color getRoute6Color()
    {
        return mRoute6Color;
    }

    public void setRoute6Color(Color route6Color)
    {
        queuePropertyChange("route6Color", mRoute6Color, route6Color);
        mRoute6Color = route6Color;
        firePropertyChange();
    }

    public Color getRoute7Color()
    {
        return mRoute7Color;
    }

    public void setRoute7Color(Color route7Color)
    {
        queuePropertyChange("route7Color", mRoute7Color, route7Color);
        mRoute7Color = route7Color;
        firePropertyChange();
    }

    public Color getRoute8Color()
    {
        return mRoute8Color;
    }

    public void setRoute8Color(Color route8Color)
    {
        queuePropertyChange("route8Color", mRoute8Color, route8Color);
        mRoute8Color = route8Color;
        firePropertyChange();
    }

    public Color getLinkColor1()
    {
        return mLinkColor1;
    }

    public void setLinkColor1(Color linkColor1)
    {
        queuePropertyChange("linkColor1", mLinkColor1, linkColor1);
        mLinkColor1 = linkColor1;
        firePropertyChange();
    }

    public Color getLinkColor2()
    {
        return mLinkColor2;
    }

    public void setLinkColor2(Color linkColor2)
    {
        queuePropertyChange("linkColor2", mLinkColor2, linkColor2);
        mLinkColor2 = linkColor2;
        firePropertyChange();
    }

    public Color getLinkColor3()
    {
        return mLinkColor3;
    }

    public void setLinkColor3(Color linkColor3)
    {
        queuePropertyChange("linkColor3", mLinkColor3, linkColor3);
        mLinkColor3 = linkColor3;
        firePropertyChange();
    }

    public Color getStarFontColor()
    {
        return mStarFontColor;
    }

    public void setStarFontColor(Color starFontColor)
    {
        queuePropertyChange("starFontColor", mStarFontColor, starFontColor);
        mStarFontColor = starFontColor;
        firePropertyChange();
    }

    public int getFocusShape()
    {
        return mFocusShape;
    }

    public void setFocusShape(int focusShape)
    {
        queuePropertyChange("focusShape", mFocusShape, focusShape);
        mFocusShape = focusShape;
        firePropertyChange();
    }

    public int getSelectShape()
    {
        return mSelectShape;
    }

    public void setSelectShape(int selectShape)
    {
        queuePropertyChange("selectShape", mSelectShape, selectShape);
        mSelectShape = selectShape;
        firePropertyChange();
    }

    public LineAttributes getLinkStyle1()
    {
        return mLinkStyle1;
    }

    public void setLinkStyle1(LineAttributes linkStyle1)
    {
        queuePropertyChange("linkStyle1", mLinkStyle1, linkStyle1);
        mLinkStyle1 = linkStyle1;
    }

    public LineAttributes getLinkStyle2()
    {
        return mLinkStyle2;
    }

    public void setLinkStyle2(LineAttributes linkStyle2)
    {
        queuePropertyChange("linkStyle2", mLinkStyle2, linkStyle2);
        mLinkStyle2 = linkStyle2;
    }

    public LineAttributes getLinkStyle3()
    {
        return mLinkStyle3;
    }

    public void setLinkStyle3(LineAttributes linkStyle3)
    {
        queuePropertyChange("linkStyle3", mLinkStyle3, linkStyle3);
        mLinkStyle3 = linkStyle3;
    }

    public LineAttributes getGridStyle()
    {
        return mGridStyle;
    }

    public void setGridStyle(LineAttributes gridStyle)
    {
        queuePropertyChange("gridStyle", mGridStyle, gridStyle);
        mGridStyle = gridStyle;
    }

    public LineAttributes getScopeStyle()
    {
        return mScopeStyle;
    }

    public void setScopeStyle(LineAttributes scopeStyle)
    {
        queuePropertyChange("scopeStyle", mScopeStyle, scopeStyle);
        mScopeStyle = scopeStyle;
    }

    public LineAttributes getRoute1Style()
    {
        return mRoute1Style;
    }

    public void setRoute1Style(LineAttributes route1Style)
    {
        queuePropertyChange("route1Style", mRoute1Style, route1Style);
        mRoute1Style = route1Style;
    }

    public LineAttributes getRoute2Style()
    {
        return mRoute2Style;
    }

    public void setRoute2Style(LineAttributes route2Style)
    {
        queuePropertyChange("route2Style", mRoute2Style, route2Style);
        mRoute2Style = route2Style;
    }

    public LineAttributes getRoute3Style()
    {
        return mRoute3Style;
    }

    public void setRoute3Style(LineAttributes route3Style)
    {
        queuePropertyChange("route3Style", mRoute3Style, route3Style);
        mRoute3Style = route3Style;
    }

    public LineAttributes getRoute4Style()
    {
        return mRoute4Style;
    }

    public void setRoute4Style(LineAttributes route4Style)
    {
        queuePropertyChange("route4Style", mRoute4Style, route4Style);
        mRoute4Style = route4Style;
    }

    public LineAttributes getRoute5Style()
    {
        return mRoute5Style;
    }

    public void setRoute5Style(LineAttributes route5Style)
    {
        queuePropertyChange("route5Style", mRoute5Style, route5Style);
        mRoute5Style = route5Style;
    }

    public LineAttributes getRoute6Style()
    {
        return mRoute6Style;
    }

    public void setRoute6Style(LineAttributes route6Style)
    {
        queuePropertyChange("route6Style", mRoute6Style, route6Style);
        mRoute6Style = route6Style;
    }

    public LineAttributes getRoute7Style()
    {
        return mRoute7Style;
    }

    public void setRoute7Style(LineAttributes route7Style)
    {
        queuePropertyChange("route7Style", mRoute7Style, route7Style);
        mRoute7Style = route7Style;
    }

    public LineAttributes getRoute8Style()
    {
        return mRoute8Style;
    }

    public void setRoute8Style(LineAttributes route8Style)
    {
        queuePropertyChange("route8Style", mRoute8Style, route8Style);
        mRoute8Style = route8Style;
    }

    public LineAttributes getGridStemStyle()
    {
        return mGridStemStyle;
    }

    public void setGridStemStyle(LineAttributes gridStemStyle)
    {
        queuePropertyChange("gridStemStyle", mGridStemStyle, gridStemStyle);
        mGridStemStyle = gridStemStyle;
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        mListeners.add(listener);
    }

    @Override
    public ISelection getSelection()
    {
        if (getFocus() != null)
            return new StructuredSelection(getFocus());
        else
            return new StructuredSelection();
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        mListeners.remove(listener);
    }

    @Override
    public void setSelection(ISelection sel)
    {
        if (sel.isEmpty())
            setFocus(null);
        else
            setFocus((StarBean)((StructuredSelection)sel).getFirstElement());
    }
    
    public Point getMouseDown()
    {
        return mMouseDown;
    }
    public void setMouseDown(Point mouseDown)
    {
        mMouseDown = mouseDown;
    }
    public void setFocus(StarBean focus)
    {
        if (getFocus() == focus)
            return;
        super.setFocus(focus);
        ISelection sel;
        if (getFocus() == null)
            sel = new StructuredSelection();
        else
            sel = new StructuredSelection(getFocus());
        SelectionChangedEvent event = new SelectionChangedEvent(this, sel);
        for (ISelectionChangedListener listener : mListeners)
            listener.selectionChanged(event);
        firePropertyChange();
    }

    public Rectangle getSelectionBand()
    {
        return mSelectionBand;
    }

    public void setSelectionBand(Rectangle selectionBand)
    {
        mSelectionBand = selectionBand;
    }
}
