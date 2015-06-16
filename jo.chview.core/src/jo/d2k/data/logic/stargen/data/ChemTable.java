package jo.d2k.data.logic.stargen.data;

public class ChemTable
{
    public int    num;
    public String symbol;
    public String html_symbol;
    public String name;
    public double weight;
    public double melt;
    public double boil;
    public double density;
    public double abunde;
    public double abunds;
    public double reactivity;
    public double max_ipp;    // Max inspired partial pressure im millibars
    public String unicode_symbol;

    public ChemTable(int _num, String _symbol, String _html_symbol,
            String _name, double _weight, double _melt, double _boil,
            double _density, double _abunde, double _abunds,
            double _reactivity, double _max_ipp)
    {
        num = _num;
        symbol = _symbol;
        html_symbol = _html_symbol;
        name = _name;
        weight = _weight;
        melt = _melt;
        boil = _boil;
        density = _density;
        abunde = _abunde;
        abunds = _abunds;
        reactivity = _reactivity;
        max_ipp = _max_ipp;
        unicode_symbol = html_symbol;
        unicode_symbol = unicode_symbol.replace("<SUB><SMALL>2</SMALL></SUB>", "\u2082");
        unicode_symbol = unicode_symbol.replace("<SUB><SMALL>3</SMALL></SUB>", "\u2083");
        unicode_symbol = unicode_symbol.replace("<SUB><SMALL>4</SMALL></SUB>", "\u2084");
    }
}
