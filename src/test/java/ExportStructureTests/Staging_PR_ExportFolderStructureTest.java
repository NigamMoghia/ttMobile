package ExportStructureTests;

import com.ValidateFileOrFolder;
import getterClasses.GetterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import getterClasses.GetXSDsFromAccurev;

import java.io.File;
import java.util.ArrayList;

import static org.testng.Assert.assertTrue;

/**
 * Created by RGupta on 12/15/2017.
 */

public class Staging_PR_ExportFolderStructureTest {
    File file = null;
    GetXSDsFromAccurev xsDsFromAccurev;
    @BeforeClass
    public void setUp() throws Exception{
        file = GetterClass.getWindowsExportFolderPath("STAGING","PR");
        xsDsFromAccurev = new GetXSDsFromAccurev("STAGING");
        System.out.println("Looking into export path : "+file);
    }
    @Test
    public void validateNumberOfFoldersShouldBe() {
        System.out.println("file= " + file);
        System.out.println("Number of folder = " + file.list().length);
    }

    @Test
    public void validateAbstractFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\abstracts");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateContrubutorsFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\contributors");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }

    }

    @Test
    public void validateDeltaFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\delta");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateGraphicsFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\graphics");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }

    }

    @Test
    public void validateIncidental_filesFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\incidental_files");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));

        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateLucene_filtersFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\lucene_filters");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateNarrativesFolderIsPresent_WithSubFolders() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\narratives");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\ICG"),false),"\n  ICG folder is missing");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\LAB"),false),"\n  LABC folder is missing");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\LABI"),false),"\n  LABI folder is missing");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\WhatsNew"),false),"\n  ICG folder is missing");
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateSOURCEFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\SOURCE");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\chineseTopics"),false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateTopicsFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\topics");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateVocabulariesFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\vocabularies");
            System.out.println("Vocabularies ar at "+folder);
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
        }else{
            System.out.println("No Export Folder found");
        }

    }

    @Test
    public void validateZhHansFolderIsPresent() {
        File folder =null;
        if(file!=null) {
            folder = new File(file + "\\zh-Hans");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder,false));
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\contributors"),false));
            assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(new File (folder+"\\vocabularies"),false));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateBuildFileIsPresent() {
        File xmlfile =null;
        if(file!=null) {
            xmlfile = new File(file + "\\build.xml");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFile(xmlfile));
        }else{
            System.out.println("No Export Folder found");
        }
    }

    @Test
    public void validateChinaBuildFolderIsPresent() {
        File xmlfile =null;
        if(file!=null) {
            xmlfile = new File(file + "\\chinaBuild.xml");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFile(xmlfile));
        }else{
            System.out.println("No Export Folder found");
        }
    }


    @Test
    public void validateExport_configurationFolderIsPresent() {
        File xmlfile =null;
        if(file!=null) {
            xmlfile = new File(file + "\\export_configuration.xml");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFile(xmlfile));
        }else{
            System.out.println("No Export Folder found");
        }
    }


    @Test
    public void validateStatisticsFolderIsPresent() {
        File xmlfile =null;
        if(file!=null) {
            xmlfile = new File(file + "\\statistics.xml");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFile(xmlfile));
        }else{
            System.out.println("No Export Folder found");
        }
    }


    @Test
    public void validateVersionFolderIsPresent() {
        File xmlfile =null;
        if(file!=null) {
            xmlfile = new File(file + "\\version.xml");
            assertTrue(ValidateFileOrFolder.validateExistenceOfFile(xmlfile));
        }else{
            System.out.println("No Export Folder found");
        }
    }

/////////////////////////////////////////////////////////////////////////////////////
    //////////////////// BELOW ARE METHODS TO TEST SUB FOLDER ///////////////////////


    @Test
    public void validate_Abstract_Folder_Contains_SubFolders_00To99(){
        File folder =null;

        if(file!=null) {
            folder = new File(file + "\\abstracts");
            assertTrue(ValidateFileOrFolder.valaidateAbstractFolderStructure(folder));
        }else{
            System.out.println("No Export Folder found");
        }
    }



    @Test
    public void validateTOC_File(){
        File folder = null;

        if(file!=null){
            folder = new File(file+"\\toc");
            try {
                assertTrue(ValidateFileOrFolder.validateExistenceOfFolder(folder, false));
                System.out.println("found folder "+folder);
            }catch (AssertionError e){
                System.out.println("The toc folder is either empty or missing");
                throw e;
            }

            try {
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File(folder + "\\toc.xml")));
                System.out.println("found file "+folder + "\\toc.xml");
            }catch (AssertionError e){
                System.out.println("The toc.xml file is missing...");
                throw e;
            }

            try {
                assertTrue(ValidateFileOrFolder.validateXMLSchema(xsDsFromAccurev.getTOCXSD(),folder+"\\toc.xml"));
                System.out.println("Validating XSD for file "+folder + "\\toc.xml");
            }catch (AssertionError e){
                System.out.println("The toc.xml is not following xsd rule...");
                throw e;
            }

        }
    }

    @Test
    public void validate_IncidentalFile_Folder_Contains_AllExpected_Files(){
        File folder =null;
        ArrayList<String> errorList = new ArrayList<String>();

        if(file!=null) {
            folder = new File(file + "\\incidental_files");
            if(ValidateFileOrFolder.validateExistenceOfFolder(folder, false)){
                try {
                    assertTrue(ValidateFileOrFolder.validateNumberOfFilesOrFoldersInDirectory(folder, 14), "Number of files in Incidental file is not 14.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\ChineseDoNotLinkKeywordTranslations.xml"))," ChineseDoNotLinkKeywordTranslations.xml is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\DisplayTagMapping.xml"))," DisplayTagMapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabAgeGroupID_Mapping.xml"))," LabAgeGroupID_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabCategory_Mapping.xml")), " LabCategory_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabConceptID_Mapping.xml")), " LabConceptID_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabConceptSpecimenLOINC_Mapping.xml"))," LabConceptSpecimenLOINC_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabGenderID_Mapping.xml"))," LabGenderID_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabPanelTests_Mapping.xml"))," LabPanelTests_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabResultID_Mapping.xml"))," LabResultID_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabSpecimenID_Mapping.xml"))," LabSpecimenID_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LabTestUTD_Mapping.xml"))," LabTestUTD_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\specialties.xml"))," specialties.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\TitlePrefix_Mapping.xml")), " TitlePrefix_Mapping.xml file is missing.");
                }catch(AssertionError e){ errorList.add(e.toString());}
                try{
                    assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\knowledge_panel_meta.xml")),"\n    knowledge_panel_meta.xml is missing in incidental file folder.\n ");
                }catch(AssertionError e){ errorList.add(e.toString());}

            }
            else{
                System.out.println("Did not run Incidental Folder's files validation as the folder is missing");
            }
        }
        for(String s:errorList)
            System.out.println(s);
        assertTrue(errorList.isEmpty());
    }
    @Test
    public void validate_Lucene_Filters_Folder_Contains_AllExpected_Files(){
        File folder =null;

        if(file!=null) {
            folder = new File(file + "\\lucene_filters");
            if(ValidateFileOrFolder.validateExistenceOfFolder(folder, false)){
                assertTrue(ValidateFileOrFolder.validateNumberOfFilesOrFoldersInDirectory(folder,9),"Number of files in Incidental file is not 9.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\AliasFilter.xml")),"AliasFilter is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\CanonicalFilter.xml")),"CanonicalFilter file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\CanonicalRelationships.xml")),"CanonicalRelationships file is missing");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\DeleteTermsFilter.xml")),"DeleteTermsFilter file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\LuceneCanonicalTerms.xml")),"LuceneCanonicalTerms file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\Modifiers.xml")),"Modifiers file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\PreferredCanonicalFilter.xml")),"PreferredCanonicalFilter file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\SynonymFilter.xml")),"SynonymFilter file is missing.");
                assertTrue(ValidateFileOrFolder.validateExistenceOfFile(new File (folder+"\\TermAmbiguities.xml")),"TermAmbiguities file is missing.");
            }
            else{
                System.out.println("Did not run Incidental Folder's files validation as the folder is missing");
            }
        }
    }

    @Test
    public void validateTOPIC_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\topics");
            File[] allSubDirectories = GetterClass.getListOfSubDirectories(folder);
            for (int i = 0; i < allSubDirectories.length; i++) {

                File speciality = new File(folder + "\\"+ allSubDirectories[i].getName());
                // System.out.println("Found Speciality : "+speciality);
                File[] allTopicsExportedInSpeciality = GetterClass.getListOfSubDirectories(speciality);


                try {
                    for (int j = 0; j < allTopicsExportedInSpeciality.length; j++) {
                        String xmlFileName = allTopicsExportedInSpeciality[j].getName();
                        //  System.out.println("File Found : " + xmlFileName);
                        if (xmlFileName.contains("_meta")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicMetaXSD(), allTopicsExportedInSpeciality[j].getAbsolutePath()));
                        } else if (xmlFileName.contains("_errors")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicErrorXSD(), allTopicsExportedInSpeciality[j].getAbsolutePath()));
                        } else {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicXSD(), allTopicsExportedInSpeciality[j].getAbsolutePath()));
                        }
                    }
                }catch(AssertionError e){
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList){
                System.out.println("ERROR  :  "+s);
            }

            assertTrue(errorList.isEmpty());
        }
    }


    @Test
    public void validateGraphics_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\graphics");
            File[] allSubDirectories = GetterClass.getListOfSubDirectories(folder);

            for (int i = 0; i < allSubDirectories.length; i++) {

                if (allSubDirectories[i].getName().equals("graphic_ids_old_new.xml"))
                    continue;

                File speciality = new File(folder + "\\"+ allSubDirectories[i].getName());
                File[] allGraphicsExportedInSpeciality = GetterClass.getListOfSubDirectories(speciality);


                try {
                    for (int j = 0; j < allGraphicsExportedInSpeciality.length; j++) {
                       // System.out.println("Speciality Of Graphic folder found : " + allGraphicsExportedInSpeciality[j].getAbsolutePath());
                        File[] allFIlesExported = GetterClass.getListOfSubDirectories(new File(allGraphicsExportedInSpeciality[j].getAbsolutePath()));

                        for (int k = 0; k < allFIlesExported.length; k++) {
                            String xmlFileName = allFIlesExported[k].getName();

                            if (xmlFileName.contains("meta.xml")) {
                                System.out.println("Going to Assert XSD for :  " + xmlFileName);
                                assertTrue(ValidateFileOrFolder.validateXMLSchema
                                        (xsDsFromAccurev.getGraphicXSD(), allFIlesExported[k].getAbsolutePath()));
                            } else if (xmlFileName.contains("_errors.xml")) {
                                assertTrue(ValidateFileOrFolder.validateXMLSchema
                                        (xsDsFromAccurev.getTopicErrorXSD(), allFIlesExported[k].getAbsolutePath()));
                            }
                            else
                                continue;
                        }
                    }
                }catch(AssertionError e){
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList){
                System.out.println("ERROR  :  "+s);
            }

            assertTrue(errorList.isEmpty());
        }
    }


    @Test
    public void validateICG_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\narratives\\ICG");

            if (ValidateFileOrFolder.validateExistenceOfFolder(folder, false)) {

                File[] allICGExported = GetterClass.getListOfSubDirectories(folder);

                try {
                    for (int j = 0; j < allICGExported.length; j++) {
                        String xmlFileName = allICGExported[j].getName();
                        //  System.out.println("File Found : " + xmlFileName);
                        if (xmlFileName.contains("_meta.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getICGMetaXSD(), allICGExported[j].getAbsolutePath()));
                        }
                        else if (xmlFileName.contains("_errors.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicErrorXSD(), allICGExported[j].getAbsolutePath()));
                        }
                        else if(xmlFileName.contains(".xml") && !xmlFileName.equals("toc_icg.xml")){
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicXSD(), allICGExported[j].getAbsolutePath()));
                        }
                        else if(xmlFileName.equals("toc_icg.xml"))
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getICGTOCXSD(), allICGExported[j].getAbsolutePath()));
                    }
                } catch (AssertionError e) {
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList) {
                System.out.println("ERROR  :  " + s);
            }

            assertTrue(errorList.isEmpty());
        }
    }



    @Test
    public void validateLABC_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\narratives\\LAB");

            if (ValidateFileOrFolder.validateExistenceOfFolder(folder, false)) {

                File[] allICGExported = GetterClass.getListOfSubDirectories(folder);

                try {
                    for (int j = 0; j < allICGExported.length; j++) {
                        String xmlFileName = allICGExported[j].getName();
                        System.out.println("File Found : " + xmlFileName);

                        if (xmlFileName.contains("Lab"))
                            continue;

                        if (xmlFileName.contains("_meta.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getLABCMetaXSD(), allICGExported[j].getAbsolutePath()));
                        } else if (xmlFileName.contains("_errors.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicErrorXSD(), allICGExported[j].getAbsolutePath()));
                        } else {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getLABCXSD(), allICGExported[j].getAbsolutePath()));
                        }
                    }

                } catch (AssertionError e) {
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList) {
                System.out.println("ERROR  :  " + s);
            }

            assertTrue(errorList.isEmpty());
        }
    }




    @Test
    public void validateLABI_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\narratives\\LABI");

            if (ValidateFileOrFolder.validateExistenceOfFolder(folder, false)) {

                File[] allICGExported = GetterClass.getListOfSubDirectories(folder);

                try {
                    for (int j = 0; j < allICGExported.length; j++) {
                        String xmlFileName = allICGExported[j].getName();
                        System.out.println("File Found : " + xmlFileName);

                        if (xmlFileName.contains("_meta.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getLABIMetaXSD(), allICGExported[j].getAbsolutePath()));
                        } else if (xmlFileName.contains("_errors.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicErrorXSD(), allICGExported[j].getAbsolutePath()));
                        } else {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getLABIXSD(), allICGExported[j].getAbsolutePath()));
                        }
                    }

                } catch (AssertionError e) {
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList) {
                System.out.println("ERROR  :  " + s);
            }

            assertTrue(errorList.isEmpty());
        }
    }




    @Test
    public void validateWhatsNew_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\narratives\\WhatsNew");

            if (ValidateFileOrFolder.validateExistenceOfFolder(folder, false)) {

                File[] allICGExported = GetterClass.getListOfSubDirectories(folder);

                try {
                    for (int j = 0; j < allICGExported.length; j++) {
                        String xmlFileName = allICGExported[j].getName();
                        System.out.println("File Found : " + xmlFileName);

                        if (xmlFileName.contains("_meta.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getWhatsNewMetaXSD(), allICGExported[j].getAbsolutePath()));
                        } else if (xmlFileName.contains("_errors.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTopicErrorXSD(), allICGExported[j].getAbsolutePath()));
                        } else {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getWhatsNewaXSD(), allICGExported[j].getAbsolutePath()));
                        }
                    }

                } catch (AssertionError e) {
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList) {
                System.out.println("ERROR  :  " + s);
            }

            assertTrue(errorList.isEmpty());
        }
    }

    @Test
    public void validateIncidentalFiles_XMLwithXSD() {
        File folder = null;
        ArrayList<String> errorList = new ArrayList<String>();

        if (file != null) {
            folder = new File(file + "\\incidental_files");

            if (ValidateFileOrFolder.validateExistenceOfFolder(folder, false)) {

                File[] allICGExported = GetterClass.getListOfSubDirectories(folder);

                try {
                    for (int j = 0; j < allICGExported.length; j++) {
                        String xmlFileName = allICGExported[j].getName();
                        // System.out.println("File Found : " + xmlFileName);

                        if (xmlFileName.contains("toc.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTOCXSD(), allICGExported[j].getAbsolutePath()));
                        }
                        else if (xmlFileName.contains("knowledge_panel_meta.xml")) {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getknowledgePanelMetaXSD(), allICGExported[j].getAbsolutePath()));
                        } /*else {
                            assertTrue(ValidateFileOrFolder.validateXMLSchema
                                    (xsDsFromAccurev.getTOCXSD(), allICGExported[j].getAbsolutePath()));
                        }*/
                    }

                } catch (AssertionError e) {
                    errorList.add(e.toString());
                }

            }
            System.out.println("******* ALL THE ERRORS REPORTED FOR TOPIC XSD VALIDATION ARE AS BELOW  **********  ");
            for (String s : errorList) {
                System.out.println("ERROR  :  " + s);
            }

            assertTrue(errorList.isEmpty());
        }
    }


}
