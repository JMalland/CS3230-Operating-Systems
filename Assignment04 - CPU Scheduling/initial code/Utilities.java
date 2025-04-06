import java.util.*;

// Source code
public class Utilities {

	ArrayList<Process> endProcesses;
	int contextSwitch;
	int timer;
	public Utilities(ArrayList<Process> endProcesses, int contextSwitch, int timer) {
		this.endProcesses = endProcesses;
		this.contextSwitch = contextSwitch;
		this.timer = timer;
	}

	public void calUtilities()
	{
		double sumTurnAroundTime = 0.0;
		double sumWaitingTime = 0.0;
		double sumUtil = 0.0;
		double sumResp = 0.0;
		
	/********************************************************************
	*   Calculate the statistic data
	*	1. CPU Utilization = sum of burst time/( sum of contextSwitch * 0.1 + sum of burst)
	*	2. Throughput = processes / timer
	*	3. Average Waiting Time = sum of all wait / processes
	*	4. Average Turnaround Time = sum of all turnaround / processes
	*
	*
	*********************************************************************/

		System.out.println("");
		System.out.println("CPU Utilization: " + cpuUtilization);
		System.out.println("Throughput: " + throughput);
		System.out.println("Average Response Time: " + avgResponseTime);
		System.out.println("Average Waiting Time: " + avgWaitingTime);
		System.out.println("Average Turnaround Time: " + avgTurnAroundTime);
	}
}