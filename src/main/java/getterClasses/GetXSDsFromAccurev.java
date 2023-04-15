package getterClasses;


import util.Update_AccurevWorkspace;

/**
 * Created by RGupta on 12/18/2017.
 */
public class GetXSDsFromAccurev {

    String localpath =null;
   public GetXSDsFromAccurev(String env) throws Exception {
       Update_AccurevWorkspace accurevWorkspace = new Update_AccurevWorkspace(env);
       localpath = accurevWorkspace.getAccurevLocalWrokspacePath();

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


    /****
     * Below are methods to return TOC XSDs
     * @return
     */
    public String getTOCXSD(){
        return localpath+"\\TOC.xsd";
    }


    /****
     * Below are methods to return Knowledge Panel Meta XSDs
     * @return
     */
    public String getknowledgePanelMetaXSD(){
        return localpath+"\\knowledge_panel_meta.xsd";
    }

}
