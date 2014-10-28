
public class node implements Comparable<node>{
	int xCor=0;
	int yCor=0;
	node parent=null;
	int pathCost=0;
	int pathLength=0;
	public node(int x, int y, node previous)
	{
		xCor=x;
		yCor=y;
		parent=previous;
	}
	public int getCost()
	{
		return pathCost;
	}
	//This method checks if, among its parents, the current point was already passed
	public boolean checkexists(int x, int y)
	{
		node cur=this.parent;
		while(cur!=null)
		{
			if(cur.xCor==x&&cur.yCor==y)
			{
				return false;
			}
			cur=cur.parent;
		}
		return true;
	}
	//this method is used to compute the primary reconvergence of 2 checkpoints,
	//at the point where both of these paths meet, what is the cost to travel from this point to the other
	public int earlistNodeDist(node compare)
	{
		node copy=this;
		node temp=compare;
		int a=0;
		int b=0;
		//while copy has a parent
		while(copy!=null)
		{
			//while temp has a parent
			while(temp!=null)
			{
				if(temp.xCor==copy.xCor&&temp.yCor==copy.yCor)
				{
					return a+b;
				}
				temp=temp.parent;
				b++;
			}
			copy=copy.parent;
			temp=compare;
			b=0;
			a++;
		}
		
		return 0;
	}
	@Override
	public int compareTo(node temp) {
		// TODO Auto-generated method stub
		return pathCost - temp.pathCost;
	}
	
}
