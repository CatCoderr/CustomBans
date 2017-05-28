package me.catcoder.custombans.utility;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class StringUtility {

    private StringUtility() {

    }

    public static String readableEnum(Enum<?> e) {
        return readableVar(e.toString());
    }

    public static String readableVar(String javaName) {
        javaName = javaName.toString().toLowerCase().replaceAll("_", " ");
        String ret = "";
        boolean CASE = true;
        for (char c : javaName.toString().toCharArray()) {
            if (Character.isSpaceChar(c)) {
                ret += c;
                continue;
            }
            if (!Character.isLetter(c)) {
                CASE = true;
                continue;
            }
            if (CASE) {
                ret += Character.toUpperCase(c);
                CASE = false;
            } else {
                ret += Character.toLowerCase(c);
            }
        }
        return ret;
    }


    public static String toString(Object[] list, String del) {
        String ret = "";
        if (del == null) {
            del = "";
        }
        for (Object item : list) {
            ret += item.toString() + del;
        }
        if (!ret.isEmpty()) {
            ret.substring(0, ret.length() - del.length());
        }
        return ret;
    }

    public static String repeat(int times, String toRepeat) {
        String str = "";
        for (int i = 0; i < times; i++) {
            str += toRepeat;
        }
        return str;
    }

    public static boolean isUUID(String string) {
        return string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    public static boolean isDouble(String number) {
        boolean ret = false;
        try {
            Double.parseDouble(number);
            ret = true;
        } catch (Exception ex) {
            ret = false;
        }
        return ret;
    }

    public static boolean isInt(String number) {
        boolean ret = false;
        try {
            Integer.parseInt(number);
            ret = true;
        } catch (Exception ex) {
            ret = false;
        }
        return ret;
    }
}
