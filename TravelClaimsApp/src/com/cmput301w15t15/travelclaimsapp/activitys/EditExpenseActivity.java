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

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.cmput301w15t15.travelclaimsapp.ClaimListController;
import com.cmput301w15t15.travelclaimsapp.ExpenseListAdaptor;
import com.cmput301w15t15.travelclaimsapp.ExpenseListController;
import com.cmput301w15t15.travelclaimsapp.R;
import com.cmput301w15t15.travelclaimsapp.R.id;
import com.cmput301w15t15.travelclaimsapp.R.layout;
import com.cmput301w15t15.travelclaimsapp.R.menu;
import com.cmput301w15t15.travelclaimsapp.activitys.EditClaimActivity.DatePickerFragment;
import com.cmput301w15t15.travelclaimsapp.model.Claim;
import com.cmput301w15t15.travelclaimsapp.model.ClaimList;
import com.cmput301w15t15.travelclaimsapp.model.Expense;
import com.cmput301w15t15.travelclaimsapp.model.ExpenseList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class EditExpenseActivity extends FragmentActivity implements TextWatcher {
	private Claim claim;
	private ClaimList claimList;
	private Expense expense;
	private ExpenseList expenseList;
	private ExpenseListAdaptor expenseListAdaptor;
	private int expenseCost=0;
	private ExpenseListAdaptor expenseAdaptor;
	private String expenseDescription; 
	private static EditText expenseNameInput;
	private static EditText date;
	private static EditText expenseCostInput;
	private static EditText expenseDescriptionInput;
	private Spinner currencySpinner;
	private static Spinner categorySpinner;
	private SimpleDateFormat sdf; 
	private static boolean Start;
	private String expenseName;
	private String claimName;
	private Date expenseDate;
	private byte[] imgShow;
	private ImageView expenseReceiptView;
	private int longClickDuration = 2000;
	private long then;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.edit_expense);
		
		sdf = new SimpleDateFormat("MM/dd/yyyy",Locale.CANADA);
		expenseName=this.getIntent().getExtras().getString("expenseName");
		claimName=this.getIntent().getExtras().getString("claimName");
		
		date = (EditText) findViewById(R.id.Edit_Expense_Date2);
		expenseNameInput=(EditText) findViewById(R.id.Edit_Expense_Name2);
		expenseCostInput=(EditText) findViewById(R.id.Edit_Expense_Cost2);
		expenseDescriptionInput=(EditText) findViewById(R.id.Edit_Expense_Description2);
		currencySpinner = (Spinner) findViewById(R.id.CurrencySpinner2);
		categorySpinner=(Spinner) findViewById(R.id.CategorySpinner2);
		expenseReceiptView = (ImageView) findViewById(R.id.Edit_Expense_Image2);
		
		claimList = ClaimListController.getClaimList();
		claim=claimList.getClaim(claimName);
		expenseList=claim.getExpenseList();
		expense=expenseList.getExpense(expenseName);
		expenseCost = expense.getCost();
		
		if (expense.getDes()!=null){
			expenseDescription = expense.getDes();
		}
		else{
			expenseDescription="None";
		}
		
		// show initial image
		if (expense.getPicture()!=null){
			imgShow = expense.getPicture();
	        Bitmap bm = BitmapFactory.decodeByteArray(imgShow, 0, imgShow.length);
	        DisplayMetrics dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);

	        expenseReceiptView.setMinimumHeight(dm.heightPixels);
	        expenseReceiptView.setMinimumWidth(dm.widthPixels);
	        expenseReceiptView.setImageBitmap(bm);
		}
		
		
		expenseReceiptView.setOnTouchListener(new OnTouchListener() {
		      
			@Override
		      public boolean onTouch(View v, MotionEvent event) {
		        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		          then = (long) System.currentTimeMillis();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		          if ((System.currentTimeMillis() - then) > longClickDuration) {
		            /* Implement long click behavior here */
		            System.out.println("Long Click has happened!");
		            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
			        startActivityForResult(i, 1);
		            return false;
		          } else {
		            /* Implement short click behavior here or do nothing */
		            System.out.println("Short Click has happened...");
		            return true;
		          }
		        }
		        return true;
		      }
		    });
		
		
		
//		expenseReceiptView.setOnLongClickListener(new OnLongClickListener()
//		{
//			@Override
//			public boolean onLongClick(View v) {
//				// Create the Intent for Image Gallery.
//				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//				
//				// Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
//		        startActivityForResult(i, 1);
//		        return true;
//			}
//		});
//		
//		expenseReceiptView.setOnClickListener(new OnClickListener() {
//		    @Override
//		    public void onClick(View v) {
//		        //your stuff
//		        
//		    }
//		});
//		
		
		
		
		
		currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
				expense.setCurr(parent.getItemAtPosition(position).toString());
				
				//claimList.notifyListeners();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		
		});
		
		
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
				expense.setCat(parent.getItemAtPosition(position).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		
		});
		set_on_click();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
			Uri pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            expenseReceiptView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            Bitmap imgToSave = BitmapFactory.decodeFile(imagePath);
            imgShow = getBytesFromBitmap(imgToSave);
            cursor.close();
		}
	}
	
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.JPEG, 70, stream);
	    return stream.toByteArray();
	}
	
	@Override
	protected void onStart()
	{

		super.onStart();
		
		if (expense.getName()!=null){
			expenseNameInput.setText(expense.getName());
		}
		else{
			expenseNameInput.setText(expenseName);
		}
		

		
		//expense show test
//		expense.setDate(expenseDate);
//		Date date = new Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//		System.out.println(dateFormat.format(date));


		if (expense.getDes()!=null){
			expenseDescriptionInput.setText(expense.getDes().toString());
		}

		if(expense.getCost()!=null){
			expenseCostInput.setText(expense.getCost().toString());
		}
		if(expense.getDate()!=null){
			date.setText(sdf.format(expense.getDate()));
		}

		expenseNameInput.addTextChangedListener(this);

		
		if(expense.getDate()!=null){
			date.setText(sdf.format(expense.getDate()));
		}

		ArrayAdapter<CharSequence> currencyAdaptor=ArrayAdapter.createFromResource(this, R.array.CurrencyArray, android.R.layout.simple_spinner_dropdown_item);
		currencySpinner.setAdapter(currencyAdaptor);
		
		
		ArrayAdapter<CharSequence> categoryAdaptor=ArrayAdapter.createFromResource(this, R.array.CategoryArray, android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdaptor);

		

		//int currPos=getIndex(currencySpinner,expense.getCurr());
		
		//currencySpinner.setSelection(getIndex(currencySpinner, expense.getCurr()));
		
		//currencySpinner.setSelection(currencyAdaptor.getPosition(expense.getCurr()));
		
		
		

		currencySpinner.setSelection(currencyAdaptor.getPosition(expense.getCurr()));

		categorySpinner.setSelection(categoryAdaptor.getPosition(expense.getCat()));
		
		setEditable();
		//expenseNameInput.addTextChangedListener(this);

		//date.addTextChangedListener(this);
	}

	/**
	 * check if the status of a claim is editable 
	 * 
	 */
	private void setEditable() {
		// TODO Auto-generated method stub
		if(claim.getStatus().equals("Submitted") || claim.getStatus().equals("Approved")){
			expenseNameInput.setFocusable(false);
			date.setEnabled(false);
			expenseCostInput.setFocusable(false);
			currencySpinner.setClickable(false);
			categorySpinner.setClickable(false);
			expenseDescriptionInput.setFocusable(false);
			
		}else{
			set_on_click();
			//add text changed listeners 
			expenseNameInput.addTextChangedListener(this);
			date.addTextChangedListener(this);
			expenseCostInput.addTextChangedListener(this);
			expenseDescriptionInput.addTextChangedListener(this);
		}
	}

	@Override
	protected void onResume()
	{

		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_expense, menu);
		return true;
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		//adapter.remove(adapter.getItem(info.position));
		return true;
	}
	//Retrieved on February 28, 2015 from http://developer.android.com/guide/topics/ui/controls/pickers.html
	/**This method creates a new instance of datepickerfragment and the condition statements 
	 * sets what date will be start date and which one is end
	 * @author Henry
	 * @param v
	 */
	public void showTruitonDatePickerDialog(View v)
	{	
		if (v==date){
			Start=true;
		}else{
			Start=false;
		}
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	/**
	 * @author Henry
	 * This defines dialogfragment by using the onCreateDialog method
	 */
	public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override

		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			// Do something with the date chosen by the user
			if (Start)
			{
				date.setText((month + 1) + "/" + day + "/" + year);
			}
		}	
	}
	
	private void set_on_click()
	{
		date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				showTruitonDatePickerDialog(v);
			}
		});
	}
	
	/** Function that is called when "Search" menu item is clicked
	 * and switches to the searchactivity
	 * @author Henry
	 * @param menu
	 */
	public void SearchOption(MenuItem menu)
    {
    	Toast.makeText(this, "Going to Search", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(EditExpenseActivity.this, SearchActivity.class);
    	startActivity(intent);
    }
	
	/**Function that is called when the "Sign Out" menu item is clicked
	 * and switches to the mainscreenactivity
	 * @author Henry
	 * @param menu
	 */
	public void SignOut(MenuItem menu)
    {
    	Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(EditExpenseActivity.this, MainMenuActivity.class);
    	startActivity(intent);
    }
	
	/** Function that is called when the "Ok" Button is clicked and switches 
	 * to the ExpenseListActivity
	 * @author Henry
	 * @param view
	 */
	public void CreateExpense(View view)
    {
    	Toast.makeText(this, "Creating an expense", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(EditExpenseActivity.this, ExpenseListActivity.class);

    	Bundle bundle=new Bundle();
    	String claimName= claim.getName();
    	intent.putExtra("claimName", claimName);

    	String expenseName=expense.getName();
    	expense.takePicture(imgShow);
    	intent.putExtra("expenseName", expenseName);
    	//Toast.makeText(this, expense.getDes(), Toast.LENGTH_SHORT).show();////

    	startActivity(intent);   
    }

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		switch(getCurrentFocus().getId()){
		case R.id.Edit_Expense_Name2:
			String newName = expenseNameInput.getText().toString();
			if(s.length() == 0 ){
				Toast.makeText(this, "Expense name cannot be null", Toast.LENGTH_LONG).show();
			}else{
				expense.setName(expenseNameInput.getText().toString());
			}
		case R.id.Edit_Expense_Date2:
			try{
				expense.setDate(sdf.parse(date.getText().toString()));
			}catch(ParseException e){
				//do nothing 
			}

		case R.id.Edit_Expense_Cost2:
			try{
				expense.setCost(Integer.parseInt(expenseCostInput.getText().toString()));
			}catch(NumberFormatException e){
				//do nothing
			}
		case R.id.Edit_Expense_Description2:
			expense.setDes(expenseDescriptionInput.getText().toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	
	
	/**
	 * help to find the position of one value in spinner list
	 * @param spinner
	 * @param myString
	 * @return
	 */
	private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
	
	
}
