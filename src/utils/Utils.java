package utils;


import java.util.Random;

public class Utils {
    private static final int MAX_WAIT_TIME = 400;
    public static final int MAX_BODY = 64000;

    public static final String storage = "storage";

    public static void waitTime(){
        try {
            Random rnd = new Random();
            Thread.sleep(rnd.nextInt(MAX_WAIT_TIME));
        }catch(InterruptedException err){
            err.printStackTrace();
        }
    }
}
