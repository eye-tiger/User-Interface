package v2.Model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import v2.Model.JSONhandler;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

/**
 * @author jason
 *
 * This class handles all the information related to the students db 
 */
public class Students implements Runnable {

	private Database dynam_db;
	private Database stati_db;
	private Map<String, JSONhandler> dynamic;
	private Map<String, JSONhandler> stati;
	
	/**
	 * @param con - The Cloudant database to connect to 
	 * 
	 * This method creates the database connections and initializes the storage structures
	 */
	public Students(CloudantClient con){
		this.dynam_db = con.database("dynamic_user_info", false);
		this.stati_db = con.database("static_user_info", false);
		
		this.dynamic = new HashMap<String, JSONhandler>();
		this.stati   = new HashMap<String, JSONhandler>();
		
	
	}
	
	/**
	 * This method pulls all the dynamic and static information from the database
	 */
	public void getData(){
		//gets list of all students in the database and stores the result in an arraylist
		JsonObject listDyn = new JsonObject();
    	listDyn = dynam_db.findAny(listDyn.getClass(), "https://eyeofthetiger.cloudant.com/dynamic_user_info/_all_docs");
    	//dynam_db.findAny(InputStream.class, "https://eyeofthetiger.cloudant.com/static_user_info/80:ea:ca:00:00:01/Goku_Dragon_Ball_Z.png");
    	JSONhandler ad = new JSONhandler( listDyn );
    	ArrayList<String> list = ad.extractFromArray( "rows", "id");
    	
    	JSONhandler d, s; //to store each fetched paged from the db for both static and dynamic
    	
    	//gets all the student's info and adds it to the maps
    	for( String f: list ){
    		d = new JSONhandler( dynam_db.find(JsonObject.class, f) );
    		s = new JSONhandler( stati_db.find(JsonObject.class, f) );

    		this.dynamic.put(f, d);
    		this.stati.put(f, s);    		
    	}
	}
	
	/**
	 * @param id - The student with id/mac address id to be deleted from the database
	 * 
	 * This method must first update the database to ensure the latest revision value is up to date
	 * 
	 */
	public void deleteStudent(String id){
		JSONhandler d = new JSONhandler( dynam_db.find(JsonObject.class, id) );	//updates the maps to the latest revision number
		this.dynamic.put(id, d);
		d = new JSONhandler( stati_db.find(JsonObject.class, id) );	//updates the maps to the latest revision number
		this.stati.put(id, d);
		this.dynam_db.remove(this.dynamic.remove(id).instance);  		//removes the student with id from the dynamic database
		this.stati_db.remove(this.stati.remove(id).instance  ); 		//removes the student with id from the static database
	}
	
	/**
	 * @param macAddress	- the new student's bluetooth tracking tag
	 * @param studentId		- the new student's id
	 * @param firstname		- the new student's first name
	 * @param lastname		- the new student's last name
	 * @param timetable		- the new student's timetable
	 */
	public void createStudent( String macAddress, String studentId, String firstname, String lastname, ArrayList<String> timetable, InputStream image ){
		
		//adds the new student to the static info database
		JSONhandler add = new JSONhandler(new JsonObject());
		add.addData("_id", macAddress);
		add.addData("user_first_name", firstname);
		add.addData("user_last_name", lastname);
		add.addData("user_id", studentId);
		add.addData("user_number_of_absences", "0");
		add.addData("user_number_of_lates", "0");
		add.addData("user_timetable", timetable); 
		
						
		//adds the student to the dynamic database info
		JSONhandler add_dyn = new JSONhandler(new JsonObject());
		add_dyn.addData("_id", macAddress);
		add_dyn.addData("user_status", "ABSENT");
		
		/////////////////// add_dyn.addData("user_status", "ABSENT/-/-");
		//////////////////// add entry and exit times here
		///////////////////
		add_dyn.addData("entry", "-");
		add_dyn.addData("exit", "-");
		
		add_dyn.addData("user_current_class", "-");
		add_dyn.addData("user_location", "-");
		add_dyn.addData("user_daily_attendance", "-");
		add_dyn.addData("rssi", "0");
		
		Response v = this.stati_db.save(add.instance);
		this.dynam_db.save(add_dyn.instance);
		
		this.stati_db.saveAttachment(image, "profile.png", "image/png", v.getId(), v.getRev() );		
	}
	
	/**
	 * @param id - the new student's id
	 * @return - a boolean indicating the id is already in use
	 */
	public boolean isValidId(String id){
		return this.stati_db.findByIndex("\"selector\": { \"user_id\":\""+ id + "\" }" , JsonObject.class).isEmpty();
	}
	
	/**
	 * @param mac - the mac address of the bluetooth tracking card
	 * @return - a boolean indicating whether the mac address is already in use.
	 */
	public boolean isValidMac(String mac){
		
		//checks if the mac address is already in use
		if( this.dynam_db.contains(mac) || this.stati_db.contains(mac) ){
			return false;
		}
		return true;
	}
	
	public ArrayList<JSONhandler>  getStudent(String id, String crit){
		ArrayList<JSONhandler> t = new ArrayList<JSONhandler>();
		
		if( crit.equals("tag") ){
			t.add( this.dynamic.get(id) );
			t.add(this.stati.get(id) );
		}
		else{
			JSONhandler st = new JSONhandler(this.stati_db.findByIndex("\"selector\": { \"user_id\":\""+ id + "\" }" , JsonObject.class).get(0));
			JSONhandler dy = this.dynamic.get( st.toString("_id") );
			t.add(dy);
			t.add(st);
		}
		return t;
	}
	
	/**
	 * @return - a map containing the list of all dynamic student info in the db
	 */
	public Map<String, JSONhandler> getDynamicInfo(){
		return this.dynamic;
	}
	
	/**
	 * @return - a map containing the list of all static student info the db
	 */
	public Map<String, JSONhandler> getStaticInfo(){
		return this.stati;
	}

	@Override
	public void run() {
		this.getData();
	}
	
}
