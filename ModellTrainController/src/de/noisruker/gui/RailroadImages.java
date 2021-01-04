package de.noisruker.gui;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;

public class RailroadImages {

    public static final Image EMPTY = new Image("/assets/textures/railroad/empty_plate.png");
    public static final Image EMPTY_2 = new Image("/assets/textures/railroad/empty_plate_2.png");

    public static final Image STRAIGHT_VERTICAL = new Image("/assets/textures/railroad/straight_rails/straight_rail_vertical.png");
    public static final Image STRAIGHT_HORIZONTAL = new Image("/assets/textures/railroad/straight_rails/straight_rail_horizontal.png");
    public static final Image STRAIGHT_SENSOR_VERTICAL = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_vertical.png");
    public static final Image STRAIGHT_SENSOR_HORIZONTAL = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_horizontal.png");
    public static final Image STRAIGHT_NORTH = new Image("/assets/textures/railroad/directional_rails/straight_north.png");
    public static final Image STRAIGHT_SOUTH = new Image("/assets/textures/railroad/directional_rails/straight_south.png");
    public static final Image STRAIGHT_EAST = new Image("/assets/textures/railroad/directional_rails/straight_east.png");
    public static final Image STRAIGHT_WEST = new Image("/assets/textures/railroad/directional_rails/straight_west.png");
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
    public static final Image SWITCH_EAST_1 = new Image("/assets/textures/railroad/switches/e/switch_straight_up.png");
    public static final Image SWITCH_EAST_2 = new Image("/assets/textures/railroad/switches/e/switch_straight_down.png");
    public static final Image SWITCH_EAST_3 = new Image("/assets/textures/railroad/switches/e/switch_up_down.png");
    public static final Image SWITCH_SOUTH_1 = new Image("/assets/textures/railroad/switches/s/switch_straight_up.png");
    public static final Image SWITCH_SOUTH_2 = new Image("/assets/textures/railroad/switches/s/switch_straight_down.png");
    public static final Image SWITCH_SOUTH_3 = new Image("/assets/textures/railroad/switches/s/switch_up_down.png");
    public static final Image END_NORTH = new Image("/assets/textures/railroad/ends/end_north.png");
    public static final Image END_EAST = new Image("/assets/textures/railroad/ends/end_east.png");
    public static final Image END_SOUTH = new Image("/assets/textures/railroad/ends/end_south.png");
    public static final Image END_WEST = new Image("/assets/textures/railroad/ends/end_west.png");

    public static final Image STRAIGHT_VERTICAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_rail_vertical_hover.png");
    public static final Image STRAIGHT_HORIZONTAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_rail_horizontal_hover.png");
    public static final Image STRAIGHT_SENSOR_VERTICAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_vertical_hover.png");
    public static final Image STRAIGHT_SENSOR_HORIZONTAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_horizontal_hover.png");
    public static final Image STRAIGHT_SENSOR_VERTICAL_OFF = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_vertical_off.png");
    public static final Image STRAIGHT_SENSOR_HORIZONTAL_OFF = new Image("/assets/textures/railroad/straight_rails/straight_sensor_rail_horizontal_off.png");
    public static final Image STRAIGHT_NORTH_HOVER = new Image("/assets/textures/railroad/directional_rails/straight_north_hover.png");
    public static final Image STRAIGHT_SOUTH_HOVER = new Image("/assets/textures/railroad/directional_rails/straight_south_hover.png");
    public static final Image STRAIGHT_EAST_HOVER = new Image("/assets/textures/railroad/directional_rails/straight_east_hover.png");
    public static final Image STRAIGHT_WEST_HOVER = new Image("/assets/textures/railroad/directional_rails/straight_west_hover.png");
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
    public static final Image SWITCH_EAST_1_HOVER = new Image("/assets/textures/railroad/switches/e/switch_straight_up_hover.png");
    public static final Image SWITCH_EAST_2_HOVER = new Image("/assets/textures/railroad/switches/e/switch_straight_down_hover.png");
    public static final Image SWITCH_EAST_3_HOVER = new Image("/assets/textures/railroad/switches/e/switch_up_down_hover.png");
    public static final Image SWITCH_SOUTH_1_HOVER = new Image("/assets/textures/railroad/switches/s/switch_straight_up_hover.png");
    public static final Image SWITCH_SOUTH_2_HOVER = new Image("/assets/textures/railroad/switches/s/switch_straight_down_hover.png");
    public static final Image SWITCH_SOUTH_3_HOVER = new Image("/assets/textures/railroad/switches/s/switch_up_down_hover.png");
    public static final Image END_NORTH_HOVER = new Image("/assets/textures/railroad/ends/end_north_hover.png");
    public static final Image END_EAST_HOVER = new Image("/assets/textures/railroad/ends/end_east_hover.png");
    public static final Image END_SOUTH_HOVER = new Image("/assets/textures/railroad/ends/end_south_hover.png");
    public static final Image END_WEST_HOVER = new Image("/assets/textures/railroad/ends/end_west_hover.png");
    public static final Image SIGNAL_VERTICAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_signal_vertical_hover.png");
    public static final Image SIGNAL_HORIZONTAL_HOVER = new Image("/assets/textures/railroad/straight_rails/straight_signal_horizontal_hover.png");

    public static final Image SWITCH_SOUTH_LEFT_ON = new Image("/assets/textures/railroad/switches/s/switch_left_on.png");
    public static final Image SWITCH_SOUTH_LEFT_OFF = new Image("/assets/textures/railroad/switches/s/switch_left_off.png");
    public static final Image SWITCH_SOUTH_RIGHT_ON = new Image("/assets/textures/railroad/switches/s/switch_right_on.png");
    public static final Image SWITCH_SOUTH_RIGHT_OFF = new Image("/assets/textures/railroad/switches/s/switch_right_off.png");
    public static final Image SWITCH_SOUTH_STRAIGHT_LEFT_ON = new Image("/assets/textures/railroad/switches/s/switch_straight_on_left.png");
    public static final Image SWITCH_SOUTH_STRAIGHT_LEFT_OFF = new Image("/assets/textures/railroad/switches/s/switch_straight_off_left.png");
    public static final Image SWITCH_SOUTH_STRAIGHT_RIGHT_ON = new Image("/assets/textures/railroad/switches/s/switch_straight_on_right.png");
    public static final Image SWITCH_SOUTH_STRAIGHT_RIGHT_OFF = new Image("/assets/textures/railroad/switches/s/switch_straight_off_right.png");

    public static final Image SWITCH_NORTH_LEFT_ON = new Image("/assets/textures/railroad/switches/n/switch_left_on.png");
    public static final Image SWITCH_NORTH_LEFT_OFF = new Image("/assets/textures/railroad/switches/n/switch_left_off.png");
    public static final Image SWITCH_NORTH_RIGHT_ON = new Image("/assets/textures/railroad/switches/n/switch_right_on.png");
    public static final Image SWITCH_NORTH_RIGHT_OFF = new Image("/assets/textures/railroad/switches/n/switch_right_off.png");
    public static final Image SWITCH_NORTH_STRAIGHT_LEFT_ON = new Image("/assets/textures/railroad/switches/n/switch_straight_on_left.png");
    public static final Image SWITCH_NORTH_STRAIGHT_LEFT_OFF = new Image("/assets/textures/railroad/switches/n/switch_straight_off_left.png");
    public static final Image SWITCH_NORTH_STRAIGHT_RIGHT_ON = new Image("/assets/textures/railroad/switches/n/switch_straight_on_right.png");
    public static final Image SWITCH_NORTH_STRAIGHT_RIGHT_OFF = new Image("/assets/textures/railroad/switches/n/switch_straight_off_right.png");

    public static final Image SWITCH_EAST_LEFT_ON = new Image("/assets/textures/railroad/switches/e/switch_left_on.png");
    public static final Image SWITCH_EAST_LEFT_OFF = new Image("/assets/textures/railroad/switches/e/switch_left_off.png");
    public static final Image SWITCH_EAST_RIGHT_ON = new Image("/assets/textures/railroad/switches/e/switch_right_on.png");
    public static final Image SWITCH_EAST_RIGHT_OFF = new Image("/assets/textures/railroad/switches/e/switch_right_off.png");
    public static final Image SWITCH_EAST_STRAIGHT_LEFT_ON = new Image("/assets/textures/railroad/switches/e/switch_straight_on_left.png");
    public static final Image SWITCH_EAST_STRAIGHT_LEFT_OFF = new Image("/assets/textures/railroad/switches/e/switch_straight_off_left.png");
    public static final Image SWITCH_EAST_STRAIGHT_RIGHT_ON = new Image("/assets/textures/railroad/switches/e/switch_straight_on_right.png");
    public static final Image SWITCH_EAST_STRAIGHT_RIGHT_OFF = new Image("/assets/textures/railroad/switches/e/switch_straight_off_right.png");

    public static final Image SWITCH_WEST_LEFT_ON = new Image("/assets/textures/railroad/switches/w/switch_left_on.png");
    public static final Image SWITCH_WEST_LEFT_OFF = new Image("/assets/textures/railroad/switches/w/switch_left_off.png");
    public static final Image SWITCH_WEST_RIGHT_ON = new Image("/assets/textures/railroad/switches/w/switch_right_on.png");
    public static final Image SWITCH_WEST_RIGHT_OFF = new Image("/assets/textures/railroad/switches/w/switch_right_off.png");
    public static final Image SWITCH_WEST_STRAIGHT_LEFT_ON = new Image("/assets/textures/railroad/switches/w/switch_straight_on_left.png");
    public static final Image SWITCH_WEST_STRAIGHT_LEFT_OFF = new Image("/assets/textures/railroad/switches/w/switch_straight_off_left.png");
    public static final Image SWITCH_WEST_STRAIGHT_RIGHT_ON = new Image("/assets/textures/railroad/switches/w/switch_straight_on_right.png");
    public static final Image SWITCH_WEST_STRAIGHT_RIGHT_OFF = new Image("/assets/textures/railroad/switches/w/switch_straight_off_right.png");

    public static final Image SIGNAL_VERTICAL = new Image("/assets/textures/railroad/straight_rails/straight_signal_vertical.png");
    public static final Image SIGNAL_VERTICAL_ON = new Image("/assets/textures/railroad/straight_rails/straight_signal_vertical_on.png");
    public static final Image SIGNAL_VERTICAL_OFF = new Image("/assets/textures/railroad/straight_rails/straight_signal_vertical_off.png");
    public static final Image SIGNAL_HORIZONTAL = new Image("/assets/textures/railroad/straight_rails/straight_signal_horizontal.png");
    public static final Image SIGNAL_HORIZONTAL_ON = new Image("/assets/textures/railroad/straight_rails/straight_signal_horizontal_on.png");
    public static final Image SIGNAL_HORIZONTAL_OFF = new Image("/assets/textures/railroad/straight_rails/straight_signal_horizontal_off.png");

    public static final ArrayList<Image> NORTH_IMAGES = new ArrayList<>();
    public static final ArrayList<Image> EAST_IMAGES = new ArrayList<>();
    public static final ArrayList<Image> SOUTH_IMAGES = new ArrayList<>();
    public static final ArrayList<Image> WEST_IMAGES = new ArrayList<>();
    public static final ArrayList<Image> HOVER_IMAGES = new ArrayList<>();
    static {
        HOVER_IMAGES.addAll(Arrays.asList(STRAIGHT_VERTICAL_HOVER, STRAIGHT_HORIZONTAL_HOVER,
                CURVE_NORTH_EAST_HOVER, CURVE_NORTH_WEST_HOVER, CURVE_SOUTH_WEST_HOVER, CURVE_SOUTH_EAST_HOVER,
                SWITCH_EAST_1_HOVER, SWITCH_EAST_2_HOVER, SWITCH_EAST_3_HOVER, SWITCH_NORTH_1_HOVER, SWITCH_NORTH_2_HOVER,
                SWITCH_NORTH_3_HOVER, SWITCH_WEST_1_HOVER, SWITCH_WEST_2_HOVER, SWITCH_WEST_3_HOVER, SWITCH_SOUTH_1_HOVER,
                SWITCH_SOUTH_2_HOVER, SWITCH_SOUTH_3_HOVER, END_NORTH_HOVER, END_EAST_HOVER, END_SOUTH_HOVER, END_WEST_HOVER, STRAIGHT_NORTH_HOVER,
                STRAIGHT_SOUTH_HOVER, STRAIGHT_EAST_HOVER, STRAIGHT_WEST_HOVER, STRAIGHT_SENSOR_VERTICAL_HOVER, STRAIGHT_SENSOR_HORIZONTAL_HOVER,
                SIGNAL_VERTICAL_HOVER, SIGNAL_HORIZONTAL_HOVER));
        NORTH_IMAGES.addAll(Arrays.asList(STRAIGHT_VERTICAL, STRAIGHT_SENSOR_VERTICAL, END_NORTH, STRAIGHT_NORTH, STRAIGHT_SOUTH, CURVE_NORTH_WEST, CURVE_NORTH_EAST,
                SWITCH_NORTH_1, SWITCH_NORTH_2, SWITCH_NORTH_3, SWITCH_SOUTH_1, SWITCH_SOUTH_2, SWITCH_WEST_1, SWITCH_EAST_2, SWITCH_WEST_3, SWITCH_EAST_3,
                SIGNAL_VERTICAL));
        EAST_IMAGES.addAll(Arrays.asList(STRAIGHT_HORIZONTAL, STRAIGHT_SENSOR_HORIZONTAL, END_EAST, STRAIGHT_EAST, STRAIGHT_WEST, CURVE_SOUTH_EAST, CURVE_NORTH_EAST,
                SWITCH_EAST_1, SWITCH_EAST_2, SWITCH_EAST_3, SWITCH_WEST_1, SWITCH_WEST_2, SWITCH_NORTH_1, SWITCH_SOUTH_2, SWITCH_NORTH_3, SWITCH_SOUTH_3,
                SIGNAL_HORIZONTAL));
        SOUTH_IMAGES.addAll(Arrays.asList(STRAIGHT_VERTICAL, STRAIGHT_SENSOR_VERTICAL, END_SOUTH, STRAIGHT_NORTH, STRAIGHT_SOUTH, CURVE_SOUTH_WEST, CURVE_SOUTH_EAST,
                SWITCH_NORTH_1, SWITCH_NORTH_2, SWITCH_SOUTH_3, SWITCH_SOUTH_1, SWITCH_SOUTH_2, SWITCH_WEST_2, SWITCH_EAST_1, SWITCH_WEST_3, SWITCH_EAST_3,
                SIGNAL_VERTICAL));
        WEST_IMAGES.addAll(Arrays.asList(STRAIGHT_HORIZONTAL, STRAIGHT_SENSOR_HORIZONTAL, END_WEST, STRAIGHT_EAST, STRAIGHT_WEST, CURVE_SOUTH_WEST, CURVE_NORTH_WEST,
                SWITCH_EAST_1, SWITCH_EAST_2, SWITCH_WEST_3, SWITCH_WEST_1, SWITCH_WEST_2, SWITCH_NORTH_2, SWITCH_SOUTH_1, SWITCH_NORTH_3, SWITCH_SOUTH_3,
                SIGNAL_HORIZONTAL));
    }

}
