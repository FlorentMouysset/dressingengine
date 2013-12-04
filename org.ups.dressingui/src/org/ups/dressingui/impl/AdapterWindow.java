package org.ups.dressingui.impl;

import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.IDressingSuggestionListener;

public class AdapterWindow {


	private IDressingSuggestion service;
	private IDressingSuggestionListener listener;
	private volatile String text;
	private boolean isTerminated = false;

	public AdapterWindow(IDressingSuggestion service ) {
		this.service = service;

		if(service != null){
			AdapterWindow.this.listener = new IDressingSuggestionListener() {
				@Override
				public void dressingSuggestionChanged(IDressingSuggestion newSuggestion) {
					updateSuggestionString(newSuggestion);
				}
			};
			AdapterWindow.this.service.addListener(AdapterWindow.this.listener);
		}
	}


	private void updateSuggestionString(IDressingSuggestion newSuggestion){
		if(newSuggestion.coatNeeded()){
			text = "Il faudrait un manteau !";
		}else if(newSuggestion.sunGlassesNeeded()){
			text = "Il faudrait une paire de lunettes de soleil !";
		}else if(newSuggestion.umbrellaNeeded()){
			text = "Il faudrait un parapluie !";
		}
	}



	public void removeListener() {
		this.isTerminated = true;
		this.service.removeListener(listener);
	}


	public String getSuggestionString() {
		return this.text;
	}


	public boolean isTerminated() {
		return this.isTerminated;
	}



}
