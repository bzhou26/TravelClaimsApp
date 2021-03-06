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
package com.cmput301w15t15.travelclaimsapp.test.modeltest;

import com.cmput301w15t15.travelclaimsapp.model.Expense;

import junit.framework.TestCase;


/**
 * @author Chris Wang
 * Testcase for flag expenses
 *
 */
public class FlagExpenseTest extends TestCase
{
	private Expense expense1;
	private Expense expense2;
	protected void setUp() throws Exception
	{
		super.setUp();
		expense1=new Expense("taxi");
		expense2=new Expense("food");

	}
	
	/**
	 *TestCase: FlagExpenseTest#1
	 *check an expense flag and unflag  
	 */
	public void flagExpenseTest(){
		expense2.setFlag(1);
		expense1.setFlag(1);
		assertFalse("This is expense is unflag",expense1.getFlag()==1);
		expense2.setFlag(0);
		assertTrue("This is expense still have flag",expense2.getFlag()==0);
	}

}
