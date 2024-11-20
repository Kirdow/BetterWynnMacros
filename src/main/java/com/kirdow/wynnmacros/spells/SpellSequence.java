package com.kirdow.wynnmacros.spells;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.mixin.InGameHudAccessor;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kirdow.wynnmacros.util.WynnHelper.mc;

public class SpellSequence {

    public static Optional<List<SpellKey>> extractSequence() {
        var hud = mc().inGameHud;
        Text text = ((InGameHudAccessor)hud).getOverlayText();
        if (text == null) {
            Logger.dev("No cast sequence can be active");
            return Optional.empty();
        }

        String fullText = text.getString();

        Matcher matcher = REGEX.matcher(fullText);
        if (!matcher.find()) {
            Logger.dev("No cast sequence is active");
            return Optional.empty();
        }

        List<SpellKey> result = new ArrayList<>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            String match = matcher.group(i);

            Optional<SpellKey> spellKey = Optional.empty();
            if (match.matches(SpellIcon.LEFT.getRegex())) {
                Logger.dev("Found left");
                spellKey = SpellKey.get(SpellIcon.LEFT);
            } else if (match.matches(SpellIcon.RIGHT.getRegex())) {
                Logger.dev("Found right");
                spellKey = SpellKey.get(SpellIcon.RIGHT);
            } else if (match.matches(SpellIcon.NONE.getRegex())) {
                Logger.dev("Found none");
                break;
            } else {
                Logger.dev("Invalid sequence character: \"%s\"", match);
                return Optional.empty();
            }

            if (spellKey.isEmpty()) {
                Logger.dev("Failed to create SpellKey");
                return Optional.empty();
            }

            result.add(spellKey.get());
        }

        Logger.dev("Returning result of (len = %d)", result.size());
        return Optional.of(result);
    }

    private static final Pattern REGEX;

    static {
        final String regexSwitch = "(" + SpellIcon.LEFT + "|" + SpellIcon.RIGHT + "|" + SpellIcon.NONE + ")";
        final String regexSep = "\\s" + SpellIcon.ARROW + "\\s";

        final String regexThreeSwitches = regexSwitch + regexSep
                + regexSwitch + regexSep
                + regexSwitch;

        REGEX = Pattern.compile(regexThreeSwitches);
    }

    public enum SpellIcon {
        LEFT('\ue100', '\ue103', '\ue010', '\ue000'),
        RIGHT('\ue101', '\ue104', '\ue011', '\ue001'),
        NONE('\ue102', '\ue105', '\ue012', '\ue002'),
        ARROW('\ue106', '\ue013');

        private final String regex;
        private final char[] chars;

        SpellIcon(char...chars) {
            this.chars = new char[chars.length];
            String[] list = new String[chars.length];
            for (int i = 0; i < chars.length; i++) {
                this.chars[i] = chars[i];
                list[i] = Character.toString(chars[i]);
            }

            regex = "[" + String.join("|", list) + "]";
        }

        public String getRegex() {
            return regex;
        }

        public char[] getChars() {
            return chars;
        }

        public String toString() {
            return regex;
        }
    }
}
