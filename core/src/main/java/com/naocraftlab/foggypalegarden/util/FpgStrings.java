package com.naocraftlab.foggypalegarden.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FpgStrings {

    public static boolean wildcardMatch(String text, String pattern) {
        int textPointer = 0;
        int patternPointer = 0;
        int starIndex = -1;
        int match = 0;

        while (textPointer < text.length()) {
            if (patternPointer < pattern.length() && (pattern.charAt(patternPointer) == text.charAt(textPointer))) {
                textPointer++;
                patternPointer++;
            } else if (patternPointer < pattern.length() && pattern.charAt(patternPointer) == '*') {
                starIndex = patternPointer;
                match = textPointer;
                patternPointer++;
            } else if (starIndex != -1) {
                patternPointer = starIndex + 1;
                match++;
                textPointer = match;
            } else {
                return false;
            }
        }

        while (patternPointer < pattern.length() && pattern.charAt(patternPointer) == '*') {
            patternPointer++;
        }

        return patternPointer == pattern.length();
    }
}
