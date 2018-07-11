package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.ArrayList;

public class DemoFleet extends Fleet 
{   
    public static int FLEET_SIZE = 2;
    	
	//Arrays that contain information about the environment cells
	public static ArrayList<int[]> refillStations = new ArrayList<int[]>();
	public static ArrayList<int[]> wells = new ArrayList<int[]>();
	public static ArrayList<int[]> stations = new ArrayList<int[]>();
	public static ArrayList<int[]> jobs = new ArrayList<int[]>();
	public static ArrayList<int[]> tankerInformation = new ArrayList<int[]>();
	
	static Point closestWell, closestRefill, closestStation;

	public static int tankerID = 0, moveTowards;
	
	static String refuelAction = "toFuelPump", stationAction = "toStation", wellAction = "toWell";
	
	public DemoFleet() 
	{
		for (int i = 0; i < FLEET_SIZE; i++) 
		{
		    this.add(new DemoTanker());	
		    //Used to create unique tanker id that helps to recognize tanker
		    tankerID++;
		}
    }      

    public static Action fleetControl(DemoTanker tanker)
    {
    	//Retrieve tanker information that is calling control
    	Point tankerCords = new Point(tanker.tanker_X, tanker.tanker_Y);
    	
    	//Retrieve points of closest well and refill station to him
    	Point closestWell = Logic.closestWell(tankerCords);
		Point closestRefill = Logic.closestRefill(tankerCords);
		
     	Action action = null;

     	//If there exists task in the job array
		if(DemoFleet.jobs.isEmpty() == false)
	    {
			closestStation = JobSelect.findClosestStation(tanker);
			
			if(closestStation != null)
			{
				int distanceToNewJob = Logic.distanceComputation(tanker.tankerCords, closestStation);
				int jobIndex = Logic.retrieveJobIndex(closestStation);
				int[] jobWaste = DemoFleet.jobs.get(jobIndex);
				
				if(tanker.chosenTask != -1)
				{
					 int[] taskInformation = DemoFleet.jobs.get(tanker.chosenTask);
					 Point currentJob = new Point(taskInformation[0], taskInformation[1]);

					 int distanceToCurrentJob = Logic.distanceComputation(tankerCords, currentJob);
					 
					 
					 	//If distance to new job 
						if(distanceToNewJob < distanceToCurrentJob)
						{
								 for(int i = 0; i < FLEET_SIZE; i++)
						     	 {
									 //Retrieve other tanker information
									 int[] fleetInformation = DemoFleet.tankerInformation.get(i);
								 
									 //Make sure that we are not refering to this tanker and we have chosen other tanker from list
									 if(fleetInformation[0] != tanker.tankerID)
									 {
										 //Retrieve other tankers coordinates
										 Point fleetTankerCoordinates = new Point(fleetInformation[1], fleetInformation[2]);
										 int fleetDistanceToNewJob = Logic.distanceComputation(fleetTankerCoordinates, closestStation);
										  
										 //Check if it has a job
										 if(fleetInformation[3] != -1)
										 {
											 //Retrieve job coordinates and information
											 int[] fleetTaskInformation = DemoFleet.jobs.get(fleetInformation[3]);
											 Point fleetTaskCoordinates = new Point(fleetTaskInformation[0], fleetTaskInformation[1]);
											 
											 //Calculate the distance from that tanker to his job
											 int fleetDistanceToTheirJob = Logic.distanceComputation(fleetTankerCoordinates, fleetTaskCoordinates);
	
											 	//Compare if the new tasks distance is closer than other tankers original task and compare if he is closer than this tanker
												 if(fleetDistanceToNewJob < distanceToNewJob && fleetDistanceToNewJob < fleetDistanceToTheirJob)
												 {
													 //If yes -> assign new task to other tanker, unassign him from his original task and update his tanker information
													int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], fleetInformation[0]};
													DemoFleet.jobs.set(jobIndex, UpdateTask_2);
													    
													int[] UpdateTask = new int[]{fleetTaskInformation[0], fleetTaskInformation[1], fleetTaskInformation[2], -1};
													DemoFleet.jobs.set(fleetInformation[3], UpdateTask);
													
													int[] tankerInformation = new int[]{i, fleetInformation[1], fleetInformation[2], jobIndex};
												    DemoFleet.tankerInformation.set(i, tankerInformation);
												 }
												 //If not -> unassign this tanker from his original task, assign the new task and update his information
												 else
												 {
													int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], tanker.tankerID};
													DemoFleet.jobs.set(jobIndex, UpdateTask_2);
														    
													int[] UpdateTask = new int[]{taskInformation[0], taskInformation[1], taskInformation[2], -1};
													DemoFleet.jobs.set(tanker.chosenTask, UpdateTask);
														
													int[] tankerInformation = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, jobIndex};
													DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformation);
												 }
										 }
										 else
										 {
											 //If other tanker has no task -> compare his distance to new task with this tankers distance
											 if(fleetDistanceToNewJob < distanceToNewJob)
											 {
												 //If other tanker is closer -> assign him the task
												 int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], fleetInformation[0]};
												 DemoFleet.jobs.set(jobIndex, UpdateTask_2);
													
												 int[] tankerInformation = new int[]{i, fleetInformation[1], fleetInformation[2], jobIndex};
												 DemoFleet.tankerInformation.set(i, tankerInformation);
											 }
											 else
											 {
												//If not -> unassign this tanker from his original task and assign him the new task, update tankers information
												int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], tanker.tankerID};
												DemoFleet.jobs.set(jobIndex, UpdateTask_2);
													    
												int[] UpdateTask = new int[]{taskInformation[0], taskInformation[1], taskInformation[2], -1};
												DemoFleet.jobs.set(tanker.chosenTask, UpdateTask);
													
												int[] tankerInformation = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, jobIndex};
												DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformation);
											 }
										 }
									 }
						     	 }
						}
				}
				else if(tanker.chosenTask == -1)
				{
					for(int i = 0; i < FLEET_SIZE; i++)
		     		{
						int[] fleetInformation = DemoFleet.tankerInformation.get(i);
						
						if(fleetInformation[0] != tanker.tankerID)
						{
							Point fleetTankerCoordinates = new Point(fleetInformation[1], fleetInformation[2]);
							int fleetDistanceToNewJob = Logic.distanceComputation(fleetTankerCoordinates, closestStation);
							 
							if(fleetInformation[3] != -1)
							{
								 int[] fleetTaskInformation = DemoFleet.jobs.get(fleetInformation[3]);
								 Point fleetTaskCoordinates = new Point(fleetTaskInformation[0], fleetTaskInformation[1]);
								 
								 int fleetDistanceToTheirJob = Logic.distanceComputation(fleetTankerCoordinates, fleetTaskCoordinates);
			
								 if(fleetDistanceToNewJob < distanceToNewJob && fleetDistanceToNewJob < fleetDistanceToTheirJob)
								 {
									int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], fleetInformation[0]};
									DemoFleet.jobs.set(jobIndex, UpdateTask_2);
									    
									int[] UpdateTask = new int[]{fleetTaskInformation[0], fleetTaskInformation[1], fleetTaskInformation[2], -1};
									DemoFleet.jobs.set(fleetInformation[3], UpdateTask);
									
									int[] tankerInformation = new int[]{i, fleetInformation[1], fleetInformation[2], jobIndex};
								    DemoFleet.tankerInformation.set(i, tankerInformation);
								 }
								 else
								 {
									int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], tanker.tankerID};
									DemoFleet.jobs.set(jobIndex, UpdateTask_2);
										
									int[] tankerInformation = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, jobIndex};
									DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformation);
								 }
							 
								
							}
							else
							{
								 if(fleetDistanceToNewJob < distanceToNewJob)
								 {
									int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], fleetInformation[0]};
									DemoFleet.jobs.set(jobIndex, UpdateTask_2);
										
									int[] tankerInformation = new int[]{i, fleetInformation[1], fleetInformation[2], jobIndex};
									DemoFleet.tankerInformation.set(i, tankerInformation);
								 }
								 else
								 {
									int[] UpdateTask_2 = new int[]{closestStation.row, closestStation.column, jobWaste[2], tanker.tankerID};
									DemoFleet.jobs.set(jobIndex, UpdateTask_2);
										
									int[] tankerInformation = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, jobIndex};
									DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformation);
								 }
								 
							}
						}
		     		}
				}
			}
			
			//Update tanker information 
			int[] tankerUpdated = DemoFleet.tankerInformation.get(tanker.tankerID);
				
			//Tanker has a job / got a job after update
			if(tankerUpdated[3] != -1)				
			{
				//Retrieve task information
				int[] taskInformation = DemoFleet.jobs.get(tankerUpdated[3]);
				Point tankerJobCoordinates = new Point(taskInformation[0], taskInformation[1]);
					
					//Check if tanker has waste
					if(tanker.getWasteLevel() == 0)
					{		
						if( (tanker.getFuelLevel() - Logic.halfPathCost(tankerCords, tankerJobCoordinates)) > 2)
						{
							action = Logic.retrieveActions(tanker, tankerJobCoordinates, stationAction);
						}
						else
						{
							action = Logic.retrieveActions(tanker, closestRefill, refuelAction);
						}
					}
					else
					{
						int costToSecondStation = Integer.MIN_VALUE;
						
						//Calculate the cost of going to second station
						if( tanker.getWasteLevel() != tanker.MAX_WASTE )
	            		{
	            			costToSecondStation = tanker.getFuelLevel() - Logic.halfPathCost(tankerCords, tankerJobCoordinates);
	            		}
						//If tanker can go to second station - move towards it
						if(costToSecondStation != Integer.MIN_VALUE && tanker.getWasteLevel() != tanker.MAX_WASTE && costToSecondStation > 2)
						{
							action = Logic.retrieveActions(tanker, tankerJobCoordinates, stationAction);
						}
						//If not - decide whether to move to well or refuel station
						else
	            		{
	            			if( (tanker.getFuelLevel() - Logic.halfPathCost(tankerCords, closestWell)) > 2)
	                        {
	            				//Move towards closest well
	            				action = Logic.retrieveActions(tanker, closestWell, wellAction); 
	                        }
	                       	else
	                       	{
	                       		//Move towards closest refill station
	                       		action = Logic.retrieveActions(tanker, closestRefill, refuelAction); 
	                       	}
	            		}	
					}
			}
			//If after update tanker has no task assigned
			else if(tankerUpdated[3] == -1)
			{
					//And has no waste
					if(tanker.getWasteLevel() == 0)
					{
						//Explore the map
						action = Logic.exploreMap(tanker);
					}
					else
					{
						//Else - check if he can move towards well -> if not, refuel, check for new tasks and if nothing is found -> move towards well
						if( (tanker.getFuelLevel() - Logic.halfPathCost(tankerCords, closestWell)) > 2)
	                    {
							action = Logic.retrieveActions(tanker, closestWell, wellAction); 
	                    }
	                   	else
	                   	{
	                   		action = Logic.retrieveActions(tanker, closestRefill, refuelAction); 
	                   	}
					}
			}
	    }

		else
		{
			action = Logic.exploreMap(tanker);
		}
	
		return action;
    }

}
