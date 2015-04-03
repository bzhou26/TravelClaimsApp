package com.cmput301w15t15.travelclaimsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.cmput301w15t15.travelclaimsapp.model.Claim;
import com.cmput301w15t15.travelclaimsapp.model.ClaimList;
import com.cmput301w15t15.travelclaimsapp.model.ClaimListSaveListener;
import com.cmput301w15t15.travelclaimsapp.model.Destination;
import com.cmput301w15t15.travelclaimsapp.model.DestinationList;
import com.cmput301w15t15.travelclaimsapp.model.Expense;
import com.cmput301w15t15.travelclaimsapp.model.ExpenseList;
import com.cmput301w15t15.travelclaimsapp.model.Listener;
import com.cmput301w15t15.travelclaimsapp.model.Tag;
import com.cmput301w15t15.travelclaimsapp.model.TagList;


public class SubmittedClaimListController
{
	private static ClaimList submittedClaimList = null;
	private static ExpenseList submittedExpenseList = null;
	
	public static void initSubmittedClaimListController() {
		if(submittedClaimList == null){
			submittedClaimList = FileManager.getSaver().loadSubmittedClaimLFromFile();
			if(submittedClaimList == null){
				submittedClaimList = new ClaimList();
			}
			submittedClaimList.setListeners();
			submittedClaimList.addListener(new Listener() {
				
				@Override
				public void update() {
					save();
					submittedClaimList.sort();
					
				}
			});
			//add a listener to each claim in loaded claimlist
			for(Claim claim : submittedClaimList.toArrayList()){
				claim.setListeners(); 
				addClaimListeners(claim);
			}
		}
	}
	
	
	/**
	 * Returns the global application claimList
	 * 
	 * If claimList is null it will load the claimList from the android file system
	 * and returns claimList
	 * 
	 * @return the application claimList
	 */
	static public ClaimList getClaimList() {
		if(submittedClaimList == null){
			submittedClaimList = FileManager.getSaver().loadSubmittedClaimLFromFile();
			submittedClaimList.sort();
			submittedClaimList.setListeners();
			submittedClaimList.addListener(new Listener() {
				
				@Override
				public void update() {
					save();
					submittedClaimList.sort();
					
				}
			});
			//add a listener to each claim in loaded claimlist
			for(Claim claim : submittedClaimList.toArrayList()){
				claim.setListeners(); 
				addClaimListeners(claim);
			}
			return submittedClaimList;
		}
		return submittedClaimList;
	}
	
	/**
	 * Returns a ArrayList<Tag> containing all unique tags in claimlist
	 * 
	 * Searches through each claim in singleton claimlist and returns list
	 * of unique tags.
	 * 
	 * @return all unique tags in the singleton claimlist
	 */
	
	
	public static ArrayList<Tag> getTagList(){
		TagList tags = new TagList();
		
		for(Claim claim : submittedClaimList.toArrayList()){
			TagList tmp = claim.getTagList();
			for(Tag tag : tmp.toArrayList()){
				if(!tags.contains(tag.getName())){
					tags.addTag(tag);
				}
			}
		}
		return tags.toArrayList();
	}
	
	
	
	
	/**
	 * Uses ClaimList.addClaim to add claim with listeners to claimList. 
	 * 
	 * @param claim claim to be added to claimlist 
	 * @throws IOException 
	 */
	public static void addClaim(Claim claim){
		addClaimListeners(claim);
		getClaimList().addClaim(claim);
	}
	/**
	 * Uses ClaimList.removeClaim to remove a claim from claimList. 
	 * 
	 * @param claim claim to be removed from claimlist 
	 */
	public static void removeClaim(Claim claim){
		
		getClaimList().removeClaim(claim);
	}
	
	
	/**
	 * Uses Claim.addExpense() to add a expense with listener to claim
	 * 
	 * @param expense	the Expense to be added to claim
	 * @param claim		the Claim to add expense to
	 */
	public static void addExpense(Expense expense, Claim claim){
		claim.addExpense(expense);
		claim.getExpenseList().sort();
		expense.addListener(new ClaimListSaveListener());
	}
	
	/**
	 * Removes expense from singleton Claimlist
	 */
	public static void removeExpense(Expense expense, Claim claim){
		claim.removeExpense(expense);
	}
	
	/**
	 * Adds a destination to a claim
	 * 
	 * @param dest 	destination to add to claim
	 * @param claim claim to add destination to 
	 */
	public static void addDestination(Destination dest, Claim claim){
		claim.getDestinationList().addDestination(dest);
		dest.addListener(new ClaimListSaveListener());
	}
	/**
	 * Deletes a destination from a claim
	 * 
	 * @param dest	destination to delete
	 * @param claim removes destination from this claim
	 */
	public static void removeDestination(Destination dest, Claim claim){
		claim.getDestinationList().deleteDestination(dest);
		
	}
	/**
	 * Adds a tag to a given claim
	 * 
	 * @param claim claim to add tag to 
	 * @param tag   tag to add to claim
	 */
	public static void addTag(Claim claim, Tag tag) {
		tag.addListener(new ClaimListSaveListener());
		claim.getTagList().addTag(tag);
	}
	
	/**
	 * Removes a {@link Tag} from a given {@link Claim}
	 * 
	 * @param claim claim to delete tag from 
	 * @param tag   tag to delete
	 */
	public static void removeTag(Claim claim, Tag tag) {
		claim.getTagList().removeTag(tag);
	}
	
	/**
	 * Saves claimlist to file using FileManager class 
	 */
	public static void save(){
		FileManager.getSaver().saveSubmittedClaimLInFile(getClaimList());
	}
	
	/**
	 * Sets the application {@link ClaimList} to new empty {@link ClaimList}
	 */
	public static void removeAllClaims(){
		submittedClaimList = new ClaimList();
		save();
	}
	
	/**
	 * Adds listeners to the claim as well as the claims expenselist and expenses 
	 * 
	 * @param claim 	the Claim to add listeners to
	 */
	private static void addClaimListeners(Claim claim){
		//first add a listener to the claim
		claim.addListener(new Listener() {
			@Override
			public void update() {
				getClaimList().sort();
				save();
			}
		});
		//next add listener to expenselist
		ExpenseList eList = claim.getExpenseList();
		eList.setListeners();
		eList.addListener(new ClaimListSaveListener());
		//next add a listener to each expense in the claim 
		for(Expense expense : eList.toArrayList()){
			expense.setListeners();
			expense.addListener(new ClaimListSaveListener());
			if(expense.getGeoLocation() != null){
				expense.getGeoLocation().setListeners();
				expense.getGeoLocation().addListener(new ClaimListSaveListener());
			}
		}
		
		DestinationList dList = claim.getDestinationList();
		dList.setListeners();
		dList.addListener(new ClaimListSaveListener());
		for(Destination dest : dList.toArrayList()){
			dest.setListeners();
			dest.addListener(new ClaimListSaveListener());
			if(dest.getGeoLocation() != null){
				dest.getGeoLocation().setListeners();
				dest.getGeoLocation().addListener(new ClaimListSaveListener());
			}
		}
		TagList tList = claim.getTagList();
		tList.setListeners();
		tList.addListener(new ClaimListSaveListener());
		for(Tag tag : tList.toArrayList()){
			tag.setListeners();
			tag.addListener(new ClaimListSaveListener());
			
		}
		
	}


	/**
	 * Returns the current ClaimList filtered as a ArrayList<Claim>
	 * 
	 * Takes a string containing comma separated search words and 
	 * returns a ArrayList of the claims that contain all search terms
	 * 
	 * @param filterString string to filter claimlist with
	 * @return	ArrayList<Claim> containing claims that match filter
	 */
	public static ArrayList<Claim> getFilteredClaimList(String filterString) {
		final ArrayList<Claim> claims = getClaimList().toArrayList();
	    ArrayList<Claim> newClaims = new ArrayList<Claim>(claims.size());
	    String[] searchWords = filterString.split(",");
        int wordCount = searchWords.length;
        boolean match;
        for (Claim claim : claims) {
        	match = false;
            ArrayList<Tag> tagList = claim.getTagList().toArrayList();
            
	            for(Tag tag : tagList){
	            	String tagName = tag.getName().toString().toLowerCase(Locale.CANADA);
	            	// First match against string
	                if (tagName.equals(filterString.toLowerCase(Locale.CANADA))){
	                    newClaims.add(claim);
	                    match = true;
	                    break;
	                //if whole does that match then split searchString by commas 
	                //and check if all search word match
	                }else{
	                	for(String word : searchWords){
	                		if(tagName.equals(word.trim().toLowerCase(Locale.CANADA))){
	                			newClaims.add(claim);
	                			match = true;
	    	                    break;
	                		}
	                	}	
	                }
	                if(match == true){
	                	break;
	                }
            	}
            }         
		return newClaims;
	}

	public static void reset(){
		submittedClaimList = null;
		submittedExpenseList = null;
	}
}