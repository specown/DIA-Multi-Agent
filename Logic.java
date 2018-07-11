package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.ArrayList;

public class Logic 
{
	 /*Distance computation - Max(Delta X/Y) 
	  *Where Delta X/Y - absolute value of the difference in the X & Y coordinates
	  */
	 public static int distanceComputation(Point first, Point second)
	 {
		 return Math.max(Math.abs(first.row-second.row), Math.abs(first.column-second.column));
	 }
	
	public static Point coordinatesOnMap( int x_cord, int y_cord, Point tankerCords )
	{
	        return new Point( tankerCords.row + x_cord, tankerCords.column + y_cord );
	}
	
	//Checks if the element already exists in the array
    public static boolean checkArray(ArrayList<int[]> currentList, int[] newListElement)
    {
    	int element=0;
    	//Create point for the element
    	Point newListPoint = new Point(newListElement[0], newListElement[1]);
    	
    	//While the array has elements
    	while(element < currentList.size())
    	{
    		//Retrieve array information and create a point out of that information
    		int[] listInformation = currentList.get(element);
    		Point currentListPoint = new Point(listInformation[0], listInformation[1]);
    		
    		//Check if the point that we are trying to add matches the one that we retrieved
    		if(newListPoint.comparePoints(currentListPoint) == true)
    		{
    			//If yes -> return true and do not add the element
    			return true;
    		}
    		
    		element++;
    	}
    	
    	return false;
    }
    

    //Path cost calculation from tanker->station->refill
    public static int halfPathCost(Point tankerCords, Point stationCoordinates)
	{
    	//Retrieve the closestRefill station from task coordinates
		Point refillPosition = closestRefill(stationCoordinates);

		int distance = distanceComputation(tankerCords, stationCoordinates) + distanceComputation(refillPosition, stationCoordinates);
		 
		return distance;
	}
     
	//Retrieves what action the tanker should do based on string
	public static Action retrieveActions(DemoTanker tanker, Point coordinates, String pathChose)
	{	
		Action action = null;
		Point tankerCords = new Point(tanker.tanker_X, tanker.tanker_Y);
		
		//Check if we have reached our target position 
		if(onLocation(tankerCords, coordinates) == false)
		{
			//If no -> then guide the tanker towards target position
			int moveTowards = directTarget(tankerCords, coordinates);
			action = new MoveAction(moveTowards);
			
			//Update tankers coordinates
			tankerMovementUpdate(tanker.tankerID, moveTowards);
		}
    	
	    	
	    	switch (pathChose)
			{
			 	case "toStation":
			 		//If we have reached our destination and tanker has a task assigned to him
			 		if(onLocation(tankerCords, coordinates) == true && tanker.chosenTask != -1)
				    {
			 			//Retrieve task from the current cell that the tanker is on
				 		Station station = (Station) tanker.getCurrentCell(tanker.cellInside);
			    		Task job = station.getTask();
			    		
			    		int[] tankerTaskInformation = DemoFleet.jobs.get(tanker.chosenTask);
			    		
			    		//Check if the job is not null
			    		if(job != null)
			    		{
			    			//Check if there will be waste remained in the task after tanker collects maximum possible amount
			    			if(job.getWasteAmount() - tanker.getWasteCapacity() > 0)
			    			{
			    				//Update waste information of the task
				    			int[] UpdateTask = new int[]{tankerTaskInformation[0], tankerTaskInformation[1], (job.getWasteAmount() - tanker.getWasteCapacity()), tanker.tankerID};
								DemoFleet.jobs.set(tanker.chosenTask, UpdateTask);
			    			}
			    			//Tanker will collect all the waste from the task
			    			else
			    			{
			    				//Create task point
				    			Point taskPoint = new Point(tankerTaskInformation[0], tankerTaskInformation[1]);
				    			//Check what is the index of the task in the Job Array
				    			int jobIndex = retrieveJobIndex(taskPoint);
				    			
				    			//Remove the job that tanker has chosen
				    		    DemoFleet.jobs.remove(tanker.chosenTask);
				    		    
				    		    //Scan all other tankers
				    			for(int i = 0; i < DemoFleet.FLEET_SIZE; i++)
					    		{
				    				//Make sure that we are not referring to current tanker
					    			if(i != tanker.tankerID)
					    			{
					    				//Retrieve other tanker information
					    				int[] fleetInformation = DemoFleet.tankerInformation.get(i);
		
					    				/* Check if he has a task and his task index is bigger than this tankers task index
					    				 * Since we used array.remove -> ID's have moved back from the one that we deleted
					    				 */
					    				if(fleetInformation[3] != -1 && fleetInformation[3] > jobIndex)
					    				{	
					    					//Update other tankers information
											int[] fleetInformationUpdate = new int[]{fleetInformation[0], fleetInformation[1], fleetInformation[2], (fleetInformation[3] - 1)};
										    DemoFleet.tankerInformation.set(fleetInformation[0], fleetInformationUpdate);
					    				}
					    			}
					    		}
				    			
				    			//Update this tankers information -> unassign him from task since he picked all the waste
								int[] tankerInformationUpdate = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, -1};
								DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformationUpdate);
			    			}
			    			
			    			//Load waste
			    			action = new LoadWasteAction(job);
			    		}
			    		//Job is null -> it should be removed from the arrayList and tanker information should be updated
			    		else
			    		{
			    			DemoFleet.jobs.remove(tanker.chosenTask);
			    			
			    			for(int i = 0; i < DemoFleet.FLEET_SIZE; i++)
				    		{
				    			if(i != tanker.tankerID)
				    			{
				    				int[] fleetInformation = DemoFleet.tankerInformation.get(i);
	
				    				if(fleetInformation[3] != -1 && fleetInformation[3] > tanker.chosenTask)
				    				{	
										int[] fleetInformationUpdate = new int[]{fleetInformation[0], fleetInformation[1], fleetInformation[2], (fleetInformation[3] - 1)};
									    DemoFleet.tankerInformation.set(fleetInformation[0], fleetInformationUpdate);
				    				}
				    			}
				    		}

							int[] tankerInformationUpdate = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, -1};
							DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformationUpdate);
			    		}
			    }

			 	break;
			 		
			 	case "toFuelPump":
			 		if(onLocation(tankerCords, coordinates) == true && tanker.getFuelLevel() != tanker.MAX_FUEL)
				    {
			 			//Check if tanker hasn't got max fuel
			 			if(tanker.getFuelLevel() != tanker.MAX_FUEL)
			 			{
			 				action = new RefuelAction();
			 			}
			 			//If he does have max fuel -> there is an issue after 'actionFailed'
			 			else
			 			{
			 				//Check if he has an order assigned and unassign him from it
			 				if(tanker.chosenTask != -1)
			 				{
			 					int[] tankerTaskInformation = DemoFleet.jobs.get(tanker.chosenTask);
			 					int[] UpdateTask = new int[]{tankerTaskInformation[0], tankerTaskInformation[1], tankerTaskInformation[2], -1};
			 					DemoFleet.jobs.set(tanker.chosenTask, UpdateTask);
			 				}
			 				
			 				//Update tanker inforamtion
			 				int[] tankerInformationUpdate = new int[]{tanker.tankerID, tanker.tanker_X, tanker.tanker_Y, -1};
			 				DemoFleet.tankerInformation.set(tanker.tankerID, tankerInformationUpdate);
			 			}
			 			
				    }
				break;
					
			 	case "toWell":
			 		if(onLocation(tankerCords, coordinates) == true)
				    {
			 			action = new DisposeWasteAction();
				    }
				break;
			}
	    			    
	    	//If action is null -> there was an issue after 'ActionFailed' -> tanker should move and restart himself.
			if(action == null)
			{
			  	int moveTowards = 0;
		    	action = new MoveAction(moveTowards);	
				tankerMovementUpdate(tanker.tankerID, moveTowards);
			}

		 return action;
	 }
	
	public static Action exploreMap(DemoTanker tanker)
	{	 
		 Action action = null;
		 
		 String refuelAction = "toFuelPump";
		 Point tankerCords = new Point(tanker.tanker_X, tanker.tanker_Y);
		 Point closestRefill = Logic.closestRefill(tankerCords);
		 
		 int pathToFuelStation = tanker.getFuelLevel() - Logic.distanceComputation(tankerCords, closestRefill);

		 if(pathToFuelStation > 2)
		 {
			 int moveTowards = 8;
			 
			 switch(tanker.tankerID)
			 {
			 		case 0:
			 			moveTowards = 2;
			 			break;
	            	
			 		case 1:
			 			moveTowards = 3;
		            	break;
		            	
			 		case 2:
			 			moveTowards = 0;
		            	break;
		            	
			 		case 3:
			 			moveTowards = 1;
		            	break;
			 }
			 
			 if(moveTowards == 8)
			 {
				 moveTowards = 0;
			 }
		    
			 action = new MoveAction(moveTowards);
			 
			 Logic.tankerMovementUpdate(tanker.tankerID, moveTowards);
		 }
		 else
		 {
			action = Logic.retrieveActions(tanker, closestRefill, refuelAction);
		 }
		 
		 return action;
	}
	
	//Used to update tankers coordinates after movement
	public static void tankerMovementUpdate(int tankerID, int moveTowards )
	{
		//Retrieve current tankre information
		int[] tankerInformation = DemoFleet.tankerInformation.get(tankerID);
		int row = tankerInformation[1];
		int column = tankerInformation[2];

	        switch( moveTowards )
	        {
	            case 0:
	            	column++;
	            	break;
	            case 1:
	            	column--;
	            	break;
	            case 2:
	            	row++;
	            	break;
	            case 3:
	            	row--;
	            	break;
	            case 4:
	            	column++;
	            	row++;
	            	break;
	            case 5:
	            	column++;
	            	row--;
	            	break;
	            case 6:
	            	row++;
	            	column--;
	            	break;
	            case 7:
	            	column--;
	            	row--;
	            	break;
	        }
	        
	        //Update tanker information in the Array
	        tankerInformation = new int[]{tankerID, row, column,tankerInformation[3]};
	        DemoFleet.tankerInformation.set(tankerID, tankerInformation);
	}

	//Checks if tanker has arrived to targets coordinates
	 public static boolean onLocation(Point tankerCords, Point targetCords)
	 {
		 //Check if coordinates match
		 if(tankerCords.comparePoints(targetCords))
		 {
			 return true;
		 }
		 return false;
	 }
	 
	 //Directs the tanker towards target coordinates
	 public static int directTarget(Point tankerCords, Point target)
	 {
		 	//Get X/Y to the target
	        int targetX = target.row - tankerCords.row;
	        int targetY =  target.column - tankerCords.column;
	        
	        int moveTowards = moveNavigation(targetX, targetY);
	        
	        return moveTowards;
	 }
	 
	 private static int moveNavigation(int targetX, int targetY)
	 {
	        int moveTowards = 8;
	        
	        if( targetY == 0 ){
	        	if( targetX == 0 )
	        	{
		            /*Arrived at the location
		             * X && Y == 0
		             */
		            	moveTowards = 8;
		        }
	        	
	        	else if( targetX > 0 ){
	            	//East
	            	moveTowards = 2;
	            }
	        	
	            else if( targetX < 0 ){
	            	//West
	            	moveTowards = 3;
	            }
	            
	        }
	        
	        else if( targetY > 0 )
	        {
	        	if( targetX == 0 ){
	            	//North
	            	moveTowards = 0;
	            }
	        	
	        	else if( targetX > 0 ){
	            	//NorthEast
	            	moveTowards = 4;
	            }
	            
	            else if( targetX < 0 ){
	            	//NorthWest
	            	moveTowards = 5;
	            }
	        }
	        
	         
	        else if( targetY < 0 ){
	            
	            if( targetX == 0 ){
	            	//South
	            	moveTowards = 1; 
	            }
	            
	            else if( targetX > 0 ){
	            	//SouthEast
	            	moveTowards = 6;
	            }
	            
	            else if( targetX < 0 ){
	            	//SouthWest
	            	moveTowards = 7;
	            }
	        }
	        
	        return moveTowards;
	 }
	 
	 //Return closest well to the tanker
	 public static Point closestWell(Point coordinates)
	 {
		int max = Integer.MAX_VALUE;
		int i = 0;
		 
		Point closestWellPosition = null;
		
		while(i < DemoFleet.wells.size())
	    {
			int[] wellInformation = DemoFleet.wells.get(i);
			Point wellPoint = new Point(wellInformation[0], wellInformation[1]);
			
			//Calculate the distance to the well
			int distance = distanceComputation(coordinates, wellPoint);
			
			//Check if distance is less than the previous distance
			if(distance < max)
			{
				max = distance;
				//If yes -> closest well station is the current one
				closestWellPosition = wellPoint;
			}
			
	    	i++;
	    }
		 return closestWellPosition;
	 }
	 
	 //Return closest refill station to the tanker
	 public static Point closestRefill(Point coordinates)
	 {
		int max = Integer.MAX_VALUE;
		
		int i = 0;
		 
		Point closestRefillPoint = null;
		
		while(i < DemoFleet.refillStations.size())
	    {
			int[] refilCoordinates = DemoFleet.refillStations.get(i);
			
			Point refilPoint = new Point(refilCoordinates[0], refilCoordinates[1]);
			
			int distance = distanceComputation(coordinates, refilPoint);
			
			if(distance < max)
			{
				max = distance;
				closestRefillPoint = refilPoint;
			}
			
	    	i++;
	    }
		 return closestRefillPoint;
	 }
	 
	 //Retrieve job index of the task
	 public static int retrieveJobIndex(Point station)
	 {
		 int i = 0, jobIndex = -1;
		 
		 while(i < DemoFleet.jobs.size())
	     {
			 	int[] tasks = DemoFleet.jobs.get(i);
			 	
			 	Point tasksPoint = new Point(tasks[0], tasks[1]);
			 	
			 	//Check if the task that we are currently checking matches the one that tanker is assigned to
			 	if(station.comparePoints(tasksPoint) == true)
			 	{
			 		//if yes -> retrieve that jobIndex
			 		jobIndex = i;
			 	}
			 	
			 	i++;
	    }
		 
		return jobIndex;
	 }
}

