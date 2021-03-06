/*
 *TravelClaimsApp
 *Copyright (C) 2015 Jon Machinski, Bo Zhou, Henry Ha, Chris Wang, Sean Scheideman
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cmput301w15t15.travelclaimsapp.activitys;

import com.cmput301w15t15.travelclaimsapp.ClaimListAdaptor;
import com.cmput301w15t15.travelclaimsapp.ClaimListController;
import com.cmput301w15t15.travelclaimsapp.ExpenseListController;
import com.cmput301w15t15.travelclaimsapp.FileManager;
import com.cmput301w15t15.travelclaimsapp.InternetController;
import com.cmput301w15t15.travelclaimsapp.R;
import com.cmput301w15t15.travelclaimsapp.SignOutController;
import com.cmput301w15t15.travelclaimsapp.SubmittedClaimListController;
import com.cmput301w15t15.travelclaimsapp.UserController;
import com.cmput301w15t15.travelclaimsapp.model.Claim;
import com.cmput301w15t15.travelclaimsapp.model.ClaimList;
import com.cmput301w15t15.travelclaimsapp.model.Expense;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for the AddClaim/ClaimList view.
 * 
 */
public class AddClaimActivity extends Activity {

	private ClaimListAdaptor claimAdaptor;
	private ListView claimListView;
	private ClaimList claimList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_claim);
		FileManager.initializeSaver(this);
		claimListView = (ListView) findViewById(R.id.Claim_Listview);
		claimList = ClaimListController.getClaimList();
		
		//create a adaptor for claim list and set it
		claimAdaptor = new ClaimListAdaptor(this,R.layout.claim_list_adaptor, claimList.toArrayList());
		claimAdaptor.notifyDataSetChanged();
        claimListView.setAdapter(claimAdaptor);
        
		registerForContextMenu(findViewById(R.id.Claim_Listview));
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		claimAdaptor.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		claimAdaptor.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_claim, menu);
		return true;
	}
	
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.add_claim_context_menu, menu);
       
        AdapterContextMenuInfo adaptorinfo = (AdapterContextMenuInfo) menuInfo;
        int position = adaptorinfo.position;
        
        String status = claimAdaptor.getItem(position).getStatus();
        
        if(status.equals(Claim.APPROVED) || status.equals(Claim.RETURNED)){
        	menu.getItem(4).setVisible(true);
        }else{
        	menu.getItem(4).setVisible(false);
        }
    }
   
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        //get the claim the user selected 
        Intent intent;
        final Claim claim = claimAdaptor.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.cmenu_delete_claim:
            	if(claim.getStatus().equals(Claim.SUBMITTED) || claim.getStatus().equals(Claim.APPROVED)){
            		return true;		//do not delete
            	}
            	claimList.removeClaim(claim);
            	claimAdaptor.notifyDataSetChanged();
            	return true;
            case R.id.cmenu_submit_claim:
            	if(ClaimListController.incompleteFields(claim)){
            		TextView tv = new TextView(this);
            		tv.setText("There are incomplete fields for this claim. Are you sure you want to submit?");
            		AlertDialog.Builder ald = new AlertDialog.Builder(this);
            		ald.setView(tv);
            		ald.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(InternetController.isInternetAvailable2(AddClaimActivity.this)){
			            		Thread thread = new submitClaimThread(claim, AddClaimActivity.this);
			            		thread.start();
			            		claim.setStatus("Submitted");
				            	claimAdaptor.notifyDataSetChanged();
			            	} else {
			            		Toast.makeText(AddClaimActivity.this, "Internet Connection Needed", Toast.LENGTH_LONG).show();
			            	}
			            
						}
					});
            		ald.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
            		ald.show();
            		return true;
            	}else{
            		if(InternetController.isInternetAvailable2(AddClaimActivity.this)){
	            		Thread thread = new submitClaimThread(claim, AddClaimActivity.this);
	            		thread.start();
	            	} else {
	            		Toast.makeText(AddClaimActivity.this, "Internet Connection Needed", Toast.LENGTH_LONG).show();
	            	}
	            	claim.setStatus("Submitted");
	            	claimAdaptor.notifyDataSetChanged();
            	}
            	return true;
            case R.id.cmenu_addExpense:
            	intent= new Intent(AddClaimActivity.this, EditExpenseActivity.class);
            	//create new expense with default name and add to claimlist
            	ExpenseListController elc = new ExpenseListController(claim.getName(), false);
            	Expense expense = new Expense("Expense"+claim.getExpenseList().size());
            	elc.addExpense(expense);
            	// attach claim name and expense name to intent 
            	intent.putExtra("expenseName", expense.getName());
            	intent.putExtra("claimName", claim.getName());
            	startActivity(intent);  
            	return true;
            case R.id.cmenu_editExpense:
            	//pass the claim selected to new activity 
            	intent = new Intent(AddClaimActivity.this, EditClaimActivity.class);
            	intent.putExtra("claimName", claim.getName());
            	startActivity(intent);   
            	return true;
            case R.id.cmenu_view_comments:
            	ListView listView = new ListView(this);
            	ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, claim.getComments());
            	AlertDialog.Builder ald = new AlertDialog.Builder(this);
            	listView.setAdapter(ad);
            	ald.setView(listView);
            	ald.setPositiveButton("Done", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
            	ald.show();
            	
            	
            	return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /** Function that is called when "Search" menu item is clicked
	 * and switches to the searchactivity
	 * 
	 * @param menu
	 */
    public void SearchOption (MenuItem menu)
    {
    	Intent intent = new Intent(AddClaimActivity.this, SearchActivity.class);
    	startActivity(intent);
    }
    /**Function that is called when the "Sign Out" menu item is clicked
	 * and switches to the mainscreenactivity
	 * 
	 * @param menu
	 */
    public void SignOut(MenuItem menu)
    {
    	SignOutController.reset();
    	Intent intent = new Intent(AddClaimActivity.this, LoginActivity.class);
    	startActivity(intent);
    }

    /**
     * Function called when add claim button is pressed 
     * 
     * @param view
     */
    public void AddClaimButton(View view)
    {	
    	//create a default claim name and add to claimlist
    	int i = claimList.size();
    	while(ClaimListController.getClaimList().getClaim("Claim"+i)!=null){
    		i++;
    	}
    	Claim claim = new Claim("Claim"+i);
		ClaimListController.addClaim(claim);
		
    	Intent intent = new Intent(AddClaimActivity.this, EditClaimActivity.class);
    	//attach claim name to intent and start activity
    	intent.putExtra("claimName", claim.getName());
    	startActivity(intent);   
    }
 
    /**
     * Checks if user is an approver, then starts Approver activity.
     * 
     * @param menu
     */
    public void MenuApprover(MenuItem menu){
    	if(!UserController.getUser().isApprover()){
    		Toast.makeText(this, "Not an Approver", Toast.LENGTH_LONG).show();
    		return;
    	}
    	if(InternetController.isInternetAvailable2(this)){
    		Thread thread = new initApproverActivityThread(this);
			thread.start();
    	} else {
    		Toast.makeText(this, "Internet Connection Needed", Toast.LENGTH_LONG).show();
    		return;
    	}

    }
    
    /**
	 * Thread that closes the activity after Submitted ClaimList has loaded.
	 */
	private Runnable launchApprover = new Runnable() {
		public void run() {
			Intent intent = new Intent(AddClaimActivity.this, ApproverClaimListActivity.class);
	    	startActivity(intent);
		}
	};
	
	/**
	 * When connected to the internet, given info is compared with server info.
	 */
	class submitClaimThread extends Thread {
		
		private Claim toSubmit;
		private Context context;
		
		public submitClaimThread(Claim claim, Context context){
			toSubmit = claim;
			this.context = context;
		}
		
		public void run() {
			toSubmit.setClaimantName(UserController.getUser().getUsername());
        	if(!SubmittedClaimListController.initSubmittedClaimListController()){
            	Toast.makeText(this.context, "Internet Connection Needed", Toast.LENGTH_LONG).show();
        	} else {
            	SubmittedClaimListController.addClaim(toSubmit);
            	SubmittedClaimListController.save();
        	}
        	
			
		}
		
	}
	
	/**
	 * Thread to get submittedClaimList for Controller in Approver activity.
	 */
	class initApproverActivityThread extends Thread {
		
		Context context;
		
		public initApproverActivityThread(Context context) {

			this.context = context;
		}

		public void run() {
			if(!SubmittedClaimListController.initSubmittedClaimListController()){
				Toast.makeText(context, "Internet connection needed", Toast.LENGTH_SHORT).show();
			} else {
				runOnUiThread(launchApprover);
			}
		
		}
		
	}
    
	
}
