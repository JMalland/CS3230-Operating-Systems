
class Ticket
{
	public volatile int value = 0;
}

class Arbitrator
{
	private int numNodes = 0; 
	private Ticket[] ticket = null;
	
	public Arbitrator(int numNodes)
	{
		this.numNodes = numNodes;
		ticket = new Ticket[numNodes];
		for (int i = 0; i < numNodes; i++)
			ticket[i] = new Ticket();
	}
	

	private int maxx(Ticket[] ticket)
	{
		int mx = ticket[0].value;
		for (int i = 1; i < ticket.length; i++)
			if (ticket[i].value > mx)
				mx = ticket[i].value;
		return mx;
	}
	

	public void wantToEnterCS(int i) 
	{
		ticket[i].value = 1;
		ticket[i].value = 1 + maxx(ticket); 
		for (int j = 0; j < numNodes; j++)
			if (j != i)
				while (!(ticket[j].value ==0 || ticket[i].value < ticket[j].value
					||//break a tie
					(ticket[i].value == ticket[j].value && i<j)))
					Thread.currentThread().yield();
	}

	public void finishedInCS(int i)
	{
		ticket[i].value = 0;
	}
}
