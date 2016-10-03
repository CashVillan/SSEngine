package com.swingsword.ssengine.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

@SuppressWarnings("deprecation")
public class SchematicUtils {

	public static Vector getMin(Location l1, Location l2) {
		return new Vector(
				Math.min(l1.getBlockX(), l2.getBlockX()), 
				Math.min(l1.getBlockY(), l2.getBlockY()),
				Math.min(l1.getBlockZ(), l2.getBlockZ()));
	}

	public static Vector getMax(Location l1, Location l2) {
		return new Vector(
				Math.max(l1.getBlockX(), l2.getBlockX()), 
				Math.max(l1.getBlockY(), l2.getBlockY()),
				Math.max(l1.getBlockZ(), l2.getBlockZ()));
	}
	
	public static void save(Vector min, Vector max, File schematicFile) {
		try {

			/*
			 * WorldEditPlugin wep = (WorldEditPlugin)
			 * Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			 * WorldEdit we = wep.getWorldEdit();
			 * 
			 * LocalPlayer localPlayer = wep.wrapPlayer(player); LocalSession
			 * localSession = we.getSession(localPlayer); ClipboardHolder
			 * selection = localSession.getClipboard(); EditSession editSession
			 * = localSession.createEditSession(localPlayer);
			 * 
			 * Vector min = selection.getClipboard().getMinimumPoint(); Vector
			 * max = selection.getClipboard().getMaximumPoint();
			 */

			// editSession.enableQueue();
			CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
			// clipboard.copy(editSession);
			SchematicFormat.MCEDIT.save(clipboard, schematicFile);
			// editSession.flushQueue();
		} catch (IOException | DataException ex) {
			ex.printStackTrace();
		}
	}

	public static void paste(File schematicFile, Location pasteLoc) {
		try {
			EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
			editSession.enableQueue();

			SchematicFormat schematic = SchematicFormat.getFormat(schematicFile);
			CuboidClipboard clipboard = schematic.load(schematicFile);

			clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), true);
			editSession.flushQueue();
		} catch (DataException | IOException ex) {
			ex.printStackTrace();
		} catch (MaxChangedBlocksException ex) {
			ex.printStackTrace();
		}
	}
}
