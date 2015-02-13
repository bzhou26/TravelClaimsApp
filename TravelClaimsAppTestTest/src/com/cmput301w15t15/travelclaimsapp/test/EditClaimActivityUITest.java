package com.cmput301w15t15.travelclaimsapp.test;

import com.cmput301w15t15.travelclaimsapp.Claim;
import com.cmput301w15t15.travelclaimsapp.ClaimList;
import com.cmput301w15t15.travelclaimsapp.ClaimListController;
import com.cmput301w15t15.travelclaimsapp.EditClaimActivity;
import com.cmput301w15t15.travelclaimsapp.EditExpenseActivity;
import com.cmput301w15t15.travelclaimsapp.R;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class EditClaimActivityUITest extends
		ActivityInstrumentationTestCase2<EditClaimActivity> {

	private Instrumentation instrumentation;
	private Activity activity;
	private EditText inputName;
	private EditText inputDestination;
	private EditText inputReason;
	private EditText inputStartDate;
	private EditText inputEndDate;
	private Button addDestination;
	private Button newExpenseButton;
	private ClaimList claimList;
	private Claim claim;
	
	
	public EditClaimActivityUITest() {
		super(EditClaimActivity.class);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		instrumentation = getInstrumentation();
		
		//Need to change the id's once UI has been made
		inputName = (EditText) activity.findViewById(R.id.editText_test);
		inputDestination = (EditText) activity.findViewById(R.id.editText_test);
		inputReason = (EditText) activity.findViewById(R.id.editText_test);
		inputStartDate = (EditText) activity.findViewById(R.id.editText_test);
		inputEndDate = (EditText) activity.findViewById(R.id.editText_test);
		addDestination = (Button) activity.findViewById(R.id.button_test);
		newExpenseButton = (Button) activity.findViewById(R.id.button_test);
		
		claimList = new ClaimList();
		claim = new Claim("The Claim");
		claimList.addClaim(claim);
	}
	//test #
	public void testSettingValues(){
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				inputName.setText("Claim1");
				inputDestination.setText("Rome");
				inputReason.setText("Work");
				inputStartDate.setText("2015-02-11");
				inputEndDate.setText("2015-03-11");
			}
		});
		instrumentation.waitForIdleSync();
		assertEquals("Claim1", inputName.getText().toString());
		assertEquals("Rome", inputDestination.getText().toString());
		assertEquals("Work", inputReason.getText().toString());
		assertEquals("2015-02-11", inputStartDate.getText().toString());
		assertEquals("2015-03-11", inputEndDate.getText().toString());
	}
	// test #
	public void testAddExpenseButton(){
		ActivityMonitor activityMonitor = new ActivityMonitor(EditExpenseActivity.class.getName(), null, false);
		instrumentation.addMonitor(activityMonitor);
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				newExpenseButton.performClick();
			}
		});
		instrumentation.waitForIdleSync();
		
		Activity nextActivity = instrumentation.waitForMonitorWithTimeout(activityMonitor, 3000);
		assertNotNull(nextActivity);	
	
	}
	
	
	
	private void testAddDestination(String destination, String reason){
		instrumentation.runOnMainSync(new Runnable() {
				
		@Override
		public void run() {
			inputDestination.setText("test");
			inputReason.setText("test1");
			addDestination.performClick();
				
			}
		});
		instrumentation.waitForIdleSync();
		
		
	}
	
}
