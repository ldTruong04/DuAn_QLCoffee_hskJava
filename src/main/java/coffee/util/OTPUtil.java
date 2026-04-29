package coffee.util;

import java.util.Random;

public class OTPUtil {
    public static String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }
}