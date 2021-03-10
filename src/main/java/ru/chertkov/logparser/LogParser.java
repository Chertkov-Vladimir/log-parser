package ru.chertkov.logparser;

import org.apache.log4j.Logger;
import ru.chertkov.logparser.util.PropertiesUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogParser {

    private static final Logger logger = Logger.getLogger(LogParser.class);

    private static int oIpCount = 0;

    private static final char tik = '\\';
    private static final char tok = '/';
    private static int ticTocCounter = 0;

    public static void main(String[] args) throws IOException {
        logger.info("start program");

        PropertiesUtils.readProperties();
        long timeStart = System.currentTimeMillis();


        File logsDirectory = new File(System.getProperty("inputDirectory"));
        File[] files = logsDirectory.listFiles();
        if(files != null && files.length != 0){
            logger.info("start handle logs");
            int filesCount = files.length-1;//одна папка clean
            double step = 100.0/filesCount;
            double counter = 0.0;
            for(File file: files){
                if(!file.isDirectory()){
                    logger.info("handle file -> " + file.getAbsolutePath());
                    List<String> blocks = getTextBlock(getFileData(file.getAbsolutePath()), "<ns2:GetRequestResponse xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\" xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns2:RequestMessage>", "</ns2:RequestMessage></ns2:GetRequestResponse>");
                    if(!blocks.isEmpty()){
                        createCleanFile(System.getProperty("outputDirectory") + file.getName(), blocks);
                    }else{
                        logger.info("file did not contain required blocks -> " + file.getAbsolutePath());
                    }
                }
                counter += step;

                if(ticTocCounter == 0){
                    ticTocCounter +=1;
                }else{
                    ticTocCounter -=1;
                }

                System.out.print(String.format("%.2f", counter) + " % " + (ticTocCounter==0?tik:tok) + '\r');
            }
            logger.info("finish handle logs");
            logger.info("count -> " + oIpCount);
        }else{
            logger.warn("directory is empty");
        }
        logger.info("finish program");
        logger.info("time -> " + ((System.currentTimeMillis() - timeStart)/1000) + " sec.");
    }

    private static String getFileData(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream stream = Files.lines(Paths.get(filePath), Charset.forName("cp1251"))) {
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        }
        return stringBuilder.toString();
    }

    private static void createCleanFile(String filename, List<String> blocks){
        String fileName = getFileName(filename, blocks.size());
        try(FileWriter writer = new FileWriter(fileName, false)) {
            for(String block: blocks){
                writer.append(block);
                writer.append("\n");
            }
            writer.flush();
            logger.info("created file -> " + fileName);
        }
        catch(IOException e){
            logger.error(e.getMessage());
        }
        oIpCount += blocks.size();
    }

    private static String getFileName(String filename, int blocksSize){
        return filename + "_fsspOIp_" + blocksSize;
    }

    private static List<String> getTextBlock(String text, String startWord, String endWords){
        List<String> textBlocks = new ArrayList<>();
        String[] rawBlocks = text.split(startWord);
        for(int i=1;i<rawBlocks.length;i++){
            textBlocks.add(startWord + rawBlocks[i].split(endWords)[0] + endWords);
        }
        return textBlocks;
    }
}
