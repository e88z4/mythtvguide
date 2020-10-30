package mythtvguide;

import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.database.impl.RecorderInfo;
import org.jmythapi.protocol.*;
import org.jmythapi.*;
import org.jmythapi.protocol.impl.Recorder;
import org.jmythapi.protocol.request.EPlaybackSockEventsMode;
import org.jmythapi.protocol.response.IFreeInputInfo;
import org.jmythapi.protocol.response.IFreeInputInfoList;
import org.jmythapi.protocol.response.IFreeInputList;
import org.jmythapi.protocol.response.IInputInfoFree;
import org.jmythapi.protocol.events.IMythEvent;
import org.jmythapi.protocol.events.IMythEventListener;

import java.io.*;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import static java.lang.Thread.sleep;

public class App 
{
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main( String[] args ) throws IOException {

        ClassLoader classLoader = App.class.getClassLoader();

        Properties properties = new Properties();
        InputStream inputStream = null;

        try{
            inputStream = classLoader.getResourceAsStream("config.properties");
            properties.load(inputStream);
        }
        catch(IOException ioException)
        {
            logger.error(ioException.getMessage());
            logger.debug(ioException.getStackTrace());
        }
        finally {
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException ex)
                {
                    logger.error(ex.getMessage());
                    logger.debug(ex.getStackTrace());
                }
            }

        }

        String server = properties.getProperty("server");
        int port = Integer.valueOf(properties.getProperty("port","6543"));
        int timeout = Integer.valueOf(properties.getProperty("timeout", "5000"));

        IBackend mythBackend = BackendFactory.createBackend(server, port);
        mythBackend.connect();
        mythBackend.getCommandConnection().setMsgDebugOut(System.out);
       

        boolean result = mythBackend.annotatePlayback("mythtvguide", EPlaybackSockEventsMode.NONE);
        IFreeInputInfoList freeInputInfoList = mythBackend.getFreeInputInfo();

        if(!freeInputInfoList.asList().isEmpty())
        {
            IRecorder recorder = null;
            for(IFreeInputInfo freeInput : freeInputInfoList.asList())
            {
                logger.info("Checking free input " + freeInput.getDisplayName());
                IRecorderInfo recorderInfo = mythBackend.getRecorderForNum(freeInput.getInputId());
                recorder = mythBackend.getRecorder(recorderInfo);
                if(!recorder.isRecording()) {
                    logger.info(freeInput.getDisplayName() + " is currently free.");
                    break;
                }
            }

            List<IRecorderChannelInfo> channelInfoList = mythBackend.getChannelInfos();            

            if(recorder != null)
            {
                boolean pictureInPicture = false;
                for(IRecorderChannelInfo channelInfo : channelInfoList)
                {
                    if(recorder.spawnLiveTV(pictureInPicture, channelInfo.getChannelNumber()))
                    {
                        try {
                            recorder.waitForIsRecording(Integer.valueOf(properties.getProperty("timeout","5000")));
                            sleep(Integer.valueOf(properties.getProperty("watchingTime","5000")));
                            logger.info("Watching channel " + channelInfo.getChannelName());
                        }
                        catch(InterruptedException ex)
                        {
                            logger.error(ex);
                        }
                    }
                    else
                    {
                        logger.error("Unable to spawn live TV");
                    }

                    recorder.stopLiveTv();
                }
            }
        }

        mythBackend.close();
    }
}
