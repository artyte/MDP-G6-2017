package mdp.robotxplorer.common;

import android.content.Context;
import android.widget.Toast;

public class Operation {
    public static String HexToBinary(String Hex) {
        int i = Integer.parseInt(Hex, 16);
        String bin = Integer.toBinaryString(i);

        while (bin.length() != 4) {
            bin = "0" + bin;
        }

        return bin;
    }

    public static String convertGridFormat(String gridDataInBinary) {
        String convertedData = "";

        for (int i = 0; i < 300; i += 15) {
            convertedData = gridDataInBinary.substring(i, i + 15) + convertedData;
        }

        return convertedData;
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
