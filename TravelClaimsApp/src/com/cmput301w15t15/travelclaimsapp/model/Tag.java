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
package com.cmput301w15t15.travelclaimsapp.model;

import java.util.ArrayList;
/**
 *This is the tag model class that creates the tag object<br>
 *
 *Tags are added to claims for searching
 */
public class Tag implements Listenable{
	protected String tagName;
	private transient ArrayList<Listener> listeners;
	/**
	 * Tag constructor
	 * @param tagName1 the name to initialize Tag with 
	 */
	public Tag(String tagName1){
		this.tagName=tagName1;
		this.listeners = new ArrayList<Listener>();
	}
	/**
	 * Set the tagName
	 * @param tagName  String to set tag name to 
	 */
	public void setName(String tagName){
		this.tagName=tagName;
		notifyListeners();
	}
	
	/**
	 * Return tagName
	 * @return the String name of Tag
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return this.tagName;
	}

	/**
	 *	Return String Tage name
	 *
	 *	Used for ArrayAdaptors
	 */
	public String toString(){
		return tagName;
	}

	@Override
	public void notifyListeners() {
		for(Listener listener : listeners){
			listener.update();
		}
	}

	@Override
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void setListeners() {
		this.listeners = new ArrayList<Listener>();
	}

	@Override
	public void deleteListener(Listener listener) {
		this.listeners.remove(listener);
	}
}