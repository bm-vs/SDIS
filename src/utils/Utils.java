package utils;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    private static final int MAX_WAIT_TIME = 400;
    public static final int MAX_BODY = 64000;
    public static final int MAX_REPEAT = 5;

    public static final String storage = "storage";

    public static void waitTime(){
        try {
            Random rnd = new Random();
            Thread.sleep(rnd.nextInt(MAX_WAIT_TIME));
        }catch(InterruptedException err){
            err.printStackTrace();
        }
    }

    public static boolean correctBackup(String filename, int chunks){
        try{
            RandomAccessFile in = new RandomAccessFile(filename, "r");
            int size = (int)in.length();
            int totalChunks = size / 64000;
            return (totalChunks + 1) == chunks;
        }catch (IOException err){
            return true;
        }
    }


    /**
     * Taken from https://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
     */
    public static <T, E> Set<T> getKeysByValue(HashMap<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(HashMap.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
