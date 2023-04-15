package com;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by RGupta on 3/19/2018.
 */
public class Sub_Topics_do_not_link_to_missing_file {
    @Test
    public void test1()throws Exception{
       MedicalTopic medicalTopic = MedicalTopic.getinstanceOFMedicalTopicOfExportType("qa","PR");
        assertTrue(medicalTopic.validateforGraphihcRefrencesPresentInMeta("/data2/qa_nightly_pr/publish/7914-20180324-1930/data/"));
    }
}
