package de.instinct.eqlibgdxutils;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class StringUtils {

	private static final int MESSAGE_MAX_LENGTH = 500;

	private static DecimalFormat decimalFormat = new DecimalFormat();
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static String getTime(long timeMillis) {
	    return timeFormat.format(new Date(timeMillis));
	}

	public static String format(double value, int decimals) {
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		decimalFormat.setMaximumFractionDigits(decimals);
		return decimalFormat.format(value).replace(",", ".");
	}
	
	public static String formatFixed(double value, int decimals) {
	    decimalFormat.setMinimumFractionDigits(decimals);
	    decimalFormat.setMaximumFractionDigits(decimals);
	    String result = decimalFormat.format(value).replace(",", ".");
	    decimalFormat.setMinimumFractionDigits(0);
	    return result;
	}

	public static String barLabelFormat(double value, double maxValue) {
		return format(value, 2) + " / " + format(maxValue, 2);
	}

	public static String limitWithAppendix(String message) {
		return message.length() > MESSAGE_MAX_LENGTH ? message.substring(0, MESSAGE_MAX_LENGTH) + "... (cut string length: " + (message.length() - MESSAGE_MAX_LENGTH) + ")" : message;
	}

	public static String limit(String message) {
		return message.length() > MESSAGE_MAX_LENGTH ? message.substring(0, MESSAGE_MAX_LENGTH) : message;
	}

	public static String limitWithAppendix(String message, int limit) {
		return message.length() > limit ? message.substring(0, limit) + "... (cut string length: " + (message.length() - limit) + ")" : message;
	}

	public static String limit(String message, int limit) {
		return message.length() > limit ? message.substring(0, limit) : message;
	}
	
	public static String limitWithDotDotDot(String message, int limit) {
		return message.length() > limit ? message.substring(0, limit - 3) + "..." : message;
	}

	public static String compress(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[4096];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        deflater.end();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static String decompress(String compressedText) {
        byte[] data = Base64.getDecoder().decode(compressedText);
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[4096];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        } finally {
            inflater.end();
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String encrypt(String value) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(setPasswd(10));
		return encryptor.encrypt(value);
	}

	public static String decrypt(String value) {
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(setPasswd(10));
			return encryptor.decrypt(value);
		} catch (EncryptionOperationNotPossibleException encryptionOperationNotPossibleException)  {
			System.out.println("decryption of: '" + value + "' not possible");
			encryptionOperationNotPossibleException.printStackTrace();
			return "decryption not possible";
		}
	}

	public static String setPasswd(int cnt) {
		String key = "halciguwbn2384238jnbsdf92828hfjskfhn9n2bf19";
		while (cnt != 0) {
			key = key + "skodjnc12038";
			cnt--;
		}
		return key;
	}

	public static String formatBigNumber(long value) {
	    return formatBigNumber(value, 2);
	}
	
	public static String formatBigNumber(long value, int decimals) {
	    if (value < 1000) {
	        return String.valueOf(value);
	    }

	    if (value < 1_000_000) {
	    	return cleanDecimal(String.format("%." + decimals + "f K", value / 1_000.0).replace(',', '.')).replace(" ", "");
	    }

	    if (value < 1_000_000_000) {
	        return cleanDecimal(String.format("%." + decimals + "f M", value / 1_000_000.0).replace(',', '.')).replace(" ", "");
	    }

	    if (value < 1_000_000_000_000L) {
	        return cleanDecimal(String.format("%." + decimals + "f B", value / 1_000_000_000.0).replace(',', '.')).replace(" ", "");
	    }
	    
	    if (value < 1_000_000_000_000_000L) {
	        return cleanDecimal(String.format("%." + decimals + "f T", value / 1_000_000_000_000.0).replace(',', '.')).replace(" ", "");
	    }
	    
	    if (value < 1_000_000_000_000_000_000L) {
	        return cleanDecimal(String.format("%." + decimals + "f Q", value / 1_000_000_000_000_000.0).replace(',', '.')).replace(" ", "");
	    }

	    return "";
	}

	private static String cleanDecimal(String number) {
	    return number.contains(".") ? number.substring(0, number.length() - 2).replaceAll("0*$","").replaceAll("\\.$","") + number.substring(number.length() - 2, number.length()) : number;
	}

	public static String generateCountdownLabel(long remainingMS, boolean showMilliseconds) {
		String minutesLabel = ((int)(remainingMS / 60000)) + "";
		int seconds = ((int)(remainingMS / 1000) % 60);
		String secondsLabel = seconds < 10 ? "0" + seconds : "" + seconds;
		return minutesLabel + ":" + secondsLabel + (showMilliseconds ? (remainingMS % 1000) : "");
	}

	public static String elide(String text, int digits) {
		return text.substring(0, digits) + "..." + text.substring(text.length() - digits, text.length());
	}

}
