package util;


import java.io.*;

/**
 * Created by RGupta on 12/18/2017.
 */
public class GetXSDsFromAccurev {
    String localpath =null;


    public  GetXSDsFromAccurev(String env) {
        try {
            String[] command =
                    {
                            "cmd",
                    };
            Process p = Runtime.getRuntime().exec(command);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());

            switch (env) {
                case "QA":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\QA_6.0");
                    localpath = "c:\\Users\\RGupta\\accurev\\QA_6.0";
                    break;
                case "DEV":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\Integration_6.0");
                    localpath = "c:\\Users\\RGupta\\accurev\\Integration_6.0";
                    break;
                case "STAGING":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\ReleasePending_5.0");
                    localpath = "c:\\Users\\RGupta\\accurev\\ReleasePending_5.0";
                    break;
            }
            stdin.println("accurev login -H accurev01p:5050 rithika.gupta 1");
            stdin.println("accurev update");
            stdin.println("cd " + localpath + "\\Source\\UpToDate.Editorial.Export\\Resources");
            localpath = localpath + "\\Source\\UpToDate.Editorial.Export\\Resources";
        }
        catch(IOException e){
            System.out.println("EXCEPTION Thrwon \n"+e);
        }

    }



    public static class SyncPipe implements Runnable {
        public SyncPipe(InputStream istrm, OutputStream ostrm) {
            istrm_ = istrm;
            ostrm_ = ostrm;
        }
        public void run() {
            try
            {
                final byte[] buffer = new byte[1024];
                for (int length = 0; (length = istrm_.read(buffer)) != -1; )
                {
                    ostrm_.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        private final OutputStream ostrm_;
        private final InputStream istrm_;
    }

    /****
     * Below are methods to return Topics XSDs
     * @return
     */
    public String getTopicXSD(){
        return localpath+"\\Topic.xsd";
    }


    public String getTopicMetaXSD(){
        return localpath+"\\Topic_Meta.xsd";
    }


    public String getTopicErrorXSD(){
        return localpath+"\\Topic_Error.xsd";
    }


    /****
     * Below are methods to return ICG XSDs
     * @return
     */
    public String getICGMetaXSD(){
        return localpath+"\\ICG_Meta.xsd";
    }


    public String getICGTOCXSD(){
        return localpath+"\\ICG_Toc.xsd";
    }



    /****
     * Below are methods to return LABCs XSDs
     * @return
     */
    public String getLABCMetaXSD(){
        return localpath+"\\lab_meta.xsd";
    }


    public String getLABCXSD(){
        return localpath+"\\lab.xsd";
    }


    /****
     * Below are methods to return LABIs XSDs
     * @return
     */
    public String getLABIMetaXSD(){
        return localpath+"\\labi_meta.xsd";
    }


    public String getLABIXSD(){
        return localpath+"\\labi.xsd";
    }


    /****
     * Below are methods to return WHATS NEW XSDs
     * @return
     */
    public String getWhatsNewMetaXSD(){
        return localpath+"\\WhatsNew_Meta.xsd";
    }


    public String getWhatsNewaXSD(){
        return localpath+"\\WhatsNew.xsd";
    }
    /****
     * Below are methods to return Graphics XSDs
     * @return
     */
    public String getGraphicXSD(){
        return localpath+"\\Graphic.xsd";
    }
}
