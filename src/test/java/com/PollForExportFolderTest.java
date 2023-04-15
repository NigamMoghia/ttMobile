package com;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.UpdateCredentialManager;

import java.util.HashMap;


/**
 * Created by rgupta on 8/22/2017.
 */
public class PollForExportFolderTest {
	private PollForExportFolder p = new PollForExportFolder();

	@BeforeClass
	public void startTest() throws Exception {
		HashMap<String, String> credentialManagerParams = new HashMap<String, String>();
		credentialManagerParams.put("UserName","sautomation");
		credentialManagerParams.put("Password","Editorial$123");
		//credentialManagerParams.put("StagingPassword","Spring20!");
		credentialManagerParams.put("StagingPassword","SA#utd16");
		UpdateCredentialManager.addOrEditCredential(credentialManagerParams);

	}

	@Test(priority=1)
	public void ProdSharePoint_ExportTest() throws Exception {
		p.setEnvironment("Prod Sharepoint");
		System.out.println("SHAREPOINT  TEST STARTED----------- ");
		//boolean result = (p.didExportRun());
		boolean result = (p.verifyProdSharepointSite());
		System.out.println("SHAREPOINT  TEST PASSED----------- ");
	}
/*	@Test(priority=11)
	public void Prod_report_server() throws Exception {
		p.setEnvironment("Prod Report");
		System.out.println("PROD Report Server TEST STARTED----------- ");
		boolean result =p.verifyIfEditorialReportServerFolderUpdated();
		System.out.println("PROD Report Server  TEST PASSED----------- ");
	}*/

	@Test(priority=2)
	public void Prod_current_pr() throws Exception {
		p.setEnvironment("Production");
		System.out.println("PRODUCTION  PR TEST STARTED----------- ");
		boolean result = (p.didExportRun_pr("PR"));
		result = (p.didZipFileGotCreated_pr());
		result = (p.verifyIfExportWasProcessedOnConbld("Production","PR"));
		result = (p.verifyWebPage("Production","PR"));
		result = p.verifyIfEditorialSourceFolderUpdated();
		result = p.verifyIfLockFileIsPresent();
		System.out.println("PRODUCTION PR TEST PASSED----------- ");
	}

	@Test(priority=3)
	public void Prod_current_nightly() throws Exception{
		String status = "";
		p.setEnvironment("Production");
		System.out.println("PRODUCTION Nightly TEST STARTED----------- ");
		boolean result = (p.didExportRun("Nightly"));
		result = (p.didZipFileGotCreated());
		result = (p.verifyIfExportWasProcessedOnConbld("Production","Nightly"));
		result = (p.verifyWebPage("Production","Nightly"));
		System.out.println("PRODUCTION Nightly TEST PASSED----------- ");
	}


	@Test(priority=4)
	public void StagingSharePoint_ExportTest() throws Exception {
		p.setEnvironment("Staging Sharepoint");
		System.out.println("SHAREPOINT  TEST STARTED----------- ");
		//boolean result = (p.didExportRun());
		boolean result =  p.verifyProdSharepointSite();
		System.out.println("SHAREPOINT TEST PASSED----------- ");
	}

	@Test(priority=5)
	public void Stag_current_pr() throws Exception {
		p.setEnvironment("Staging");
		System.out.println("STAGING PR TEST STARTED----------- ");
		boolean result = (p.didExportRun_pr("PR"));
		result = (p.didZipFileGotCreated_pr());
		result = (p.verifyIfExportWasProcessedOnConbld("Staging","PR"));
		result = (p.verifyWebPage("Staging","PR"));
		result = p.verifyIfEditorialSourceFolderUpdated();
		result = p.verifyIfLockFileIsPresent();
		System.out.println("STAGING PR TEST PASSED----------- ");
	}

	@Test(priority=6)
	public void Stag_current_nightly() throws Exception{
		p.setEnvironment("Staging");
		System.out.println("STAGING Nightly TEST STARTED----------- ");
		boolean result = (p.didExportRun("Nightly"));
		result = (p.didZipFileGotCreated());
		result = (p.verifyIfExportWasProcessedOnConbld("Staging","Nightly"));
		result = (p.verifyWebPage("Staging","Nightly"));
		System.out.println("STAGING Nightly TEST PASSED----------- ");
	}

	@Test(priority=7)
	public void QA_current_pr() throws Exception{
		p.setEnvironment("QA");
		System.out.println("QA PR TEST STARTED----------- ");
		boolean result = (p.didExportRun_pr("PR"));
		result = (p.didZipFileGotCreated_pr());		
		result = (p.verifyIfExportWasProcessedOnConbld("QA","PR"));
		result = (p.verifyWebPage("QA","PR"));
		result = p.verifyIfEditorialSourceFolderUpdated();
		result = p.verifyIfLockFileIsPresent();
		System.out.println("QA PR TEST PASSED----------- ");
	}

	@Test(priority=8)
	public void QA_current_nightly() throws Exception {
		p.setEnvironment("QA");
		System.out.println("QA Nightly TEST STARTED----------- ");
		boolean result = (p.didExportRun("Nightly"));
		result = (p.didZipFileGotCreated());        
		result = (p.verifyIfExportWasProcessedOnConbld("QA","Nightly"));
		result = (p.verifyWebPage("QA","Nightly"));
		System.out.println("QA Nightly TEST PASSED----------- ");
	}



	@Test(priority=9)
	public void Dev_current_pr() throws Exception{
		p.setEnvironment("Dev");
		System.out.println("DEV PR TEST STARTED----------- ");
		boolean result = (p.didExportRun_pr("PR"));
		result = (p.verifyIfExportWasProcessedOnConbld("Dev","PR"));
		result = (p.verifyWebPage("Dev","PR"));
		//result = p.verifyIfEditorialSourceFolderUpdated();
		//result = p.verifyIfLockFileIsPresent();
		System.out.println("DEV PR TEST PASSED----------- ");
	}

	@Test(priority=10)
	public void Dev_current_nightly() throws Exception {
		p.setEnvironment("Dev");
		System.out.println("DEV Nightly TEST STARTED----------- ");
		boolean result = (p.didExportRun("Nightly"));
		result = (p.didZipFileGotCreated());
		result = (p.verifyIfExportWasProcessedOnConbld("Dev","Nightly"));
		result = (p.verifyWebPage("Dev","Nightly"));
		System.out.println("DEV Nightly TEST PASSED----------- ");
	}

	@Test(priority=11)
	public void testVerifyWebFarmPages() throws Exception {
		VerifyWebFarmsUrls webFarmsUrls= new    VerifyWebFarmsUrls();
		Thread.sleep(10000);
		webFarmsUrls.verifyWebFarmPages("Production", "PR");
	}

	@AfterClass
	public void testEmail() throws Exception {
		EmailTest em = new EmailTest();
		em.sendEMail();
	}

}