/*
 * Copyright (c) 2022 Alexander Majka (mfnalex), JEFF Media GbR
 * Website: https://www.jeff-media.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.ancash.minecraft.updatechecker;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever an update check is finished.
 */
public class UpdateCheckEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final UpdateChecker instance;
    private final UpdateCheckResult result;
    private final UpdateCheckSuccess success;
    private CommandSender[] requesters = null;

    protected UpdateCheckEvent(UpdateCheckSuccess success, UpdateChecker checker) {
        instance = checker;
        this.success = success;
        if (success == UpdateCheckSuccess.FAIL && instance.getLatestVersion() == null) {
            result = UpdateCheckResult.UNKNOWN;
        } else {
            if (instance.isUsingLatestVersion()) {
                result = UpdateCheckResult.RUNNING_LATEST_VERSION;
            } else {
                result = UpdateCheckResult.NEW_VERSION_AVAILABLE;
            }
        }
    }

    public UpdateChecker getChecker() {
    	return instance;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the latest version string found by the UpdateChecker, or null if all previous checks have failed.
     *
     * @return Latest version string found by the UpdateChecker, or null if all previous checks have failed
     */
    public String getLatestVersion() {
        return instance.getLatestVersion();
    }

    /**
     * Gets an array of all CommandSenders who have requested this update check. Normally this will either be the ConsoleCommandSender or a player.
     *
     * @return Array of all CommandSenders who have requested this update check
     */
    public CommandSender[] getRequesters() {
        if (requesters == null || requesters.length == 0) return null;
        return requesters;
    }

    /**
     * Sets the CommandSenders who requested this update check.
     *
     * @param requesters CommandSenders who requested this update check
     * @return UpdateCheckEvent instance
     */
    protected UpdateCheckEvent setRequesters(CommandSender... requesters) {
        this.requesters = requesters;
        return this;
    }

    /**
     * Gets the result, i.e. whether a new version is available or not.
     *
     * @return UpdateCheckResult of this update check
     */
    public UpdateCheckResult getResult() {
        return result;
    }

    /**
     * Checks whether the update checking attempt was successful or failed.
     *
     * @return UpdateCheckSuccess of this update check
     */
    public UpdateCheckSuccess getSuccess() {
        return success;
    }

    /**
     * Gets the version string of the currently used plugin version.
     *
     * @return Version string of the currently used plugin version
     */
    public String getUsedVersion() {
        return instance.getUsedVersion();
    }

}
