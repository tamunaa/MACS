package objects.questions;

import java.io.IOException;
import java.util.HashMap;

public class Parser {
    public static String StringArrayToString(String[] arr) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);
            if (i != arr.length - 1) {
                str.append(',');
            }
        }
        return str.toString();
    }

    public static String[] StringToStringArray(String str) {
        return str.isEmpty() ? new String[0] : str.split(",");
    }

    public static HashMap<String, String> GetQuestionsAndAnswersOfMatching(String str) {
        if (str.isEmpty()) return new HashMap<String, String>();
        String[] all = str.split("[,:]");
        HashMap<String, String> result = new HashMap<>();
        for (int i = 0; i < all.length; i+=2) {
            result.put(all[i], all[i + 1]);
        }
        return result;
    }
}
