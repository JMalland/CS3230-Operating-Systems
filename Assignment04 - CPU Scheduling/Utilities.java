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

		for (Process p : endProcesses) {
			sumTurnAroundTime += p.completionTime - p.arrivalTime;
			sumWaitingTime += p.completionTime - p.arrivalTime - p.burstTime;
			sumUtil += p.burstTime;
			sumResp += p.responseTime;
		}

		double cpuUtilization = sumUtil / (contextSwitch * 0.1 + sumUtil);
		double throughput = (double) endProcesses.size() / (double) timer;
		double avgResponseTime = (sumResp) / endProcesses.size();
		double avgWaitingTime = sumWaitingTime / endProcesses.size();
		double avgTurnAroundTime = sumTurnAroundTime /endProcesses.size();

		System.out.println("");
		System.out.println("CPU Utilization: " + cpuUtilization);
		System.out.println("Throughput: " + throughput);
		System.out.println("Average Response Time: " + avgResponseTime);
		System.out.println("Average Waiting Time: " + avgWaitingTime);
		System.out.println("Average Turnaround Time: " + avgTurnAroundTime);
	}
}