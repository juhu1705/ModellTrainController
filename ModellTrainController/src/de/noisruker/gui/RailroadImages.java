package de.noisruker.gui;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;

public class RailroadImages {

    public static final Image EMPTY = new Image("/assets/textures/railroad/empty_plate.png");
    public static final Image EMPTY_2 = new Image("/assets/textures/railroad/empty_plate_2.png");

    public static final Image STRAIGHT_VERTICAL = new Image("/assets/textures/railroad/straight_rails/straight_rail_vertical.png");
    public static final Image STRAIGHT_HORIZONTAL = new Image("/assets/textures/railroad/straight_rails/straight_rail_horizontal.png");
    public static final Image STRAIGHT_CUT_VERTICAL = new Image("/assets/textures/railroad/straight_rails/straight_cut_rail_vertical.png");
    public static final Image STRAIGHT_CUT_HORIZONTAL = new Image("/assets/textures/railroad/straight_rails/straight_cut_rail_horizontal.png");
    public static final Image CURVE_NORTH_EAST = new Image("/assets/textures/railroad/curves/curve_o_n.png");
    public static final Image CURVE_SOUTH_EAST = new Image("/assets/textures/railroad/curves/curve_o_s.png");
    public static final Image CURVE_NORTH_WEST = new Image("/assets/textures/railroad/curves/curve_w_n.png");
    public static final Image CURVE_SOUTH_WEST = new Image("/assets/textures/railroad/curves/curve_w_s.png");
    public static final Image SWITCH_NORTH_1 = new Image("/assets/textures/railroad/switches/n/switch_straight_up.png");
    public static final Image SWITCH_NORTH_2 = new Image("/assets/textures/railroad/switches/n/switch_straight_down.png");
    public static final Image SWITCH_NORTH_3 = new Image("/assets/textures/railroad/switches/n/switch_up_down.png");
    public static final Image SWITCH_WEST_1 = new Image("/assets/textures/railroad/switches/w/switch_straight_up.png");
    public static final Image SWITCH_WEST_2 = new Image("/assets/textures/railroad/switches/w/switch_straight_down.png");
    public static final Image SWITCH_WEST_3 = new Image("/assets/textures/railroad/switches/w/switch_up_down.png");
    public static final Image SWITCH_EAST_1 = new Image("/assets/textures/railroad/switches/o/switch_straight_up.png");
    public static final Image SWITCH_EAST_2 = new Image("/assets/textures/railroad/switches/o/switch_straight_down.png");
    public static final Image SWITCH_EAST_3 = new Image("/assets/textures/railroad/switches/o/switch_up_down.png");
    public static final Image SWITCH_SOUTH_1 = new Image("/assets/textures/railroad/switches/s/switch_straight_up.png");
    public static final Image SWITCH_SOUTH_2 = new Image("/assets/textures/railroad/switches/s/switch_straight_down.png");
    public static final Image SWITCH_SOUTH_3 = new Image("/assets/textures/railroad/switches/s/switch_up_down.png");
    public static final Image HOVER_N = new Image("/assets/textures/railroad/hovers/hover_north.png");
    public static final Image HOVER_E = new Image("/assets/textures/railroad/hovers/hover_east.png");
    public static final Image HOVER_S = new Image("/assets/textures/railroad/hovers/hover_south.png");
    public static final Image HOVER_W = new Image("/assets/textures/railroad/hovers/hover_west.png");

    public static final Image STRAIGHT_VERTICAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_rail_vertical_hover.png");
    public static final Image STRAIGHT_HORIZONTAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_rail_horizontal_hover.png");
    public static final Image CURVE_NORTH_EAST_HOVER = new Image("/assets/textures/railroad/curves/curve_o_n_hover.png");
    public static final Image CURVE_SOUTH_EAST_HOVER = new Image("/assets/textures/railroad/curves/curve_o_s_hover.png");
    public static final Image CURVE_NORTH_WEST_HOVER = new Image("/assets/textures/railroad/curves/curve_w_n_hover.png");
    public static final Image CURVE_SOUTH_WEST_HOVER = new Image("/assets/textures/railroad/curves/curve_w_s_hover.png");
    public static final Image SWITCH_NORTH_1_HOVER = new Image("/assets/textures/railroad/switches/n/switch_straight_up_hover.png");
    public static final Image SWITCH_NORTH_2_HOVER = new Image("/assets/textures/railroad/switches/n/switch_straight_down_hover.png");
    public static final Image SWITCH_NORTH_3_HOVER = new Image("/assets/textures/railroad/switches/n/switch_up_down_hover.png");
    public static final Image SWITCH_WEST_1_HOVER = new Image("/assets/textures/railroad/switches/w/switch_straight_up_hover.png");
    public static final Image SWITCH_WEST_2_HOVER = new Image("/assets/textures/railroad/switches/w/switch_straight_down_hover.png");
    public static final Image SWITCH_WEST_3_HOVER = new Image("/assets/textures/railroad/switches/w/switch_up_down_hover.png");
    public static final Image SWITCH_EAST_1_HOVER = new Image("/assets/textures/railroad/switches/o/switch_straight_up_hover.png");
    public static final Image SWITCH_EAST_2_HOVER = new Image("/assets/textures/railroad/switches/o/switch_straight_down_hover.png");
    public static final Image SWITCH_EAST_3_HOVER = new Image("/assets/textures/railroad/switches/o/switch_up_down_hover.png");
    public static final Image SWITCH_SOUTH_1_HOVER = new Image("/assets/textures/railroad/switches/s/switch_straight_up_hover.png");
    public static final Image SWITCH_SOUTH_2_HOVER = new Image("/assets/textures/railroad/switches/s/switch_straight_down_hover.png");
    public static final Image SWITCH_SOUTH_3_HOVER = new Image("/assets/textures/railroad/switches/s/switch_up_down_hover.png");

    public static final ArrayList<Image> HOVER_IMAGES = new ArrayList<>();
    static {
        HOVER_IMAGES.addAll(Arrays.asList(HOVER_N, HOVER_E, HOVER_S, HOVER_W, STRAIGHT_VERTICAL_HOVER, STRAIGHT_HORIZONTAL_HOVER,
                CURVE_NORTH_EAST_HOVER, CURVE_NORTH_WEST_HOVER, CURVE_SOUTH_WEST_HOVER, CURVE_SOUTH_EAST_HOVER,
                SWITCH_EAST_1_HOVER, SWITCH_EAST_2_HOVER, SWITCH_EAST_3_HOVER, SWITCH_NORTH_1_HOVER, SWITCH_NORTH_2_HOVER,
                SWITCH_NORTH_3_HOVER, SWITCH_WEST_1_HOVER, SWITCH_WEST_2_HOVER, SWITCH_WEST_3_HOVER, SWITCH_SOUTH_1_HOVER,
                SWITCH_SOUTH_2_HOVER, SWITCH_SOUTH_3_HOVER));
    }

}
