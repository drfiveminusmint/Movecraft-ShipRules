package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TypeRules {
    private final CraftType applicableType;
    private final double maxLengthToWidthRatio;
    private final double minLengthToWidthRatio;
    private final double maxLengthToHeightRatio;
    private final double minLengthToHeightRatio;
    private final double maxWidthToHeightRatio;
    private final double minWidthToHeightRatio;
    private final int maxAbsoluteLength;
    private final int minAbsoluteLength;
    private final int maxAbsoluteWidth;
    private final int minAbsoluteWidth;
    private final int maxAbsoluteHeight;
    private final int minAbsoluteHeight;
    private final int maxEngineBlobs;
    private final int minEngineBlobs;
    private final boolean requireCruiseSignAlignment;

    public TypeRules(File f) {
        // This code is cannibalized from the movecraft type code
        final Map<Object, Object> data;
        try {
            InputStream input = new FileInputStream(f);
            Yaml yaml = new Yaml();
            data = yaml.load(input);
            input.close();
        } catch (IOException e) {
            throw new RulesNotFoundException("Error for file '" + f.getAbsolutePath() + "': IOException Encountered.");
        }

        maxLengthToWidthRatio = doubleFromObject(data.getOrDefault("maxLengthToWidthRatio", -1.0));
        minLengthToWidthRatio = doubleFromObject(data.getOrDefault("minLengthToWidthRatio", -1.0));
        maxLengthToHeightRatio = doubleFromObject(data.getOrDefault("maxLengthToHeightRatio", -1.0));
        minLengthToHeightRatio = doubleFromObject(data.getOrDefault("minLengthToHeightRatio", -1.0));
        maxWidthToHeightRatio = doubleFromObject(data.getOrDefault("maxWidthToHeightRatio", -1.0));
        minWidthToHeightRatio = doubleFromObject(data.getOrDefault("minWidthToHeightRatio", -1.0));

        maxAbsoluteHeight = (int) data.getOrDefault("maxAbsoluteHeight", -1);
        minAbsoluteHeight = (int) data.getOrDefault("minAbsoluteHeight", -1);
        maxAbsoluteLength = (int) data.getOrDefault("maxAbsoluteLength", -1);
        minAbsoluteLength = (int) data.getOrDefault("minAbsoluteLength", -1);
        maxAbsoluteWidth = (int) data.getOrDefault("maxAbsoluteWidth", -1);
        minAbsoluteWidth = (int) data.getOrDefault("minAbsoluteWidth", -1);

        minEngineBlobs = (int) data.getOrDefault("minEngineBlobs", -1);
        maxEngineBlobs = (int) data.getOrDefault("minEngineBlobs", -1);

        requireCruiseSignAlignment = (boolean) data.getOrDefault("requireCruiseSignAlignment", false);

        try {
            this.applicableType = CraftManager.getInstance()
                    .getCraftTypeFromString((String) data.get("applicableType"));
        } catch (TypeNotPresentException e) {
            throw new RulesNotFoundException(
                    "Could not parse type name '" + data.get("applicableType") + "' for file " + f.getName());
        }
    }

    private double doubleFromObject(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        }
        return (Double) obj;
    }

    public CraftType getApplicableType() {
        return applicableType;
    }

    public double getMaxLengthToWidthRatio() {
        return maxLengthToWidthRatio;
    }

    public double getMinLengthToWidthRatio() {
        return minLengthToWidthRatio;
    }

    public double getMaxLengthToHeightRatio() {
        return maxLengthToHeightRatio;
    }

    public double getMinLengthToHeightRatio() {
        return minLengthToHeightRatio;
    }

    public double getMaxWidthToHeightRatio() {
        return maxWidthToHeightRatio;
    }

    public double getMinWidthToHeightRatio() {
        return minWidthToHeightRatio;
    }

    public int getMaxAbsoluteLength() {
        return maxAbsoluteLength;
    }

    public int getMinAbsoluteLength() {
        return minAbsoluteLength;
    }

    public int getMaxAbsoluteWidth() {
        return maxAbsoluteWidth;
    }

    public int getMinAbsoluteWidth() {
        return minAbsoluteWidth;
    }

    public int getMaxAbsoluteHeight() {
        return maxAbsoluteHeight;
    }

    public int getMinAbsoluteHeight() {
        return minAbsoluteHeight;
    }

    public int getMaxEngineBlobs() {
        return maxEngineBlobs;
    }

    public int getMinEngineBlobs() {
        return minEngineBlobs;
    }

    public boolean getRequireCruiseSignAlignment() {
        return requireCruiseSignAlignment;
    }

    public static class RulesNotFoundException extends RuntimeException {
        public RulesNotFoundException(String s) {
            super(s);
        }
    }
}
