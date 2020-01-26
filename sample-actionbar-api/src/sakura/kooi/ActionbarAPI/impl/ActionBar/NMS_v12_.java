package sakura.kooi.ActionbarAPI.impl.ActionBar;

import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import sakura.kooi.ActionbarAPI.ActionBar;
import sakura.kooi.NMSTranslator.api.SupportedVersion;

@SupportedVersion(lowestVersion = "1.12", implVersion = "MC_1.12+")
public class NMS_v12_ implements ActionBar {

	@Override
	public void sendActionBar(final Player player, final String message) {
		final CraftPlayer cplayer = (CraftPlayer) player;
		final PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.SYSTEM);
		cplayer.getHandle().playerConnection.sendPacket(packet);
	}

}
