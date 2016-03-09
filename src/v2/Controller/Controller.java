package v2.Controller;

import java.util.ArrayList;
import java.util.Map;

import com.trolltech.qt.gui.QApplication;
import v2.Model.JSONhandler;
import v2.Model.Model;
import v2.View.StudentTabs;
import v2.View.MainView;
import v2.View.AdminTabs;
import v2.View.CourseTabs;

/**
 * @author jason
 * 
 * This is a singleton class that instantiates both the Model class and the View
 */
public class Controller {

	private static Controller control = null; //the single instance of the Controller class
	public Model model;	                  //the model which contains all the backend of the class
	private MainView view;               //the view of the UI
	
	/**
	 * The constructor creates instances of the model and the main UI view
	 */
	private Controller(){
		model = new Model();
		view = new MainView();
	}
	
	/**
	 * @return - the single instance of the controller
	 */
	public static Controller getInstance(){
		if( control == null){
			control = new Controller();
		}
		return control;
	}
	
	/**
	 * This methods connects the view and the model together. It connects all the slots of the
	 * view to the model
	 */
	public void setUP(){		
		this.view.nc.label.setText("yeah boy what's up");
	}
	
	public void hello(String y){
		System.out.println( y);
	}
	
	public void display(){
		
		Map<String, ArrayList<JSONhandler>> t = model.retrieveStudents();
		for( String r: t.keySet() ){
			StudentTabs s = new StudentTabs();
			s.setupUi(view.stuScrollWidget);
   		    s.fname.connectSlotsByName();
   		    s.lname.connectSlotsByName();
   		    s.absents.connectSlotsByName();
   		    s.lates.connectSlotsByName();
   		    s.id.connectSlotsByName();
			s.fname.setText(t.get(r).get(1).toString("user_first_name") );
			s.lname.setText(t.get(r).get(1).toString("user_last_name") );
			s.lates.setText(t.get(r).get(1).toString("user_number_of_lates"));
			s.absents.setText(t.get(r).get(1).toString("user_number_of_absences"));
			s.id.setText(t.get(r).get(1).toString("user_id"));
			
			s.cStatus.connectSlotsByName();
			s.currClass.connectSlotsByName();
			s.curLocation.connectSlotsByName();
			s.p1.connectSlotsByName();
			s.p2.connectSlotsByName();
			s.p3.connectSlotsByName();
			s.p4.connectSlotsByName();
			//s.timeTable.connectSlotsByName();
			s.cStatus.setText(t.get(r).get(0).toString("user_status"));
			s.currClass.setText(t.get(r).get(0).toString("user_current_class"));
			s.curLocation.setText(t.get(r).get(0).toString("user_location"));
			
			String time[] = t.get(r).get(1).extractToArray("user_timetable");
			if( time.length > 2){
				s.p1.setText(time[0]);
				s.p2.setText(time[1]);
			}
		}
		
		Map<String, JSONhandler> u = model.retrieveAdmin();
		for( String r: u.keySet() ){
			AdminTabs s = new AdminTabs();
			s.setupUi(view.adminScrollWidget );
			
   		    s.fname.connectSlotsByName();
   		    s.lname.connectSlotsByName();
   		    s.id.connectSlotsByName();
			s.fname.setText(u.get(r).toString("admin_first_name") );
			s.lname.setText(u.get(r).toString("admin_last_name") );
			s.id.setText(u.get(r).toString("admin_id"));
			
			s.cStatus.connectSlotsByName();
			s.currclass.connectSlotsByName();
			s.location.connectSlotsByName();
			s.p1.connectSlotsByName();
			s.p2.connectSlotsByName();
			s.p3.connectSlotsByName();
			s.p4.connectSlotsByName();
			s.cStatus.setText(u.get(r).toString("admin_status"));
			s.currclass.setText(u.get(r).toString("admin_current_class"));
			s.location.setText(u.get(r).toString("admin_location"));
			
			String time[] = u.get(r).extractToArray("admin_timetable");
			if( time.length > 2){
				s.p1.setText(time[0]);
				s.p2.setText(time[1]);
			}
		}
		
		
		Map<String, JSONhandler> v = model.retrieveCourses();
		for( String r: v.keySet() ){
			CourseTabs s = new CourseTabs();
			s.setupUi(view.cScrollWidget );
   		    s.cname.connectSlotsByName();
   		    s.id.connectSlotsByName();
   		    s.period.connectSlotsByName();
   		    s.start.connectSlotsByName();
   		    s.location.connectSlotsByName();
   		    s.duration.connectSlotsByName();
   		    
			s.cname.setText(v.get(r).toString("class_name") );
			s.start.setText(v.get(r).toString("class_time_start") );
			s.id.setText(v.get(r).toString("_id") );
			s.duration.setText(v.get(r).toString("duration") );
			s.location.setText(v.get(r).toString("class_location") );
			s.period.setText(v.get(r).toString("period") );
		}
	}
	
	/**
	 * The method creates and displays the UI
	 */
	public void activate(String args[]){
		//this.setUP();
		view.activate(args);
		this.display();
		QApplication.execStatic();
	}
	
	public static void main(String args[]){
		Controller t = Controller.getInstance();
		t.activate(args);
	}
	
}




/*
 * 	IMPLEMENT THE SEARCH FUNCTIONALITY
 *  ADD THE MAIN UI WINDOW TO THE VIEW PACKAGE
 *  EDIT THE CONFIG DATABASE AND USE A SPECIFIC NUMBER OF PERIODS
 *  ADD METHOD IN THE CONFIG FILE THAT RETURNS THE START TIME FOR EACH PERIOD
 *  EDIT THE OTHER UI PAGES TO REFLECT THE CHANGES IN THE DB
 *  CREATE METHODS IN THE MODEL THAT WILL UPDATE 
 * 
 */










