package com.swingsword.ssengine.listeners;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.achievements.AchievementManager;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.command.FriendCommand;
import com.swingsword.ssengine.command.InvCommand;
import com.swingsword.ssengine.command.MessageCommand;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.games.hub.utils.PlayerUtils;
import com.swingsword.ssengine.inventory.Item;
import com.swingsword.ssengine.inventory.ItemManager;
import com.swingsword.ssengine.inventory.PlayerInventory;
import com.swingsword.ssengine.options.OptionHandler;
import com.swingsword.ssengine.options.OptionInventory;
import com.swingsword.ssengine.party.PartyManager;
import com.swingsword.ssengine.player.AccountCreationManager;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerProfileSettings;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.AnvilUtils;
import com.swingsword.ssengine.utils.BlockUtils;
import com.swingsword.ssengine.utils.ExpUtils;
import com.swingsword.ssengine.utils.FriendUtils;
import com.swingsword.ssengine.utils.InboxUtils;
import com.swingsword.ssengine.utils.InventoryUtils;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.Notification;
import com.swingsword.ssengine.utils.PartyUtils;
import com.swingsword.ssengine.utils.ProfileUtils;
import com.swingsword.ssengine.utils.SettingsUtils;
import com.swingsword.ssengine.utils.StatsUtils;

public class InventoryListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void inventoryClick(final InventoryClickEvent event) {
		final Player p = (Player) event.getWhoClicked();
		if (PlayerSessionManager.playerSession.get(p.getName()) != null && PlayerSessionManager.getSession(p).created) {
			if (Bukkit.getPluginManager().getPlugin("SSHub") != null) {
				if (event.getInventory().getType() != InventoryType.CREATIVE && !p.isOp()) {
					event.setCancelled(true);
				}
			}

			if (event.getRawSlot() == 4 && event.getCurrentItem().getType() == Material.SKULL_ITEM) {
				String target = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[2].replace("'s", "");

				if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equals(LanguageUtils.translate(p, "Return to your Profile"))) {
					ProfileUtils.openProfileMenu(p);
				} else {
					p.closeInventory();
					p.chat("/search " + target);
				}
			}

			if (event.getInventory() != null && event.getInventory().getSize() > 9 && event.getInventory().getItem(13) != null && event.getInventory().getItem(13).getType() == Material.REDSTONE_COMPARATOR) {
				event.setCancelled(true);
				return;
			}

			// Join inv

			if (p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getTitle().contains(LanguageUtils.translate(p, "Join"))) {
				event.setCancelled(true);

				if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
					String targetServer = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

					if (event.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
						p.closeInventory();
						p.sendMessage(ChatColor.GREEN + "Joining " + targetServer + "...");
						Channel.sendToServer(p, targetServer);
					} else {
						p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + LanguageUtils.translate(p, "Can't join that server right now") + ".");
					}
				}
			}

			// Private profile
			if (p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getTitle().contains(LanguageUtils.translate(p, "Your Profile")) && !p.getOpenInventory().getTopInventory().getTitle().contains("Edit")) {
				event.setCancelled(true);

				if (event.getRawSlot() == 10) {
					StatsUtils.openStatsInventory(p, p.getName());

				} else if (event.getRawSlot() == 12) {
					new PlayerInventory(p.getName()).openInventory(p, 1);

					return;

				} else if (event.getRawSlot() == 13) {
					ProfileUtils.openProfileSettingsMenu(p);

					return;

				} else if (event.getRawSlot() == 14) {
					FriendUtils.openFriendInventory(p, 0);
					event.setCancelled(true);
					return;
				} else if (event.getRawSlot() == 15) {
					p.openInventory(InboxUtils.getInboxInv(p, 0));

					return;

				} else if (event.getRawSlot() == 16) {
					p.openInventory(BlockUtils.getBlockInv(p, 0));

					return;
				}
			}

			// Public profile
			if (p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getTitle().contains(LanguageUtils.translate(p, "Profile")) && !p.getOpenInventory().getTopInventory().getTitle().endsWith(ChatColor.RESET + "") && !p.getOpenInventory().getTopInventory().getTitle().contains("Edit")) {
				final String target = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle().replace("'s " + LanguageUtils.translate(p, "Profile"), ""));
				event.setCancelled(true);

				if (event.getRawSlot() == 22) {
					if (event.getCurrentItem().getType() == Material.BOOK) {
						final Inventory inv = Bukkit.createInventory(null, 54, target + "'s " + LanguageUtils.translate(p, "Friends"));

						ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + "Return to " + target + "'s Profile", null);
						SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
						meta.setOwner(target);
						playerHead.setItemMeta(meta);
						inv.setItem(4, playerHead);

						p.openInventory(inv);

						Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								PlayerAccount account = new PlayerAccount(SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));

								ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + "Return to " + target + "'s Profile", null);
								SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
								meta.setOwner(target);
								playerHead.setItemMeta(meta);
								inv.setItem(4, playerHead);

								for (UUID all : account.getFriends().keySet()) {
									if (account.getFriends().get(all) == 1) {
										PlayerAccount friendAcc = new PlayerAccount(SQLManager.getSQL("global").getValues(all));

										ItemStack friendHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + (String) friendAcc.getCache().get("name"), null);
										SkullMeta friendmeta = (SkullMeta) playerHead.getItemMeta();
										friendmeta.setOwner((String) friendAcc.getCache().get("name"));
										playerHead.setItemMeta(friendmeta);
										inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), friendHead);
									}
								}

								if (InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
									inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, 0, ChatColor.WHITE + "No friends.", null));
								}
							}
						});
					}

				} else if (event.getRawSlot() == 11) {
					StatsUtils.openStatsInventory(p, target);

				} else if (event.getRawSlot() == 12) {
					InvCommand.openInv(p, target);

				} else if (event.getRawSlot() == 13) {
					if (event.getAction().name().contains("ALL")) {
						if (ItemUtils.loreContains(event.getCurrentItem(), " " + LanguageUtils.translate(p, "invite"))) {
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									PlayerAccount account = new PlayerAccount(SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));

									if (PlayerProfileSettings.isAllowed(p, account, "2")) {
										if (!Bukkit.getOfflinePlayer(target).isOnline()) {
											MasterPlugin.getMasterPlugin().channel.sendRequest(p.getName(), target, true);
											Notification.sendNotification(p, ChatColor.GREEN + "Invited " + ChatColor.YELLOW + target);
											Notification.sendChatNotification(p, "Session", "Sent invite to " + target + ".");
										} else {
											p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Already connected to the same server.");
										}
									} else {
										p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't invite that player to a game.");
									}
								}
							});
						}

					} else if (event.getAction().name().contains("HALF")) {
						if (ItemUtils.loreContains(event.getCurrentItem(), " join")) {
							OptionInventory oinv = new OptionInventory("Join " + target + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
								public void run() {
									MasterPlugin.getMasterPlugin().channel.sendRequest(p.getName(), target, false);
									Notification.sendNotification(p, ChatColor.GREEN + "Requested to join " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s server");
									Notification.sendChatNotification(p, "Session", "Sent request to " + target + ".");
									p.closeInventory();
								}
							}, new Runnable() {
								public void run() {
									Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
										public void run() {
											InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));
										}
									});
								}
							});
							OptionHandler.loadOptionInventory(p, oinv);
							p.openInventory(oinv.inventory);
						}
					}
					return;
				}

				if (event.getRawSlot() == 14) {
					AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
						@Override
						public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
							if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

								if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
									event.setWillClose(true);
									event.setWillDestroy(true);
								} else {
									event.setWillClose(true);
									event.setWillDestroy(true);

									String message = event.getName();

									MessageCommand.sendMessage(p, target, message);
								}

							} else {
								event.setWillClose(false);
								event.setWillDestroy(false);
							}
						}
					});
					gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter message"));
					gui.open();
					p.setLevel(0);
					p.setExp(0);

					return;
				}

				if (event.getRawSlot() == 15) {
					if (event.getAction().name().contains("ALL")) {
						if (ItemUtils.loreContains(event.getCurrentItem(), " unfriend")) {
							final OptionInventory oinv = new OptionInventory("Unfriend " + target + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
								public void run() {
									p.chat("/friend remove " + target);
									p.chat("/search " + target);
									
									//p.openInventory(InventoryUtils.getEmptyPlayerInventory(p, target));
								}
							}, new Runnable() {
								public void run() {
									p.chat("/search " + target);
									//p.openInventory(InventoryUtils.getEmptyPlayerInventory(p, target));
								}
							});
							OptionHandler.loadOptionInventory(p, oinv);
							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									p.openInventory(oinv.inventory);
								}
							});

						} else if (ItemUtils.loreContains(event.getCurrentItem(), " friend")) {
							final OptionInventory oinv = new OptionInventory("Friend " + target + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
								public void run() {
									p.chat("/friend add " + target);

									p.openInventory(InventoryUtils.getEmptyPlayerInventory(p, target));
								}
							}, new Runnable() {
								public void run() {
									p.openInventory(InventoryUtils.getEmptyPlayerInventory(p, target));
								}
							});
							OptionHandler.loadOptionInventory(p, oinv);
							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									p.openInventory(oinv.inventory);
								}
							});
						}

					} else {
						if (ItemUtils.loreContains(event.getCurrentItem(), " unblock")) {
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									UUID uuid = SQLManager.getSQL("global").getUUID(target);

									BlockUtils.removeBlock(p, target, uuid);
								}
							});

						} else if (ItemUtils.loreContains(event.getCurrentItem(), " block")) {
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									UUID uuid = SQLManager.getSQL("global").getUUID(target);

									BlockUtils.addBlock(p, target, uuid, null);
								}
							});
						}
					}
				}

				return;
			}
			
			// Edit Settings
			if (p.getOpenInventory().getTopInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).startsWith(LanguageUtils.translate(p, "Settings"))) {
				event.setCancelled(true);

				if (event.getRawSlot() == 9) {
					Inventory inv = Bukkit.createInventory(null, 27, LanguageUtils.translate(p, "Settings") + " - " + LanguageUtils.translate(p, "Privacy"));

					inv.setItem(4, ItemUtils.createItem(Material.BED, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(p, "Back"), null));

					for (int x = 1; x <= 3; x++) {
						int visibility = PlayerSessionManager.getSession(p).getAccount().profileSettings.getVisibility(x + "");

						inv.setItem(x + 11, ItemUtils.createItem(Material.getMaterial(PlayerProfileSettings.getBlockId(visibility)), 1, (byte) 0, PlayerProfileSettings.getColor(visibility) + PlayerProfileSettings.getName(x), Arrays.asList(PlayerProfileSettings.getLore(visibility))));
					}

					p.closeInventory();
					p.openInventory(inv);

				} else if (event.getRawSlot() == 13) {
					PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("sex", PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("sex").equals("m") ? "f" : "m");
					ProfileUtils.openProfileSettingsMenu(p);
				} else if (event.getRawSlot() == 12) {
					PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("lang", AccountCreationManager.nextLanguage(PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("lang")));
					ProfileUtils.openProfileSettingsMenu(p);
					com.swingsword.ssengine.utils.PlayerUtils.sendTab(p);
					
					if (GameManager.currentGame.gamePlugin.getName().contains("Hub")) {
						PlayerUtils.loadPlayerInventory(p);
					}
				} else if (event.getRawSlot() == 17) {
					p.closeInventory();
					p.openInventory(SettingsUtils.getSettingsInventory(p));
				}
			}

			// Privacy settings
			if (p.getOpenInventory().getTopInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).startsWith(LanguageUtils.translate(p, "Settings" + " - " + LanguageUtils.translate(p, "Privacy")))) {
				event.setCancelled(true);

				if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getClickedInventory() == p.getOpenInventory().getTopInventory()) {
					if (event.getCurrentItem().getType() == Material.BED) {
						ProfileUtils.openProfileSettingsMenu(p);
					} else {
						ProfileUtils.openEditProfileSettingMenu(p, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
					}
				}

				return;
			}

			// Set profile visibility

			if (p.getOpenInventory().getTopInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).startsWith("Set ")) {
				event.setCancelled(true);

				int optionId = PlayerProfileSettings.getId(ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).split(" ")[1]);
				ChatColor color = ChatColor.getByChar(event.getCurrentItem().getItemMeta().getDisplayName().toCharArray()[1]);

				if (color == ChatColor.RED) {
					PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting(optionId + "", 0 + "");
				} else if (color == ChatColor.YELLOW) {
					PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting(optionId + "", 1 + "");
				} else {
					PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting(optionId + "", 2 + "");
				}

				ProfileUtils.openProfileSettingsMenu(p);

				return;
			}

			// Stats Inventory

			if (p.getOpenInventory().getTopInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).contains(" Stats")) {
				String target = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getItem(4).getItemMeta().getDisplayName().split(" ")[2].replace("'s", ""));
				if (target.equals("your")) {
					target = p.getName();
				}

				event.setCancelled(true);

				if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
					final String gamemode = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

					if (AchievementManager.getAchievementsWithPrefix(StatsUtils.getPrefix(gamemode)).size() > 0) {
						p.closeInventory();

						StatsUtils.openGamemodeInventory(p, gamemode, target);
					}
				}

				return;
			}

			// Gamemode Achievements Inventory

			if (p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getSize() > 9 && p.getOpenInventory().getTopInventory().getItem(18) != null && p.getOpenInventory().getTopInventory().getItem(18).getTypeId() == 374) {
				String target = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getItem(4).getItemMeta().getDisplayName());
				try {
					target = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getItem(4).getItemMeta().getDisplayName().split(" ")[2].replace("'s", ""));
					if (target.equals("your")) {
						target = p.getName();
					}
				} catch (Exception e) {
				}
				if (target != null && target.equals("Go back")) {
					target = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getTitle()).split(" - ")[1];
				}

				final String finalTarget = target;

				event.setCancelled(true);

				if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BED) {
					p.closeInventory();

					StatsUtils.openStatsInventory(p, finalTarget);
				}

				return;
			}

			// Main items
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
				if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains(LanguageUtils.translate(p, "Friends List"))) {
					FriendUtils.openFriendInventory(p, 0);
					event.setCancelled(true);
					return;
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.AQUA + LanguageUtils.translate(p, "Settings"))) {
					p.openInventory(SettingsUtils.getSettingsInventory(p));
					event.setCancelled(true);
					return;
				} else if (!p.getOpenInventory().getTopInventory().getTitle().contains(LanguageUtils.translate(p, "Your Profile")) && event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.AQUA + LanguageUtils.translate(p, "Your Profile"))) {
					ProfileUtils.openProfileMenu(p);
					event.setCancelled(true);
					return;
					// SETTINGS
				} else if (event.getClickedInventory().getTitle().toLowerCase().contains("settings - global settings")) {
					event.setCancelled(true);
					if (event.getCurrentItem().getType() == Material.BED) {
						ProfileUtils.openProfileSettingsMenu(p);
						return;
					}
					int setting = 0;
					if (event.getRawSlot() == 11 || event.getRawSlot() == 20) {
						setting = 1;
					}
					if (event.getRawSlot() == 13 || event.getRawSlot() == 22) {
						setting = 2;
					}
					if (event.getRawSlot() == 15 || event.getRawSlot() == 24) {
						setting = 3;
					}
					if (event.getRawSlot() == 17 || event.getRawSlot() == 26) {
						setting = 4;
					}
					if (event.getRawSlot() == 40 || event.getRawSlot() == 49) {
						setting = 5;
					}

					PlayerSessionManager.getSession(p).getAccount().settings.settings[setting] = !PlayerSessionManager.getSession(p).getAccount().settings.settings[setting];
					SettingsUtils.applySettings(p);
					p.openInventory(SettingsUtils.getSettingsInventory(p));

					// Friends
				} else if (event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Friends")) || event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Requests"))) {
					event.setCancelled(true);

					if (event.getRawSlot() >= 18) {
						if (event.getCurrentItem().getType() != Material.PAPER) {
							boolean pending = false;

							for (String lore : event.getCurrentItem().getItemMeta().getLore()) {
								if (lore.contains(LanguageUtils.translate(p, "Request"))) {
									pending = true;
								}
							}

							if (pending && event.getCurrentItem().getItemMeta().getLore().size() == 4) {
								if (event.getAction().name().contains("ALL")) {
									p.chat("/friend add " + ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

								} else if (event.getAction().name().contains("HALF")) {
									p.chat("/friend remove " + ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
								}

							} else if (pending) {
								p.chat("/friend remove " + ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

							} else {
								Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))));
									}
								});
							}
						}

					} else {
						if (ItemUtils.getDisplayName(event.getCurrentItem()).contains(LanguageUtils.translate(p, "Friends"))) {
							FriendUtils.openFriendInventory(p, 0);

						} else if (ItemUtils.getDisplayName(event.getCurrentItem()).contains("Add ")) {
							AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
								@Override
								public void onAnvilClick(AnvilUtils.AnvilClickEvent event) {
									if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

										if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
											event.setWillClose(true);
											event.setWillDestroy(true);
										} else {
											event.setWillClose(false);
											event.setWillDestroy(false);

											FriendCommand.addFriend(p, event.getName(), event.getClickedItem());
										}

									} else {
										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								}
							});

							gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter username"));
							gui.open();
							p.setLevel(0);
							p.setExp(0);

						} else if (ItemUtils.getDisplayName(event.getCurrentItem()).contains(LanguageUtils.translate(p, "Requests"))) {
							FriendUtils.openFriendInventory(p, 1);

						} else if (ItemUtils.getDisplayName(event.getCurrentItem()).contains(LanguageUtils.translate(p, "Delete"))) {
							FriendUtils.openFriendInventory(p, 2);
						}
					}

					// Friends display

				} else if (event.getClickedInventory().getTitle().toLowerCase().contains("'s friends")) {
					event.setCancelled(true);
					return;

					// Blocking

				} else if (event.getClickedInventory().getTitle().toLowerCase().contains("blocked")) {
					event.setCancelled(true);

					if (event.getRawSlot() == 2) {
						AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
							@Override
							public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
								if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

									if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										event.setWillClose(false);
										event.setWillDestroy(false);

										Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
											public void run() {
												final UUID uuid = SQLManager.getSQL("global").getUUID(event.getName());

												Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
													public void run() {

														if (uuid != null) {
															if (!PlayerSessionManager.getSession(p).getAccount().getBlocks().contains(uuid)) {
																BlockUtils.confirmBlock(p, event.getName(), uuid, event.getClickedItem());

															} else {
																String message = ChatColor.RED + "You already blocked " + ChatColor.YELLOW + event.getName();

																ItemMeta meta = event.getClickedItem().getItemMeta();
																meta.setDisplayName(message);
																event.getClickedItem().setItemMeta(meta);
															}
														} else {
															String message = ChatColor.RED + "0 results for " + ChatColor.YELLOW + event.getName();

															ItemMeta meta = event.getClickedItem().getItemMeta();
															meta.setDisplayName(message);
															event.getClickedItem().setItemMeta(meta);
														}
													}
												});
											}
										});
									}

								} else {
									event.setWillClose(false);
									event.setWillDestroy(true);

									p.openInventory(BlockUtils.getBlockInv(p, 0));
								}
							}
						});
						gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter username"));
						gui.open();
						p.setLevel(0);
						p.setExp(0);

					} else if (event.getRawSlot() == 0) {
						p.openInventory(BlockUtils.getBlockInv(p, 0));

					} else if (event.getRawSlot() == 8) {
						p.openInventory(BlockUtils.getBlockInv(p, 1));

					} else if (event.getRawSlot() == 6) {
						if (PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("vb").equals("1")) {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("vb", "0");
						} else {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("vb", "1");
						}

						p.openInventory(BlockUtils.getBlockInv(p, 0));

					} else {
						final String target = ChatColor.stripColor(ItemUtils.getDisplayName(event.getCurrentItem()));

						if (event.getAction().name().contains("ALL") || event.getClickedInventory().getTitle().toLowerCase().contains("you")) {
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));
								}
							});

						} else if (event.getAction().name().contains("HALF")) {
							p.closeInventory();

							BlockUtils.removeBlock(p, target, SQLManager.getSQL("global").getUUID(target));
							p.openInventory(BlockUtils.getBlockInv(p, 0));
						}
					}

					// Inbox

				} else if (event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Inbox")) || event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Message"))) {
					event.setCancelled(true);

					if (event.getRawSlot() == 2) {
						AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
							@Override
							public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
								if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

									if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										event.setWillClose(false);
										event.setWillDestroy(false);

										Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
											public void run() {
												final UUID uuid = SQLManager.getSQL("global").getUUID(event.getName());

												Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
													public void run() {

														if (uuid != null) {
															final String target = event.getName();

															AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
																@Override
																public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
																	if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

																		if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
																			event.setWillClose(false);
																			event.setWillDestroy(false);
																		} else {
																			event.setWillClose(true);
																			event.setWillDestroy(true);

																			String message = event.getName();

																			MessageCommand.sendMessage(p, target, message);
																		}

																	} else {
																		event.setWillClose(false);
																		event.setWillDestroy(false);
																	}
																}
															});
															gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.createItem(Material.getMaterial(421), 1, (byte) 0, LanguageUtils.translate(p, "Enter message"), null));
															gui.open();
															p.setLevel(0);
															p.setExp(0);

														} else {
															String message = ChatColor.RED + "0 results for " + ChatColor.YELLOW + event.getName();

															ItemMeta meta = event.getClickedItem().getItemMeta();
															meta.setDisplayName(message);
															event.getClickedItem().setItemMeta(meta);
														}
													}
												});
											}
										});
									}

								} else {
									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						});
						gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.createItem(Material.getMaterial(421), 1, (byte) 0, LanguageUtils.translate(p, "Enter username"), null));
						gui.open();
						p.setLevel(0);
						p.setExp(0);

					} else if (event.getRawSlot() == 0) {
						p.openInventory(InboxUtils.getInboxInv(p, 0));

					} else if (event.getRawSlot() == 6) {
						p.openInventory(InboxUtils.getInboxInv(p, 1));

					} else if (event.getRawSlot() == 8) {
						if (PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("msg").equals("1")) {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("msg", "0");
						} else {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("msg", "1");
						}

						InboxUtils.reopenInbox(p);

					} else if (ItemUtils.getDisplayName(event.getCurrentItem()).contains(LanguageUtils.translate(p, "Message"))) {
						final String target = ChatColor.stripColor(ItemUtils.getDisplayName(event.getCurrentItem()).replace(LanguageUtils.translate(p, "Message from") + " ", ""));

						if (event.getRawSlot() >= 18) {
							if (!target.contains("No ")) {
								if (!event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Delete"))) {
									if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
										Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
											public void run() {
												InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));
											}
										});
									} else {
										AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
											@Override
											public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
												if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

													if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
														event.setWillClose(false);
														event.setWillDestroy(false);
													} else {
														event.setWillClose(true);
														event.setWillDestroy(true);

														String message = event.getName();

														MessageCommand.sendMessage(p, target, message);
													}

												} else {
													event.setWillClose(false);
													event.setWillDestroy(false);
												}
											}
										});
										gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter message"));
										gui.open();
										p.setLevel(0);
										p.setExp(0);
									}

								} else {
									Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
										public void run() {
											InboxUtils.removeMessage(p.getUniqueId(), target);
											final Inventory inv = InboxUtils.getInboxInv(p, 1);

											Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
												public void run() {
													p.openInventory(inv);
												}
											});
										}
									});
								}
							}
						}

					} else if (ItemUtils.getDisplayName(event.getCurrentItem()).contains(LanguageUtils.translate(p, "Request"))) {
						final String target = ChatColor.stripColor(ItemUtils.getDisplayName(event.getCurrentItem()).replace("Request from ", ""));

						if (event.getRawSlot() >= 18) {
							if (event.getAction().name().contains("ALL")) {
								Channel.playerSessionRequests.put(p.getName(), Channel.playerSessionRequests.get(p.getName()).replace(target + "/;", ""));
								p.closeInventory();
								Channel.makeJoinServer(target, Channel.serverName);

							} else {
								Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));
									}
								});
							}
						}
					} else {
						final String target = ChatColor.stripColor(ItemUtils.getDisplayName(event.getCurrentItem()).replace("Invite from ", ""));

						if (event.getRawSlot() >= 18) {
							if (event.getAction().name().contains("ALL")) {
								Channel.playerSessionRequests.put(p.getName(), Channel.playerSessionRequests.get(p.getName()).replace(target + "/;", ""));
								p.closeInventory();
								MasterPlugin.getMasterPlugin().channel.joinPlayer(p, target);

							} else {
								Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										InventoryUtils.openPlayerInventory(p, SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target)));
									}
								});
							}
						}

					}

					// Achievements & Stats

				} else if (event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Achievements"))) {
					event.setCancelled(true);

					if (event.getRawSlot() == 8 && event.getClickedInventory().getItem(8).getType() != Material.AIR) {
						if (PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("am").equals("1")) {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("am", "0");
						} else {
							PlayerSessionManager.getSession(p).getAccount().profileSettings.setSetting("am", "1");
						}

						Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								StatsUtils.openStatsInventory(p, p.getName());
							}
						});
					}

					// Party

				} else if (event.getClickedInventory().getTitle().contains(LanguageUtils.translate(p, "Party")) || event.getClickedInventory().getTitle().toLowerCase().contains(LanguageUtils.translate(p, "Invites"))) {
					event.setCancelled(true);

					if (event.getRawSlot() == 2) {
						AnvilUtils gui = new AnvilUtils(p, new AnvilUtils.AnvilClickEventHandler() {
							@Override
							public void onAnvilClick(final AnvilUtils.AnvilClickEvent event) {
								if (event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT) {

									if (event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
										event.setWillClose(true);
										event.setWillDestroy(true);
									} else {
										event.setWillClose(false);
										event.setWillDestroy(false);

										if (PartyManager.getParty(event.getName(), false) == null) {
											if (PartyManager.getParty(p.getName(), false) == null) {
												PartyManager.createParty(p);
											}

											PartyManager.getParty(p.getName(), false).invite(event.getName());

											p.openInventory(PartyUtils.getPartyInv(p, 0));

										} else {
											String message = ChatColor.YELLOW + event.getName() + ChatColor.RED + " already joined a party.";

											ItemMeta meta = event.getClickedItem().getItemMeta();
											meta.setDisplayName(message);
											event.getClickedItem().setItemMeta(meta);
										}
									}

								} else {
									event.setWillClose(false);
									event.setWillDestroy(true);

									p.openInventory(BlockUtils.getBlockInv(p, 0));
								}
							}
						});
						gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.createItem(Material.getMaterial(421), 1, (byte) 0, LanguageUtils.translate(p, "Enter username"), null));
						gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter username"));
						gui.open();
						p.setLevel(0);
						p.setExp(0);

					} else if (event.getRawSlot() == 0) {
						p.openInventory(PartyUtils.getPartyInv(p, 0));

					} else if (event.getRawSlot() == 8) {
						p.openInventory(PartyUtils.getPartyInv(p, 1));

						/*
						 * } else if(event.getRawSlot() == 6) {
						 * if(PlayerSessionManager.getSession(p).getAccount().
						 * profileSettings.getSetting("vb").equals("1")) {
						 * PlayerSessionManager.getSession(p).getAccount().
						 * profileSettings.setSetting("vb", "0"); } else {
						 * PlayerSessionManager.getSession(p).getAccount().
						 * profileSettings.setSetting("vb", "1"); }
						 * 
						 * p.openInventory(BlockUtils.getBlockInv(p, 0));
						 */

					} else {
						/*
						 * final String target =
						 * ChatColor.stripColor(ItemUtils.getDisplayName(event.
						 * getCurrentItem()));
						 * 
						 * if(target != null && ) {
						 * if(event.getAction().name().contains("ALL")) {
						 * p.closeInventory();
						 * 
						 * //TODO accept invite
						 * 
						 * } else if(event.getAction().name().contains("HALF"))
						 * { p.closeInventory();
						 * 
						 * //TODO deny invite } }
						 */
					}

					// Player Inventory

				} else if (event.getClickedInventory().getTitle().contains(" - " + LanguageUtils.translate(p, "Page") + " ")) {
					event.setCancelled(true);

					String target = ChatColor.stripColor(event.getClickedInventory().getTitle()).split(" - " + LanguageUtils.translate(p, "Page") + " ")[0];
					int page = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getTitle()).split(" - " + LanguageUtils.translate(p, "Page") + " ")[1]);

					if (event.getCurrentItem() != null) {
						if (event.getCurrentItem().equals(PlayerInventory.nextPage)) {
							p.closeInventory();
							new PlayerInventory(target).openInventory(p, page + 1);

						} else if (event.getCurrentItem().equals(PlayerInventory.prevPage)) {
							new PlayerInventory(target).openInventory(p, page - 1);

						} else if (event.getRawSlot() >= 18) {
							Item item = ItemManager.getItem(event.getCurrentItem());

							if (p.getName().equals(target)) {
								if (event.getAction().name().contains("ALL")) {
									if (item.isEquipped()) {
										new PlayerInventory(target).unequipItem(item);
									} else {
										new PlayerInventory(target).equipItem(item);
									}

									p.closeInventory();
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		try {
			int exp = PlayerSessionManager.getSession(((Player) event.getPlayer())).getAccount().getExp();

			((Player) event.getPlayer()).setLevel(ExpUtils.getLevel(exp));
			((Player) event.getPlayer()).setExp((float) ExpUtils.getCurrentExp(exp) / (float) ExpUtils.expNeeded(ExpUtils.getLevel(exp) + 1));
		} catch (Exception e) {
		}
	}
}
