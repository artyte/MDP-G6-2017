package mdp.robotxplorer.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Operation {
    public static String binaryToHex(String binaryString) {
        String hexString = "";

        if (binaryString.length() % 4 != 0) {
            int numOf0sToPad = 4 - ((binaryString.length() % 4));

            for (int i = 0; i < numOf0sToPad; i ++)
                binaryString += "0";
        }

        for (int i = 0; i < binaryString.length(); i += 4) {
            try {
                int hexValue = Integer.parseInt(binaryString.substring(i, i + 4), 2);
                hexString += Integer.toHexString(hexValue).toUpperCase();

            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Log.e(Config.log_id, e.getMessage());
            }
        }

        return hexString;
    }

    /*public static String HexToBinary(String Hex) {
        int i = Integer.parseInt(Hex, 16);
        String bin = Integer.toBinaryString(i);

        while (bin.length() != 4) {
            bin = "0" + bin;
        }

        return bin;
    }*/

    public static void writeToFile(String data, String directory) {
        try {
            File file = new File(directory);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            bos.write(data.getBytes());
            bos.close();

        } catch (IOException e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}