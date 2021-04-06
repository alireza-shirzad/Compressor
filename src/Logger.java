import java.io.*;
import java.nio.file.Files;

public class Logger {
    private String loggerPath;
    private BufferedWriter writer;

    public Logger(Character type , String fileName){

        loggerPath = fileName + "_logger.txt";
        File loggerFile = new File(loggerPath);
        try {
            Files.deleteIfExists(loggerFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String messgage, boolean nextLine){
        try {
            try {
                writer =  new BufferedWriter(new FileWriter(loggerPath, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nextLine) writer.write(messgage + "\n");
            else writer.write(messgage);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
