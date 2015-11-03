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

public class Found {
	public static void main(String[] args) throws Exception{
		//JSONArray songsMetaData = convertToJSONArray(System.getProperty("user.dir") + "/mashringins/MashupsongsMetaData.json");
		//String matchResult = readAll(new FileReader(System.getProperty("user.dir") + "/mashringins/MashupResult.json"));
		JSONArray songsMetaData = convertToJSONArray(System.getProperty("user.dir") + "/albumsongsMetaData.json");
		String matchResult = readAll(new FileReader(System.getProperty("user.dir") + "/AlbumResult.json"));
		String[] jsons = matchResult.split("\n");
		List<String> validJSONS = new ArrayList<String>();
		for(int i = 0; i < jsons.length; i++){
			if(!jsons[i].trim().equalsIgnoreCase("")){
				validJSONS.add(jsons[i].trim());
			}
		}
		jsons = validJSONS.toArray(new String[validJSONS.size()]);
		File file = new File("found.txt"); //Your file
    	FileOutputStream fos = new FileOutputStream(file);
    	PrintStream ps = new PrintStream(fos);
    	System.setOut(ps);
		for(int i = 0; i < songsMetaData.length() && i < jsons.length; i++){
			if(!jsons[i].trim().equalsIgnoreCase("No Match Found")){
				JSONObject json = new JSONObject(jsons[i]);
				String offlineId = getCleanedString(get(json, "id"));
				String onlineId = getCleanedString(get(songsMetaData.getJSONObject(i), "id"));
				String offlineTitle = getCleanedString(get(json, "title"));
				String offlineAlbum = getCleanedString(get(json, "album"));
				Long offlineDuration = Long.valueOf(get(json, "duration"));
				String onlineTitle = getCleanedString(get(songsMetaData.getJSONObject(i), "title"));
				String onlineAlbum = getCleanedString(get(songsMetaData.getJSONObject(i), "album"));
				Long onlineDuration = Long.valueOf(get(songsMetaData.getJSONObject(i), "duration"));
				boolean comp;
				comp = offlineId.contentEquals(onlineId);
				int compare1 = compare(offlineTitle, onlineTitle);
				int compare2 = compare(offlineAlbum, onlineAlbum);
				Long diff = null;
				if(offlineDuration > onlineDuration){
					diff = offlineDuration - onlineDuration;
				}
				else{
					diff = onlineDuration - offlineDuration;
				}
			    if(comp == false && !(compare1 == 0 || compare2 == 0 && offlineAlbum != onlineAlbum || diff > 3))
			    {
			    	
			    	System.out.println("Content Found :: " + (i+1) + ",  OndeviceContentId :: " + onlineId + ", OnlineContentId :: " + offlineId + ", OndeviceTitle :: " + onlineTitle + ", OnlineTitle ::" + offlineTitle + ", OndeviceDuration ::" + onlineDuration + ", OnlineDuration ::" + offlineDuration);
			    	continue;
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
	
	
	static String getCleanedString(String onlineTitle){
		String onlineCleanedTitle = "";
		char[] onlineTitleChar = onlineTitle.toCharArray();
		int flag = 0;
		for(int j = 0; j < onlineTitleChar.length; j++){
			if(onlineTitle.toLowerCase().toCharArray()[j] >= 'a' && onlineTitle.toLowerCase().toCharArray()[j] <= 'z'){
				onlineCleanedTitle += onlineTitle.toCharArray()[j];
				flag = 1;
			}
			else if(onlineTitle.toLowerCase().toCharArray()[j] >= '0' && onlineTitle.toLowerCase().toCharArray()[j] <= '9'){
				onlineCleanedTitle += onlineTitle.toCharArray()[j];
				flag = 1;
			}
			else if(onlineTitle.toLowerCase().toCharArray()[j] == ' '){
				onlineCleanedTitle += onlineTitle.toCharArray()[j];
				flag = 1;
			}
			else{
				if(flag == 1){
					onlineCleanedTitle += "";
					flag = 0;
				}
			}
		}
		return onlineCleanedTitle;
	}

	static int compare(String offtext, String ontext){
		int count = 0;
		String[] arrOffText = offtext.split(" ");
		String[] arrOnText = ontext.split(" ");
		for(int i = 0; i < arrOnText.length; i++){
			for(int j = 0 ; j < arrOffText.length; j++){
				char[] charArrOnText = arrOnText[i].trim().toCharArray();
				char[] charArrOffText = arrOffText[j].trim().toCharArray();
				int per = getMatch(charArrOffText, charArrOnText);
				if(per >= 75 && per <= 100)
				//if(per == 100)
				{
					count++;
					break;
				}
			}
			
		}
		return count;
	}
	
	static int getMatch(char[] charArrOffText, char[] charArrOnText){
		int per = 0;
		int count = 0;
		int j = 0;
		for(int i = 0 ; i < charArrOnText.length; i++){
			//System.out.println(charArrOnText[i]);
			if(j == charArrOffText.length){
				j = 0;
			}
			for(; j < charArrOffText.length; j++){
				if(String.valueOf((charArrOnText[i])).trim().equalsIgnoreCase(String.valueOf(charArrOffText[j]).trim())){
					count++;
					break;
				}
			}
		}
		
		if(charArrOnText.length > 0){
			per = (int)(((double)count/charArrOnText.length) * 100);
		}
		return per;
	}
}
