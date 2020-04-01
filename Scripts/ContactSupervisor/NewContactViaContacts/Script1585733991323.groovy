import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.hzi.Helper as Helper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/LoginAsContactSupervisor'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/SwitchToContacts'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.setText(findTestObject('Contacts/Page_SORMAS/contact_search_field_name'), findTestData('ContactTestData').getValue(
        2, 1))

Thread.sleep(1000)

WebUI.click(Helper.createTestObjectWithXPath('//table[@aria-rowcount]//a'))

WebUI.click(findTestObject('Object Repository/Contacts/Page_SORMAS/span_Case contacts'))

WebUI.callTestCase(findTestCase('ContactSupervisor/partials/createAndCheckNewContactFromCaseContacts'), [:], FailureHandling.STOP_ON_FAILURE)

