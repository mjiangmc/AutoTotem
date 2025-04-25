package autototem.autototem;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class AutoTotem extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AutoTotem 已启用");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoTotem 已停用");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        double health = player.getHealth();
        double damage = event.getFinalDamage();
        // 只有会致死时才处理
        if (damage < health) return;

        PlayerInventory inv = player.getInventory();
        int slot = -1;
        // 检查快捷栏 0–8
        for (int i = 0; i <= 8; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == Material.TOTEM) {
                slot = i;
                break;
            }
        }
        if (slot == -1) return; // 没有图腾，继续正常死亡

        // 恢复事件并取消伤害，保证玩家不死
        event.setCancelled(false);
        event.setDamage(0);

        // 消耗一个图腾
        ItemStack totem = inv.getItem(slot);
        totem.setAmount(totem.getAmount() - 1);
        inv.setItem(slot, totem.getAmount() > 0 ? totem : null);

        // 原版图腾效果
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,   100, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0, false, false));

        // 播放复活动画
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
    }
}
