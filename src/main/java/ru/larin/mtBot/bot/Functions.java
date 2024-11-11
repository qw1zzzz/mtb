package ru.larin.mtBot.bot;

public class Functions {
    public static boolean containsSomeCodes(String[] codes) {
        int indexOfCode;
        boolean answer = false;
        for (int i = 0; i < codes.length; i++) {
            indexOfCode = i;
            if (codes[i].matches("usd") || codes[i].matches("Usd") || codes[i].matches("USD")
                    || codes[i].matches("eur") || codes[i].matches("Eur") || codes[i].matches("EUR") ||
                    codes[i].matches("cny") || codes[i].matches("Cny") || codes[i].matches("CNY")) {
                if (indexOfCode == 0) {
                    answer = true;
                } else if (indexOfCode == 1) {
                    String buffer = codes[1];
                    codes[1] = codes[0];
                    codes[0] = buffer;
                    answer = true;
                }
                break;
            }
        }
        return answer;
    }

    public static double strToDb(String str) {
        char[] buf = str.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == ',') {
                buf[i] = '.';
            }
        }
        return Double.valueOf(String.valueOf(buf));
    }

    public static String transDate(String date) {
        char[] buf = date.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == '.') {
                buf[i] = '/';
            }
        }
        return new String(buf);
    }

    public static boolean notNegative(int num) {
        if (num >= 0) {
            return true;
        }
        return false;
    }
}
