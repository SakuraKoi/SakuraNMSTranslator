package sakura.kooi.ActionbarAPI.impl.ActionBar;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import sakura.kooi.ActionbarAPI.ActionBar;
import sakura.kooi.NMSTranslator.api.SupportedVersion;

@SupportedVersion(lowestVersion = "1.8", highestVersion = "1.11.9", implVersion = "MC_1.8-1.11")
public class NMS_v8_v11 implements ActionBar {

	@Override
	public void sendActionBar(final Player player, final String message) {
		final CraftPlayer cplayer = (CraftPlayer) player;
		final PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
		cplayer.getHandle().playerConnection.sendPacket(packet);
	}

}
