package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.util.ChatUtils;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PilotListener implements Listener {
    @EventHandler
    public void onCraftPilot(CraftDetectEvent event) {
        Craft craft = event.getCraft();

        // Ignore all of this if the player has permission to bypass the rules
        if (craft instanceof PilotedCraft
                && ((PilotedCraft) craft).getPilot().hasPermission("movecraft.rules.bypass"))
            return;
        if (craft instanceof SubCraft) {
            Craft parent = ((SubCraft) craft).getParent();
            if (parent instanceof PilotedCraft
                    && ((PilotedCraft) parent).getPilot().hasPermission("movecraft.rules.bypass"))
                return;
        }

        CraftType type = craft.getType();

        // First, find which direction is 'forward.' This value is initialized to avoid
        // a NPE later.
        CruiseDirection direction = CruiseDirection.NONE;

        // If it's a CruiseOnPilot craft we can fast-track this, otherwise we have to
        // find a cruise sign.
        if (type.getBoolProperty(CraftType.CRUISE_ON_PILOT)) {
            direction = craft.getCruiseDirection();
            if (direction == null) {
                // Movecraft hasn't yet set the direction based on the pilot, let's do that
                // ourselves.
                BlockState state = event.getStartLocation().toBukkit(craft.getWorld()).getBlock().getState();
                if ((state instanceof Sign)) {
                    Sign sign = (Sign) state;
                    if (sign.getBlockData() instanceof Directional) {
                        direction = CruiseDirection.fromBlockFace(((Directional) sign.getBlockData()).getFacing());
                    } else {
                        direction = CruiseDirection.NONE;
                    }
                }
            }
        } else {
            boolean requireCruiseSignAlignment = type.getBoolProperty(TypeRules.REQUIRE_CRUISE_SIGN_ALIGNMENT);
            for (MovecraftLocation location : craft.getHitBox()) {
                Block block = location.toBukkit(craft.getWorld()).getBlock();
                if (!Tag.WALL_SIGNS.isTagged(block.getType()))
                    continue;

                BlockState state = block.getState();
                if (!(state instanceof Sign))
                    continue;

                Sign sign = (Sign) state;
                String line = ChatColor.stripColor(sign.getLine(0));
                if (!line.equalsIgnoreCase("Cruise: OFF") && !line.equalsIgnoreCase("Cruise: ON"))
                    continue;

                BlockData data = sign.getBlockData();
                if (!(data instanceof Directional))
                    continue;

                CruiseDirection currentDirection = CruiseDirection.fromBlockFace(((Directional) data).getFacing());
                if (direction == CruiseDirection.NONE) {
                    direction = currentDirection;
                } else if (direction != currentDirection && requireCruiseSignAlignment) {
                    // TODO: Remove cast when maven cache is cleared
                    ((Audience) craft.getAudience()).sendMessage(Component.text(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                            + "Detection failed: All cruise signs must face the same way."));
                    event.setCancelled(true);
                }
            }
        }

        if (direction != CruiseDirection.NONE) {
            // TODO: Remove cast when maven cache is cleared
            if (!checkDimensions(craft.getHitBox(), direction, type, (Audience) craft.getAudience())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private boolean intIsInRange(int compare, int lower, int upper) {
        if (compare < lower && lower != -1) {
            return false;
        }
        return compare <= upper || upper == -1;
    }

    private boolean doubleIsInRange(double compare, double lower, double upper) {
        if (compare < lower && lower != -1.0) {
            return false;
        }
        return compare <= upper || upper == -1.0;
    }

    private boolean checkDimensions(HitBox box, CruiseDirection direction, CraftType type, Audience audience) {
        if (box.isEmpty())
            return true;

        int length;
        int width;
        int height = box.getMaxY() - box.getMinY() + 1;
        int distanceX = box.getMaxX() - box.getMinX() + 1;
        int distanceZ = box.getMaxZ() - box.getMinZ() + 1;

        // we need to worry about two configurations: east-west and north-south. Ratios
        // will be the same either way
        if (direction == CruiseDirection.EAST || direction == CruiseDirection.WEST) {
            length = distanceX;
            width = distanceZ;
        } else {
            length = distanceZ;
            width = distanceX;
        }

        String craftTypeName = type.getStringProperty(CraftType.NAME);

        // check absolute dimensions
        int min = type.getIntProperty(TypeRules.MIN_ABSOLUTE_LENGTH);
        int max = type.getIntProperty(TypeRules.MAX_ABSOLUTE_LENGTH);
        if (!intIsInRange(length, min, max)) {
            if (min == max) {
                audience.sendMessage(Component.text(ChatUtils.MOVECRAFT_COMMAND_PREFIX + String.format(
                        "Your craft has an invalid length! For crafts of type %s, length must be %d, but yours is %d long.",
                        craftTypeName, min, length)));
            } else {
                audience.sendMessage(Component.text(String.format(
                        "Your craft has an invalid length! For crafts of type %s, length must be between %d and %d, but yours is %d long.",
                        craftTypeName, min, max, length)));
            }
            return false;
        }
        min = type.getIntProperty(TypeRules.MIN_ABSOLUTE_WIDTH);
        max = type.getIntProperty(TypeRules.MAX_ABSOLUTE_WIDTH);
        if (!intIsInRange(width, min, max)) {
            if (min == max) {
                audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                        + "Your craft has an invalid width! For crafts of type %s, width must be %d, but yours is %d wide.",
                        craftTypeName, min, width)));
            } else {
                audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                        + "Your craft has an invalid width! For crafts of type %s, width must be between %d and %d, but yours is %d wide.",
                        craftTypeName, min, max, width)));
            }
            return false;
        }
        min = type.getIntProperty(TypeRules.MIN_ABSOLUTE_HEIGHT);
        max = type.getIntProperty(TypeRules.MAX_ABSOLUTE_HEIGHT);
        if (!intIsInRange(height, min, max)) {
            if (min == max) {
                audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                        + "Your craft has an invalid height! For crafts of type %s, height must be %d, but yours is %d tall.",
                        craftTypeName, min, height)));
            } else {
                audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                        + "Your craft has an invalid height! For crafts of type %s, height must be between %d and %d, but yours is %d tall.",
                        craftTypeName, min, max, height)));
            }
            return false;
        }

        // next, check ratios
        double lwr = ((double) length) / width;
        double lhr = ((double) length) / height;
        double whr = ((double) width) / height;

        double low = type.getDoubleProperty(TypeRules.MIN_LENGTH_TO_WIDTH_RATIO);
        double high = type.getDoubleProperty(TypeRules.MAX_LENGTH_TO_WIDTH_RATIO);
        if (!doubleIsInRange(lwr, low, high)) {
            audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                    + "Your craft has an invalid length-to-width ratio! For crafts of type %s, length:width must be between %.1f and %.1f, but the ratio of your craft is %.1f.",
                    craftTypeName, low, high, lwr)));
            return false;
        }
        low = type.getDoubleProperty(TypeRules.MIN_LENGTH_TO_HEIGHT_RATIO);
        high = type.getDoubleProperty(TypeRules.MAX_LENGTH_TO_HEIGHT_RATIO);
        if (!doubleIsInRange(lhr, low, high)) {
            audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                    + "Your craft has an invalid length-to-height ratio! For crafts of type %s, length:height must be between %.1f and %.1f, but the ratio of your craft is %.1f.",
                    craftTypeName, low, high, lhr)));
            return false;
        }
        low = type.getDoubleProperty(TypeRules.MIN_WIDTH_TO_HEIGHT_RATIO);
        high = type.getDoubleProperty(TypeRules.MAX_WIDTH_TO_HEIGHT_RATIO);
        if (!doubleIsInRange(whr, low, high)) {
            audience.sendMessage(Component.text(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX
                    + "Your craft has an invalid width-to-height ratio! For crafts of type %s, width:height must be between %.1f and %.1f, but the ratio of your craft is %.1f.",
                    craftTypeName, low, high, whr)));
            return false;
        }
        return true;
    }
}
