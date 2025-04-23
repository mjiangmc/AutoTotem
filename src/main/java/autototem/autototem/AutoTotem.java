package autototem.autototem;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
        // 注册监听器
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AutoTotem 已启用");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoTotem 已停用");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (!(e instanceof Player)) return;

        Player player = (Player) e;
        double health = player.getHealth();
        double damage = event.getFinalDamage();
        // 只有会致死时才触发
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
        if (slot == -1) {
            // 没有图腾，正常死亡
            return;
        }

        // 扣除一个图腾
        ItemStack totem = inv.getItem(slot);
        totem.setAmount(totem.getAmount() - 1);
        inv.setItem(slot, totem.getAmount() > 0 ? totem : null);

        // 取消致死伤害
        event.setDamage(0);

        // 原版图腾药水效果：
        // —— 再生 II，持续 45 秒（45*20 = 900 tick）
        // —— 吸收 II，持续 5 秒（5*20 = 100 tick）
        // —— 火焰抗性 I，持续 40 秒（40*20 = 800 tick）
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,   100, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0, false, false));

        // 播放图腾的复活动画
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
    }
}