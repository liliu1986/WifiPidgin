package com.iotbyte.wifipidgin.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class Utils {
    public static final String UTILS_TAG = "UTILS";

    /**
     * Returns the sha1 of a given String
     * a boilerplate code from http://www.sha1-online.com/sha1-java/
     *
     * @param input input to calculate sha1
     * @return sha1 of the input
     */
    public static String sha1(byte[] input) {
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            // this should never happen.
            assert false;
            e.printStackTrace();
            return "";
        }
        byte[] result = mDigest.digest(input);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < input.length; i++) {
            sb.append(Integer.toString((input[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }
    /**
     * Returns the sha1 of a given String
     * a boilerplate code from http://www.sha1-online.com/sha1-java/
     *
     * @param input input to calculate sha1
     * @return sha1 of the input
     */
    public static String sha1(String input) {
        return sha1(input.getBytes());
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    @Deprecated
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            //int intVal = bytes[idx] & 0xff;
            if ( bytes[idx] == 0x0) {
                sbuf.append("0");
            }
            else {
                sbuf.append(String.format("%1X", bytes[idx]));
            }
        }
        return sbuf.toString();
    }

    /**
     * Convert string to hex byte array. For example:
     *  Input "deadbeef"
     *  Output {0xDE, 0xAD, 0xBE, 0xEF}
     *
     * @param s String to be converted into hex array.
     * @return The result hex string. Empty array byte[0] if input string in malformated.
     */
    @Deprecated
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();

        if (len == 0) {
            return new byte[0];
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * macAddressByteToHexString()
     * <p/>
     * Convert mac address from byte array representation to string representation, which
     * is used for json process
     *
     * @param bytes the byte array representation of mac address
     * @return the string representation of
     */

    public static String macAddressByteToHexString(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            // int intVal = bytes[idx] & 0xff;
            sbuf.append(String.format("%1X", bytes[idx]));
            if ((idx % 2) != 0) sbuf.append(":");
        }
        if (sbuf.length() > 0) sbuf.deleteCharAt(sbuf.length() - 1);
        return sbuf.toString();
    }

    /**
     * Converts a string of mac address into byte array.
     * @param s Mac address string. Mac address can be either column separated (12:34:56:78:90:ab),
     *          or non column separated (1234567890ab)
     * @return byte array of the mac address. Empty byte array (byte[0]) if input is mal-formatted.
     */
    public static byte[] macAddressHexStringToByte(String s) {
        String noColumnStr = s.replaceAll(":", "");
        // check against mac address length
        if (noColumnStr.length() != 12) {
            return new byte[0];
        }
        return hexStringToByteArray(s);
    }

    /**
     * Get utf8 byte array.
     *
     * @param str
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
            Log.e(UTILS_TAG, "Error getting MAC Address");
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * ipFormatter()
     * <p/>
     * The format with InetAddress.getByName() will produce a format of string with
     * (for example) /192.168.2.2 the backslash is not needed for other process
     * This method will strip off this backslash
     *
     * @param ipAddress the ip address string with unwanted backslash
     * @return a proper ip address without backslash
     */
    public static String ipFormatter(String ipAddress) {
        StringBuilder ip = new StringBuilder();

        for (int i =0; i<ipAddress.length(); i++){
            if (ipAddress.charAt(i) != '/'  ) ip.append(ipAddress.charAt(i));
        }
        return ip.toString();
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     *  @param context application context
     *  @param contentUri Uniform Resource Identifiers
     *  @return The file path to the provided URI
     **/
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    /**
     * Calculate file hash base on first x bytes. This implementation uses Utils.sha1().
     * @param filePath Absolute path of the file.
     * @param length Number of bytes at the beginning of the file to be used for hash calculation.
     *               Any length not within [1, fileSizeInBytes] will results in full file content
     *               to be used for hash calculation.
     * @return file hash. Empty string if file cannot be accessed.
     */
    public static String getFileHash(String filePath, int length) {

        try {
            FileInputStream ifs = new FileInputStream(filePath);
            long size = ifs.getChannel().size();
            if (length > 1 && length < size) {
                size = length;
            }
            byte[] hashableBytes = new byte[(int)size];
            ifs.read(hashableBytes, 0, length);
            ifs.close();
            return sha1(hashableBytes);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Calculate file hash base on the whole file. This implementation uses Utils.sha1().
     * @param filePath Absolute path of the file.
     * @return file hash. Empty string if file cannot be accessed.
     */
    public static String getFileHash(String filePath) {
        return getFileHash(filePath, 0);
    }
}
