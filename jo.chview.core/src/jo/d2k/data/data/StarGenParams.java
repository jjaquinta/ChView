package jo.d2k.data.data;

import jo.util.beans.Bean;

public class StarGenParams extends Bean
{
    public boolean GENERATE = true;
    public double EXCLUSION_ZONE = 15;
    
    // chances out of 1000 for each spectral type
    public int[] SPECTRUM_FREQ = {
            2,   47,   59,  216,  284,  229,  155,    5,    3,    0
        };
    public int[][] SPECTRUM_CLASS_FREQ = {
            //     I    II   III    IV     V    VI     D
              {  262,   48,   71,   95,  524,    0,    0 }, // O
              {   52,   15,  193,  216,  520,    0,    4 }, // B
              {   18,    7,   86,  159,  727,    0,    3 }, // A
              {    5,    6,   25,   87,  876,    0,    1 }, // F
              {    6,    9,   74,   66,  843,    0,    2 }, // G
              {    8,    8,  242,   42,  696,    0,    4 }, // K
              {    3,    3,   85,    1,  824,    0,   84 }, // M
              {    0,    0,    0,    0,  981,    0,   19 }, // L
              {    0,    0,    0,    0, 1000,    0,    0 }, // T
              {    0,    0,    0,    0, 1000,    0,    0 }, // Y
              };
    public String[] CLASS_NAME = {
        "I", "II", "III", "IV", "V", "VI", "D",
    };
    
    public double[][] ABS_MAG_FREQ = {
            //     I    II   III    IV     V    VI     D
              { -4.5, -4.6, -5.3, -4.1, -1.0,  0.0,  0.0 }, // O
              { -4.9, -2.6, -1.7, -1.6, -0.8,  0.0,  2.0 }, // B
              { -4.6, -2.6,  0.8,  1.1,  1.3,  0.0,  2.0 }, // A
              { -4.3, -1.3,  1.7,  2.6,  3.5,  0.0,  1.7 }, // F
              { -3.4, -1.4,  0.6,  3.8,  4.7,  0.0,  3.4 }, // G
              { -3.0, -1.9,  0.3,  3.7,  6.4,  0.0,  7.3 }, // K
              { -3.0, -2.6, -0.9,  9.7,  9.0,  0.0, 12.2 }, // M
              {  0.0,  0.0,  0.0,  0.0, -0.4,  0.0, -2.3 }, // L
              {  0.0,  0.0,  0.0,  0.0, -0.8,  0.0,  0.0 }, // T
              {  0.0,  0.0,  0.0,  0.0,  0.9,  0.0,  0.0 }, // Y
              };
    
    public int[][] SECONDARY_NUM_FREQ = {
            //     0     1     2
              { 1000,    0,    0 }, // O
              { 1000,    0,    0 }, // B
              {  996,    4,    0 }, // A
              {  998,    2,    0 }, // F
              {  996,    4,    0 }, // G
              {  995,    4,    1 }, // K
              {  986,   12,    2 }, // M
              { 1000,    0,    0 }, // L
              {  974,   26,    0 }, // T
              {  875,  125,    0 }, // Y
              };
    
    public int[][] SECONDARY_TYPE_FREQ = {
            //     O     B     A     F     G     K     M     L     T     Y
              {    0,    0,    0,    0,    0, 1000,    0,    0,    0,    0 }, // O
              {    0,    0,    0,    0,    0, 1000,    0,    0,    0,    0 }, // B
              {    0,    0,  400,  200,    0,  200,  200,    0,    0,    0 }, // A
              {    0,    0,    0,  250,  250,  125,  375,    0,    0,    0 }, // F
              {    0,    0,    0,    0,  393,  392,  143,   36,   36,    0 }, // G
              {    0,    0,    0,    0,    0,  552,  276,   34,  138,    0 }, // K
              {    0,    0,    0,    0,   18,   -1,  873,   55,   55,    0 }, // M
              {    0,    0,    0,    0,    0, 1000,    0,    0,    0,    0 }, // L
              {    0,    0,    0,    0,    0,    0,  500,    0,    0,  500 }, // T
              {    0,    0,    0,    0,    0,    0, 1000,    0,    0,    0 }, // Y
              };
    
    public double[] SECONDARY_DISTANCE_FREQ = {
        0.001, .005, 0.037, 0.082, 0.180, 0.225,    
    };

    public String[] GREEK_NAMES = {
        "Alpha",
        "Beta",
        "Gamma",
        "Delta",
        "Epsilon",
        "Zeta",
        "Eta",
        "Theta",
        "Iota",
        "Kappa",
        "Lambda",
        "Mu",
        "Nu",
        "Omicron",
    };

    public String PREFIX = "UN "; 
}
