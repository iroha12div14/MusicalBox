package trophy;

import java.util.Arrays;
import java.util.List;

public class Trophies {
    public final static int NONE    = 0;


    public final static int MAX_COMBO_30PLUS    = 101;
    public final static int MAX_COMBO_100PLUS   = 102;
    public final static int MAX_COMBO_300PLUS   = 103;
    public final static int MAX_COMBO_1000PLUS  = 104;

    public final static int PERFECT_100PLUS     = 112;
    public final static int PERFECT_300PLUS     = 113;
    public final static int PERFECT_1000PLUS    = 114;

    public final static int MISS_1000PLUS_MAX_COMBO_0 = 124;

    public final static int FC_ACV_98PLUS   = 131;
    public final static int ACV_100         = 132;

    public final static int FC_ACV_LT5_OOPS_1000PLUS    = 141;
    public final static int FC_ACV_LT25_OOPS_0          = 142;


    public final static int ALL_PLAY_COUNT_10PLUS   = 201;
    public final static int ALL_PLAY_COUNT_100PLUS  = 202;
    public final static int ALL_PLAY_COUNT_1000PLUS = 203;

    public final static int ACV_POINT_10000PLUS     = 211;
    public final static int ACV_POINT_100000PLUS    = 212;
    public final static int ACV_POINT_1000000PLUS   = 213;


    // ---------------------------------------------------- //

    public final static List<Integer> member = Arrays.asList(
            MAX_COMBO_30PLUS, MAX_COMBO_100PLUS, MAX_COMBO_300PLUS, MAX_COMBO_1000PLUS,
            PERFECT_100PLUS, PERFECT_300PLUS, PERFECT_1000PLUS,
            MISS_1000PLUS_MAX_COMBO_0,
            FC_ACV_98PLUS, ACV_100,
            FC_ACV_LT5_OOPS_1000PLUS, FC_ACV_LT25_OOPS_0,
            ALL_PLAY_COUNT_10PLUS, ALL_PLAY_COUNT_100PLUS, ALL_PLAY_COUNT_1000PLUS,
            ACV_POINT_10000PLUS, ACV_POINT_100000PLUS, ACV_POINT_1000000PLUS
    );
}
