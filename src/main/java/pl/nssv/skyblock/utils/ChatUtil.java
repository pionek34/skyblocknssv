package pl.nssv.skyblock.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("<##([A-Fa-f0-9]{6})>");
    
    public static String color(String message) {
        if (message == null) return "";
        
        // Hex colors
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);
        
        // Legacy colors
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    
    public static String stripColor(String message) {
        return ChatColor.stripColor(color(message));
    }
    
    public static String formatGradient(String text, String color1, String color2) {
        if (text == null || text.isEmpty()) return text;
        
        StringBuilder result = new StringBuilder();
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            double ratio = (double) i / (double) (length - 1);
            
            String hexColor = interpolateColor(color1, color2, ratio);
            result.append(ChatColor.of("#" + hexColor)).append(c);
        }
        
        return result.toString();
    }
    
    private static String interpolateColor(String color1, String color2, double ratio) {
        int r1 = Integer.parseInt(color1.substring(0, 2), 16);
        int g1 = Integer.parseInt(color1.substring(2, 4), 16);
        int b1 = Integer.parseInt(color1.substring(4, 6), 16);
        
        int r2 = Integer.parseInt(color2.substring(0, 2), 16);
        int g2 = Integer.parseInt(color2.substring(2, 4), 16);
        int b2 = Integer.parseInt(color2.substring(4, 6), 16);
        
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        
        return String.format("%02X%02X%02X", r, g, b);
    }
    
    public static String smallCaps(String text) {
        String[] smallCapsLetters = {"ᴀ", "ʙ", "ᴄ", "ᴅ", "ᴇ", "ꜰ", "ɢ", "ʜ", "ɪ", "ᴊ", "ᴋ", "ʟ", "ᴍ", "ɴ", "ᴏ", "ᴘ", "ǫ", "ʀ", "ѕ", "ᴛ", "ᴜ", "ᴠ", "ᴡ", "х", "ʏ", "ᴢ"};
        String[] normalLetters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        
        for (int i = 0; i < normalLetters.length; i++) {
            text = text.replace(normalLetters[i], smallCapsLetters[i]);
        }
        
        return text;
    }
}
