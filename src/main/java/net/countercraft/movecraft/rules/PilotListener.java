package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftType;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.utils.ChatUtils;
import net.countercraft.movecraft.utils.HitBox;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PilotListener implements Listener {
    @EventHandler
    @SuppressWarnings("Deprecation")
    public void onCraftPilot(CraftDetectEvent event) {
        Craft eventCraft = event.getCraft();

        //Screw the rules, I have permissions!
        //(dated memes in comments are best practice, right?)
        //Ignore all of this if the player has permission to bypass the rules
        if (eventCraft.getNotificationPlayer() != null) {
            if (eventCraft.getNotificationPlayer().hasPermission("movecraft.rules.bypass")) {
                return;
            }
        }
        CraftType eventType = eventCraft.getType();
        TypeRules applicableRules = MovecraftShipRules.getInstance().getRulesByType(eventType);
        if (applicableRules == null) {
            return;
        }
        //First, find which direction is 'forward.' This value is initialised to avoid a NPE later.
        CruiseDirection direction = CruiseDirection.NONE;

        //If it's a CruiseOnPilot craft we can fast-track this, otherwise we have to find a cruise sign.
        if(eventType.getCruiseOnPilot()) {
            direction = eventCraft.getCruiseDirection();
        } else {
            for (MovecraftLocation location : eventCraft.getHitBox()) {
                Block block = location.toBukkit(eventCraft.getW()).getBlock();
                if (!block.getType().name().contains("WALL_SIGN")) {
                    continue;
                }
                Sign sign = (Sign) block.getState();
                if(!ChatColor.stripColor(sign.getLine(0)).toLowerCase().startsWith("cruise:")) {
                    continue;
                }
                if(direction == CruiseDirection.NONE) {
                    direction = CruiseDirection.fromRaw(sign.getRawData());
                } else {
                    if(direction != CruiseDirection.fromRaw(sign.getRawData()) && applicableRules.getRequireCruiseSignAlignment()) {
                        eventCraft.getNotificationPlayer().sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Detection failed: All cruise signs must face the same way.");
                    }
                }
            }
        }

        if (direction != CruiseDirection.NONE) {
            if(!checkDimensions(eventCraft.getHitBox(), direction, applicableRules, eventCraft.getNotificationPlayer())) {
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

    private boolean doubleIsInRange (double compare, double lower, double upper) {
        if (compare < lower && lower != -1.0) {
            return false;
        }
        return compare <= upper || upper == -1.0;
    }

    private boolean checkDimensions(HitBox box, CruiseDirection direction, TypeRules rules, Player player) {
        if(box.isEmpty()) {
            return true;
        }
        int length;
        int width;
        int height = box.getMaxY()-box.getMinY() + 1;

        //max-min will be off by one, correct that
        int distanceX = box.getMaxX()-box.getMinX() + 1;
        int distanceZ = box.getMaxZ()-box.getMinZ() + 1;

        //we need to worry about two configurations: east-west and north-south. Ratios will be the same either way
        if(direction == CruiseDirection.EAST || direction == CruiseDirection.WEST) {
            length = distanceX;
            width = distanceZ;
        } else {
            length = distanceZ;
            width = distanceX;
        }

        //check absolute dimensions
        if (!intIsInRange(length, rules.getMinAbsoluteLength(), rules.getMaxAbsoluteLength())) {
            if (rules.getMinAbsoluteLength() == rules.getMaxAbsoluteLength()) {
                player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + String.format("Your craft has an invalid length! For crafts of type %s, length must be %d, but yours is %d long.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteLength(), length));
            } else  {
                player.sendMessage(String.format("Your craft has an invalid length! For crafts of type %s, length must be between %d and %d, but yours is %d long.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteLength(), rules.getMaxAbsoluteLength(), length));
            }

            return false;
        }
        if (!intIsInRange(width, rules.getMinAbsoluteWidth(), rules.getMaxAbsoluteWidth())) {
            if (rules.getMinAbsoluteWidth() == rules.getMaxAbsoluteWidth()) {
                player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid width! For crafts of type %s, width must be %d, but yours is %d wide.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteWidth(), width));
            } else  {
                player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid width! For crafts of type %s, width must be between %d and %d, but yours is %d wide.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteWidth(), rules.getMaxAbsoluteWidth(), width));
            }

            return false;
        }
        if (!intIsInRange(height, rules.getMinAbsoluteHeight(), rules.getMaxAbsoluteHeight())) {
            if (rules.getMinAbsoluteLength() == rules.getMaxAbsoluteLength()) {
                player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid height! For crafts of type %s, height must be %d, but yours is %d tall.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteHeight(), height));
            } else  {
                player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid height! For crafts of type %s, height must be between %d and %d, but yours is %d tall.", rules.getApplicableType().getCraftName(), rules.getMinAbsoluteHeight(), rules.getMaxAbsoluteHeight(), height));
            }

            return false;
        }

        //next, check ratios
        double lwr = ((double)length) / width;
        double lhr = ((double)length) / height;
        double whr = ((double)width) / height;

        if (!doubleIsInRange(lwr, rules.getMinLengthToWidthRatio(), rules.getMaxLengthToWidthRatio())) {
            player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid length-to-width ratio! For crafts of type %s, length:width must be between %s and %s, but the ratio of your craft is %s.", rules.getApplicableType().getCraftName(), rules.getMinLengthToWidthRatio(), rules.getMaxLengthToWidthRatio(), lwr));
            return false;
        }
        if (!doubleIsInRange(lhr, rules.getMinLengthToHeightRatio(), rules.getMaxLengthToHeightRatio())) {
            player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid length-to-height ratio! For crafts of type %s, length:height must be between %s and %s, but the ratio of your craft is %s.", rules.getApplicableType().getCraftName(), rules.getMinLengthToHeightRatio(), rules.getMaxLengthToHeightRatio(), lhr));
            return false;
        }
        if (!doubleIsInRange(whr, rules.getMinWidthToHeightRatio(), rules.getMaxWidthToHeightRatio())) {
            player.sendMessage(String.format(ChatUtils.MOVECRAFT_COMMAND_PREFIX + "Your craft has an invalid width-to-height ratio! For crafts of type %s, width:height must be between %s and %s, but the ratio of your craft is %s.", rules.getApplicableType().getCraftName(), rules.getMinWidthToHeightRatio(), rules.getMaxWidthToHeightRatio(), whr));
            return false;
        }

        return true;
    }


}
