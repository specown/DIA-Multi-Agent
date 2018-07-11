package uk.ac.nott.cs.g53dia.multidemo;

public class JobSelect 
{
	public static Point findClosestStation(DemoTanker tanker)
	{	
		Point closestStation = null;
		Point tankerCords = new Point(tanker.tanker_X, tanker.tanker_Y);
		
		//Find the closest refill station from current tanker coordinates
		Point closestRefill = Logic.closestRefill(tankerCords);
		
		int i = 0, minDistance = Integer.MAX_VALUE;;

		//Scan all jobs in array
		while(i < DemoFleet.jobs.size())
    	{
			//Retrieve job information
			int[] taskInformation = DemoFleet.jobs.get(i);
			
			//Check if job is not assigned to any tankerID
			if(taskInformation[3] == -1)
			{
				//Retrieve job position and create a point
				Point taskPosition = new Point(taskInformation[0], taskInformation[1]);
	
				//Path cost to go with current fuel level from tanker-> task -> refill station
				int pathCost = tanker.getFuelLevel() - Logic.halfPathCost(tankerCords, taskPosition);
				
				//Path cost to go from closest refill station with maximum fuel -> task. I use this to ensure that it is possible to get to task
				int fullCost = tanker.MAX_FUEL - Logic.distanceComputation(closestRefill, taskPosition);
				
				//Check if task has waste and tanker can possibly get to the task and still have reserve of fuel in case of action fail
				if(taskInformation[2] > 0 && pathCost > 2 && fullCost > 2)
				{
					//If there are more than one task available -> measure the distance
					if(DemoFleet.jobs.size() > 0 && Logic.distanceComputation(tankerCords, taskPosition) < minDistance)
					{
						//Measure the distance from tanker to task -> use this to compare distances to task
						minDistance = Logic.distanceComputation(tankerCords, taskPosition);
						closestStation = taskPosition;
					}
					//If there is only one job availabe -> return that job point
					else if(DemoFleet.jobs.size() == 0)
					{
						minDistance = Logic.distanceComputation(tankerCords, taskPosition);
						closestStation = taskPosition;
					}
				}
			}
    		i++;
    	}

		//Return closest task to the current tanker coordinates
		return closestStation;	
	}
}

