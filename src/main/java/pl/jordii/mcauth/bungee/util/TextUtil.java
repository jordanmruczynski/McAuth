package pl.jordii.mcauth.bungee.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {

    public static BaseComponent[] newComponent(String... lines) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String line : lines) {
            messageBuilder.append(line);
            messageBuilder.append("\n");
        }
        return TextComponent.fromLegacyText(messageBuilder.toString());
    }

}
