package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.property.BooleanProperty;
import net.countercraft.movecraft.craft.type.property.DoubleProperty;
import net.countercraft.movecraft.craft.type.property.IntegerProperty;

import org.bukkit.NamespacedKey;

public class TypeRules {
    public static final NamespacedKey MAX_LENGTH_TO_WIDTH_RATIO = new NamespacedKey("movecraft-shiprules",
            "max_length_to_width_ratio");
    public static final NamespacedKey MIN_LENGTH_TO_WIDTH_RATIO = new NamespacedKey("movecraft-shiprules",
            "min_length_to_width_ratio");
    public static final NamespacedKey MAX_LENGTH_TO_HEIGHT_RATIO = new NamespacedKey("movecraft-shiprules",
            "max_length_to_height_ratio");
    public static final NamespacedKey MIN_LENGTH_TO_HEIGHT_RATIO = new NamespacedKey("movecraft-shiprules",
            "min_length_to_height_ratio");
    public static final NamespacedKey MAX_WIDTH_TO_HEIGHT_RATIO = new NamespacedKey("movecraft-shiprules",
            "max_width_to_height_ratio");
    public static final NamespacedKey MIN_WIDTH_TO_HEIGHT_RATIO = new NamespacedKey("movecraft-shiprules",
            "min_width_to_height_ratio");

    public static final NamespacedKey MAX_ABSOLUTE_LENGTH = new NamespacedKey("movecraft-shiprules",
            "max_absolute_length");
    public static final NamespacedKey MIN_ABSOLUTE_LENGTH = new NamespacedKey("movecraft-shiprules",
            "min_absolute_length");
    public static final NamespacedKey MAX_ABSOLUTE_WIDTH = new NamespacedKey("movecraft-shiprules",
            "max_absolute_width");
    public static final NamespacedKey MIN_ABSOLUTE_WIDTH = new NamespacedKey("movecraft-shiprules",
            "min_absolute_width");
    public static final NamespacedKey MAX_ABSOLUTE_HEIGHT = new NamespacedKey("movecraft-shiprules",
            "max_absolute_height");
    public static final NamespacedKey MIN_ABSOLUTE_HEIGHT = new NamespacedKey("movecraft-shiprules",
            "min_absolute_height");

    public static final NamespacedKey REQUIRE_CRUISE_SIGN_ALIGNMENT = new NamespacedKey("movecraft-shiprules",
            "require_cruise_sign_alignment");

    public static void register() {
        CraftType.registerProperty(
                new DoubleProperty("maxLengthToWidthRatio", MAX_LENGTH_TO_HEIGHT_RATIO, type -> -1.0));
        CraftType
                .registerProperty(new DoubleProperty("minLengthToWidthRatio", MIN_LENGTH_TO_HEIGHT_RATIO, type -> -1.0));
        CraftType.registerProperty(
                new DoubleProperty("maxLengthToHeightRatio", MAX_LENGTH_TO_WIDTH_RATIO, type -> -1.0));
        CraftType
                .registerProperty(new DoubleProperty("minLengthToHeightRatio", MIN_LENGTH_TO_WIDTH_RATIO, type -> -1.0));
        CraftType.registerProperty(
                new DoubleProperty("maxWidthToHeightRatio", MAX_WIDTH_TO_HEIGHT_RATIO, type -> -1.0));
        CraftType.registerProperty(new DoubleProperty("minWidthToHeightRatio", MIN_WIDTH_TO_HEIGHT_RATIO, type -> -1.0));

        CraftType.registerProperty(new IntegerProperty("maxAbsoluteLength", MAX_ABSOLUTE_LENGTH, type -> -1));
        CraftType.registerProperty(new IntegerProperty("minAbsoluteLength", MIN_ABSOLUTE_LENGTH, type -> -1));
        CraftType.registerProperty(new IntegerProperty("maxAbsoluteWidth", MAX_ABSOLUTE_WIDTH, type -> -1));
        CraftType.registerProperty(new IntegerProperty("minAbsoluteWidth", MIN_ABSOLUTE_WIDTH, type -> -1));
        CraftType.registerProperty(new IntegerProperty("maxAbsoluteHeight", MAX_ABSOLUTE_HEIGHT, type -> -1));
        CraftType.registerProperty(new IntegerProperty("minAbsoluteHeight", MIN_ABSOLUTE_HEIGHT, type -> -1));

        CraftType.registerProperty(
                new BooleanProperty("requireCruiseSignAlignment", REQUIRE_CRUISE_SIGN_ALIGNMENT, type -> false));
    }
}
