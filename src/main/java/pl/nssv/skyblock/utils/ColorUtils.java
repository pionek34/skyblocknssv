package pl.nssv.skyblock.utils;

import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("##([A-Fa-f0-9]{6})");
    
    /**
     * Convert RGB values to hex color string
     * @param r Red (0-255)
     * @param g Green (0-255)
     * @param b Blue (0-255)
     * @return Hex color string like "<##RRGGBB>"
     */
    public static String rgbToHex(int r, int g, int b) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        String hex = String.format("%02X%02X%02X", r, g, b);
        return ChatColor.of("#" + hex) + "";
    }
    
    /**
     * Convert hex color to RGB array
     * @param hex Hex string like "##RRGGBB" or "RRGGBB"
     * @return int array [r, g, b]
     */
    public static int[] hexToRGB(String hex) {
        hex = hex.replace("##", "").replace("#", "");
        
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        
        return new int[]{r, g, b};
    }
    
    /**
     * Create rainbow colored text
     * @param input Text to colorize
     * @param wrapAmount Number of characters for full rainbow cycle (-1 for text length)
     * @param lightness Lightness value (0.0 - 1.0, default 0.56)
     * @return Rainbow colored text
     */
    public static String rainbow(String input, int wrapAmount, double lightness) {
        if (input == null || input.isEmpty()) return "";
        
        double delta = 360.0 / (wrapAmount > 0 ? wrapAmount : input.length());
        double lightnessModifier = ((lightness * 2) - 1) * 255;
        double angle = 90;
        
        StringBuilder output = new StringBuilder();
        String format = "";
        
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            
            // Handle minecraft color codes
            if (character == '§' || (i > 0 && input.charAt(i - 1) == '§')) {
                if ("abcdefklmnor0123456789".indexOf(character) != -1) {
                    if (character == 'r') {
                        format = "";
                    } else {
                        format += "&" + character;
                    }
                }
                continue;
            }
            
            // Calculate RGB for this character
            int r = (int) ((0.5 * (Math.sin(Math.toRadians(angle)) + 1)) * 255 + lightnessModifier);
            int g = (int) ((0.5 * (Math.sin(Math.toRadians(angle + 120)) + 1)) * 255 + lightnessModifier);
            int b = (int) ((0.5 * (Math.sin(Math.toRadians(angle + 240)) + 1)) * 255 + lightnessModifier);
            
            output.append(rgbToHex(r, g, b)).append(format).append(character);
            angle -= delta;
        }
        
        return output.toString();
    }
    
    public static String rainbow(String input) {
        return rainbow(input, -1, 0.56);
    }
    
    /**
     * Create gradient colored text
     * @param input Text to colorize
     * @param colors Array of hex colors for gradient
     * @return Gradient colored text
     */
    public static String gradient(String input, String... colors) {
        if (input == null || input.isEmpty() || colors == null || colors.length < 2) {
            return input;
        }
        
        // Parse colors to RGB
        int[][] rgb = new int[colors.length][3];
        for (int i = 0; i < colors.length; i++) {
            rgb[i] = hexToRGB(colors[i]);
        }
        
        String uncolored = ChatColor.stripColor(input);
        int n = colors.length - 1;
        double delta = 180.0 / (uncolored.length() / (double) n - 1);
        
        StringBuilder output = new StringBuilder();
        String format = "";
        int x = 0;
        
        for (int section = 0; section < n; section++) {
            int charsPerSection = (int) Math.ceil(uncolored.length() / (double) n);
            
            for (int i = 0; i < charsPerSection && (section * charsPerSection + i) < uncolored.length(); i++) {
                int y = (section - 1) * charsPerSection + i;
                
                // Handle formatting codes
                while (y + x < input.length() && input.charAt(y + x) == '§') {
                    if (y + x + 1 < input.length()) {
                        char code = input.charAt(y + x + 1);
                        if ("abcdefklmnor0123456789".indexOf(code) != -1) {
                            if (code == 'r') {
                                format = "";
                            } else {
                                format += "&" + code;
                            }
                            x += 2;
                        }
                    }
                }
                
                if (y < 0 || y >= uncolored.length()) continue;
                
                char character = uncolored.charAt(y);
                
                double startProportion = 0.5 * (Math.sin(Math.toRadians(delta * (i - 1) + 90)) + 1);
                double endProportion = 0.5 * (Math.sin(Math.toRadians(delta * (i - 1) + 270)) + 1);
                
                int r = (int) (rgb[section][0] * startProportion + rgb[section + 1][0] * endProportion);
                int g = (int) (rgb[section][1] * startProportion + rgb[section + 1][1] * endProportion);
                int b = (int) (rgb[section][2] * startProportion + rgb[section + 1][2] * endProportion);
                
                output.append(rgbToHex(r, g, b)).append(format).append(character);
            }
        }
        
        return output.toString();
    }
    
    /**
     * Translate hex color codes in text
     * @param text Text with ##RRGGBB patterns
     * @return Text with proper ChatColor codes
     */
    public static String translateHexColorCodes(String text) {
        if (text == null) return null;
        
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            ChatColor color = ChatColor.of("#" + hex);
            matcher.appendReplacement(buffer, color.toString());
        }
        
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
