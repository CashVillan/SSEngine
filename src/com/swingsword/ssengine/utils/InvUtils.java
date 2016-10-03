package com.swingsword.ssengine.utils;

public class InvUtils {

	/*public static ItemStack nextPage = ItemUtils.createItem(Material.BED, 1, (byte) 0, ChatColor.WHITE + "Next Page ▶", null);
	public static ItemStack prevPage = ItemUtils.createItem(Material.BED, 1, (byte) 0, ChatColor.WHITE + "◀ Previous Page", null);

	public static HashMap<Player, String> playerTargetInv = new HashMap<Player, String>();
	
	public static void openInv(Player player, String content) {
		openPage(player, player.getName(), content, 1);
	}
	
	public static void openPage(final Player player, String targetPlayer, String targetContent, int page) {
		final Inventory inv = Bukkit.createInventory(null, 54, "Inventory - Page " + page);
		
		playerTargetInv.put(player, targetContent);
		
		List<Item> itemList = new ArrayList<Item>();
		for(String all : StringUtils.stringToList(targetContent)) {
			//itemList.add(ItemManager.getItem(Integer.parseInt(all)));
		}
		
		int x = 0;
		for(x = 0; x < itemList.size(); x++) {
			inv.setItem(18 + (x - (36 * (page - 1))), itemList.get(x).getStack());
		}
		
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + "Return to your Profile", null);
		if(!player.getName().equals(targetPlayer)) {
			playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + "Return to " + targetPlayer + "'s Profile", null);
		}
		
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(targetPlayer); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		
		if(page > 1) {
			inv.setItem(0, prevPage);
		}
		if(itemList.size() > 36 * page) {
			inv.setItem(8, nextPage);
		}
		
		if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
			inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, 0, ChatColor.WHITE + "No items.", null));
		}
		
		Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				player.openInventory(inv);
			}
		});
	}*/
}
