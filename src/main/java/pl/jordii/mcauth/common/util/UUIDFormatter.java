package pl.jordii.mcauth.common.util;

import java.util.UUID;

public class UUIDFormatter {

    public static boolean isPremiumUUID(UUID uuid) {
        return isPremiumUUID(uuid.toString());
    }

    public static boolean isPremiumUUID(String uuid) {
        return uuid.split("-")[2].startsWith("4");
    }

    public static UUID interpolateDashes(String rawUUID) {
        if (rawUUID.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID format.");
        }

        String fullUUID = rawUUID.substring(0, 8) + "-" +
                rawUUID.substring(8, 12) + "-" +
                rawUUID.substring(12, 16) + "-" +
                rawUUID.substring(16, 20) + "-" +
                rawUUID.substring(20, 32);

        return UUID.fromString(fullUUID);
    }

    private static String removeDashes(String uuid) {
        return uuid.replace("-", "");
    }

    public static String removeDashes(UUID uuid) {
        return removeDashes(uuid.toString());
    }

}
