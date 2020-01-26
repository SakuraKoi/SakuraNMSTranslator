package sakura.kooi.ActionbarAPI;

import org.bukkit.entity.Player;
import sakura.kooi.NMSTranslator.NMSTranslatorMain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionbarReflection implements ActionBar {
    private static Class<?> classCraftPlayer;
    private static Class<?> classPacketChat;
    private static Class<?> classChatSerializer;
    private static Class<?> classIChatComponent;
    private static Method methodSerializeString;
    private static Class<?> classChatComponentText;
    private static Method methodGetHandle;
    private static Field fieldConnection;
    private static Method methodSendPacket;
    private final String version = NMSTranslatorMain.getNmsVersion();

    public ActionbarReflection() throws ReflectiveOperationException {
        classCraftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        classPacketChat = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
        Class<?> classPacket = Class.forName("net.minecraft.server." + version + ".Packet");
        if (version.equalsIgnoreCase("v1_8_R1") || version.equalsIgnoreCase("v1_7_")) {
            classChatSerializer = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
            classIChatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            methodSerializeString = classChatSerializer.getDeclaredMethod("a", String.class);
        } else {
            classChatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
            classIChatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        }
        methodGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
        Class<?> classEntityPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
        fieldConnection = classEntityPlayer.getDeclaredField("playerConnection");
        Class<?> classPlayerConnection = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
        methodSendPacket = classPlayerConnection.getDeclaredMethod("sendPacket", classPacket);
    }

    @Override
    public void sendActionBar(final Player player, final String message) {
        try {
            final Object p = classCraftPlayer.cast(player);
            Object ppoc;
            if (version.equalsIgnoreCase("v1_8_R1") || version.equalsIgnoreCase("v1_7_")) {
                final Object cbc = classIChatComponent
                        .cast(methodSerializeString.invoke(classChatSerializer, "{\"text\": \"" + message + "\"}"));
                ppoc = classPacketChat.getConstructor(classIChatComponent, byte.class).newInstance(cbc, (byte) 2);
            } else {
                final Object o = classChatComponentText.getConstructor(String.class).newInstance(message);
                ppoc = classPacketChat.getConstructor(classIChatComponent, byte.class).newInstance(o, (byte) 2);
            }
            final Object h = methodGetHandle.invoke(p);
            final Object pc = fieldConnection.get(h);
            methodSendPacket.invoke(pc, ppoc);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

}
