
package sg.edu.ntu.mdp.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonOperation {
    public String sha1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X", b));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }



    public void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static  String HexToBinary(String Hex) {
        int i = Integer.parseInt(Hex, 16);
        String bin = Integer.toBinaryString(i);
        while(bin.length()!=4)
        {
            bin="0"+bin;
        }
        return bin;
    }

    public static  String convertGridFormat(String gridDataInBinary) {

        String convertedData="";
        for (int i = 0; i < 300; i += 15) {
            convertedData = gridDataInBinary.substring(i, i + 15) + convertedData;
        }
        return convertedData;
    }



}
