package sakura.kooi.ActionbarAPI;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sakura.kooi.NMSTranslator.ImplLoader;

import java.io.IOException;

public class ActionbarAPI extends JavaPlugin {
    private static ActionBar impl;
    @Override
    public void onEnable() {
        ImplLoader implLoader;
        try {
            implLoader = ImplLoader.forPlugin(this, "sakura.kooi.ActionbarAPI.impl", true);
        } catch (IOException e) {
            e.printStackTrace();
            this.setEnabled(false);
            return;
        }
        try {
            impl = implLoader.of(ActionBar.class, ActionbarReflection.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {

    }

    public static void sendActionBar(final Player player, final String message) {
        impl.sendActionBar(player, message);
    }
}
