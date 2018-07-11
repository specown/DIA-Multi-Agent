package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.Random;


/**
 * A simple example Tanker
 * 
 * @author Julian Zappala
 */
/*
 * Copyright (c) 2011 Julian Zappala
 * 
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class DemoTanker extends Tanker 
{
	//Initialize cells
	Cell[][] cellInside;
	
	//Tanker Point
	Point tankerCords;
	
	//Tanker information variables
	public int tankerID, tanker_X, tanker_Y, chosenTask;
	
	public DemoTanker()
	{
		this(new Random());
		
		//Initial tanker coordinates - (0, 0)
		tankerCords = new Point(0,0);
		
		//retrieve tankerID from the control
		tankerID = DemoFleet.tankerID;
	
		//Retrieve X/Y from tankerCords
	  	tanker_X = tankerCords.row;
	  	tanker_Y = tankerCords.column;
	  	
	  	//Initial chosen task is -none
	  	chosenTask = -1;
	  	
	  	//Add initial tanker information to ArrayList in control
	  	int[] tankerInformation = new int[]{tankerID, tanker_X, tanker_Y, chosenTask};
	  	DemoFleet.tankerInformation.add(tankerInformation);
	}

    public DemoTanker(Random r) 
    {
	this.r = r;
    }
    
    //Gets environment data and stores it in ArrayLists
    private void environmentData()
    {      
    	
    	//Map is a square meaning row/column are equal to cells
    	for(int row=0; row<cellInside.length; row++)
    	{
    		for(int column=0; column<cellInside.length; column++)
    		{
    			
    			//Put Row/Column into Cell
    			Cell currentCell = cellInside[row][column];
    			
    			int x_cord = row + tanker_X - Tanker.VIEW_RANGE;
    			int y_cord = tanker_Y - column + Tanker.VIEW_RANGE;
    			
    			
    			int[] position = new int[]{x_cord, y_cord};
    			
    				//Check the type of the cell 
    				if(currentCell instanceof Station)
	    			{
    					//Check if such position already exists in public ArrayList
    					if(Logic.checkArray(DemoFleet.stations, position) == false)
    					{
    						DemoFleet.stations.add(position);
    					}
    					
    					Station station = (Station) currentCell;
    					Task job = station.getTask();
    					
    					//Check if cell has a task
    					if(job != null && job.isComplete() == false)
    					{
    						//If it does have a task - put task information into public ArrayList and assign it to -none
    						int [] taskInformation = new int[]{x_cord, y_cord, job.getWasteAmount(), -1};
    						
    						if(Logic.checkArray(DemoFleet.jobs, taskInformation) == false)
    						{
    							DemoFleet.jobs.add(taskInformation);
    						}
    						
    					}
    				}	
    				else if(currentCell instanceof Well)
	    			{
    					if(Logic.checkArray(DemoFleet.wells, position) == false)
    					{
    						DemoFleet.wells.add(position);
    					}
	    			}
    				else if(currentCell instanceof FuelPump)
	    			{
    					if(Logic.checkArray(DemoFleet.refillStations, position) == false)
    					{
    						DemoFleet.refillStations.add(position);
    					}
	    			}
    		}
    	}
   }
    

    public Action senseAndAct(Cell[][] view, long timestep) 
    {
    	//Retrieve tanker information from arrayList in control
    	int[] tankerInformation = DemoFleet.tankerInformation.get(this.tankerID);
    	
    	/*Retrieve current position from GUI
    	 *I used this method to make sure that tankers coordinates are the actual coordinates on GUI 
    	 *it helps to protect from ActionFailure and make sure that tankers coordinates remain as they are 
    	 *since I use my own 'Point' class and .getPosition only works with original Point - I use my own method 
    	 *to cut those coordinates and retrieve the actual numbers from string
    	 */
    	String positionInString = this.getPosition().toString();
    	positionInString = positionInString.substring(positionInString.indexOf("(")+1,positionInString.indexOf(")"));
    	this.tanker_X = Integer.parseInt(positionInString.substring(0, positionInString.indexOf(",")));
    	this.tanker_Y = Integer.parseInt(positionInString.substring(positionInString.indexOf(",") + 2));
    	this.chosenTask = tankerInformation[3];
    	
    	//Double check if the task actually exists -> if not, then unassign task from tanker
    	if(chosenTask != -1 && DemoFleet.jobs.get(this.chosenTask) == null)
    	{
    		tankerInformation = new int[]{this.tankerID, this.tanker_X, this.tanker_Y, -1};
    	} 
    	else
    	{
    		tankerInformation = new int[]{this.tankerID, this.tanker_X, this.tanker_Y, this.chosenTask};
    	}

    	//Update the information in control
    	DemoFleet.tankerInformation.set(this.tankerID, tankerInformation);
    	
    	cellInside = view;
    	Action action = null;

    	//Scan the environment for new data
    	environmentData();

    	//Retrieve what action the tanker should do from control
    	action = DemoFleet.fleetControl(this);
    	
    	return action;
    }
}
