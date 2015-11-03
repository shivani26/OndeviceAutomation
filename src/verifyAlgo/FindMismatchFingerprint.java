package verifyAlgo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindMismatchFingerprint {
	public static void main(String[] args) throws Exception{
		JSONArray songsMetaData = convertToJSONArray(System.getProperty("user.dir") + "/songsData.json");
		String matchResult = readAll(new FileReader(System.getProperty("user.dir") + "/fingData.json"));
		
		String[] jsons = matchResult.split("\n");
		List<String> validJSONS = new ArrayList<String>();
		for(int i = 0; i < jsons.length; i++){
			if(!jsons[i].trim().equalsIgnoreCase("")){
				validJSONS.add(jsons[i].trim());
			}
		}
		jsons = validJSONS.toArray(new String[validJSONS.size()]);
		
		for(int i = 0; i < songsMetaData.length() && i < jsons.length; i++){
			if(!jsons[i].trim().equalsIgnoreCase("No Match Found")){
				JSONObject json = new JSONObject(jsons[i]);
				if(get(json, "album") == null && get(json, "title") == null)
				{
					System.out.println((i+1) + "No Match Found");
				}
				else
				{
					String onlineAlbum = getCleanedString(get(json, "album"));
					String onlineTitle = getCleanedString(get(json, "title"));
				
				String offlineTitle = getCleanedString(get(songsMetaData.getJSONObject(i), "title"));
				String offlineAlbum = getCleanedString(get(songsMetaData.getJSONObject(i), "album"));
				
				int compare1 = compare(offlineTitle, onlineTitle);
				int compare2 = compare(offlineAlbum, onlineAlbum);
			    if(compare1 == 0 || compare2 == 0)
			    {	
			    	System.out.println("Title/Album Mismatch :: "+ (i+1) + ",  OndeviceTitle :: "  + onlineTitle + ", OnlineTitle :: " + offlineTitle + ", OndeviceAlbum :: " + offlineAlbum + ", OnlineAlbum :: " + onlineAlbum);
					continue; 
				} 
				}	
			}
			
		}
	}
	
	static public JSONObject convertToJSONObject(String path) throws IOException, JSONException{
        String jsonText = readAll(new FileReader(path));
  	    JSONObject json = new JSONObject(jsonText);  	    
		return json;
	}
	
	static public JSONArray convertToJSONArray(String path) throws IOException, JSONException{
        String jsonText = readAll(new FileReader(path));
        JSONArray json = new JSONArray(jsonText);  	    
		return json;
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    
	    return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	static public String get(JSONObject obj, String keyMain) throws Exception {
		String value = null;
	    Iterator iterator = obj.keys();
	    String key = null;
	    while (iterator.hasNext()) {
	        key = (String) iterator.next();
	        if ((obj.optJSONArray(key)==null) && (obj.optJSONObject(key)==null) && (key.equals(keyMain))) {
	        	value = obj.getString(keyMain); 
	        	break;
	        }
	        else if (obj.optJSONObject(key) != null) {
	        	value = get(obj.getJSONObject(key), keyMain);
	        }
	        else if (obj.optJSONArray(key) != null) {
	            JSONArray jArray = obj.getJSONArray(key);
	            int flag = 0;
	            for (int i = 0; i < jArray.length(); i++) {
	            	if(jArray.get(i) instanceof JSONObject){
	            		flag = 1;
	            		value = get(jArray.getJSONObject(i), keyMain);
	            	}
	            }
	            if(flag == 0){
	            	if ((key.equals(keyMain))) {
	            		for (int i = 0; i < jArray.length(); i++) {
	            			if (!(jArray.get(i) instanceof JSONObject && jArray.get(i) instanceof JSONArray)) {
	            				value += jArray.get(i) + ",";
	            			}
	            		}
	            		value = value.trim().substring(0, value.trim().length() - 1);
	            		break;
		            }
	            }
	        }
	    }
		return value;
	}
	
	
	static String getCleanedString(String offlineTitle){
		String offlineCleanedTitle = "";
		char[] offlineTitleChar = offlineTitle.toCharArray();
		int flag = 0;
		for(int j = 0; j < offlineTitleChar.length; j++){
			if(offlineTitle.toLowerCase().toCharArray()[j] >= 'a' && offlineTitle.toLowerCase().toCharArray()[j] <= 'z'){
				offlineCleanedTitle += offlineTitle.toCharArray()[j];
				flag = 1;
			}
			else if(offlineTitle.toLowerCase().toCharArray()[j] >= '0' && offlineTitle.toLowerCase().toCharArray()[j] <= '9'){
				offlineCleanedTitle += offlineTitle.toCharArray()[j];
				flag = 1;
			}
			else if(offlineTitle.toLowerCase().toCharArray()[j] == ' '){
				offlineCleanedTitle += offlineTitle.toCharArray()[j];
				flag = 1;
			}
			else{
				if(flag == 1){
					offlineCleanedTitle += "";
					flag = 0;
				}
			}
		}
		return offlineCleanedTitle;
	}

	static int compare(String ontext, String offtext){
		int count = 0;
		String[] arrOnText = ontext.split(" ");
		String[] arrOffText = offtext.split(" ");
		for(int i = 0; i < arrOffText.length; i++){
			for(int j = 0 ; j < arrOnText.length; j++){
				char[] charArrOffText = arrOffText[i].trim().toCharArray();
				char[] charArrOnText = arrOnText[j].trim().toCharArray();
				int per = getMatch(charArrOnText, charArrOffText);
				if(per >= 95 && per <= 100)
				//if(per == 100)
				{
					count++;
					break;
				}
			}
			
		}
		return count;
	}
	
	static int getMatch(char[] charArrOnText, char[] charArrOffText){
		int per = 0;
		int count = 0;
		int j = 0;
		for(int i = 0 ; i < charArrOffText.length; i++){
			//System.out.println(charArrOnText[i]);
			if(j == charArrOnText.length){
				j = 0;
			}
			for(; j < charArrOnText.length; j++){
				if(String.valueOf((charArrOffText[i])).trim().equalsIgnoreCase(String.valueOf(charArrOnText[j]).trim())){
					count++;
					break;
				}
			}
		}
		
		if(charArrOffText.length > 0){
			per = (int)(((double)count/charArrOffText.length) * 100);
		}
		return per;
	}
}
