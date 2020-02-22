public class AC3_forScp_domv
{
	DATA data=null;
	
	int vdense[][]=null;
	int vcurlevel[][]=null;
	
	int level=0;
	
	int vcur[]=null;//变量[a]的论域大小
	
	int	levelvdense[]=null;
	int	levelvsparse[]=null;
	//
	int vtoc[][]=null;
	int vtocn[]=null;
	
	  long    vvT[]=null;
	  long	 cT[]=null;
	  long    backT=0;
	  int    heap[]=null;
	  int    heapn=0;
	  int    inheap[]=null;
	  
	int    assignedcount[]=null;
	int maxd=0;
	
	int scopes[][]=null;
	int scopeN[]=null;
	
	boolean relMap[][][][]=null;

	public AC3_forScp_domv(DATA indata)
	{
		data=indata;
		
		scopes=data.scopes;
		scopeN=data.scopeN;
		
		for(int i=0;i<data.varN;i++)
		{
			if(data.domn[i]>maxd)
			{
				maxd=data.domn[i];
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		vdense=new int[data.varN][];
		vcurlevel=new int[data.varN+1][data.varN];
		vcur=new int[data.varN];
		for(int i=0;i<data.varN;i++)
		{
			vdense[i]=new int[data.domn[i]];
			vcur[i]=data.domn[i]-1;
			
			for(int j=0;j<data.domn[i];j++)
			{
				vdense[i][j]=j;
			}
		}
		
		///////////////////////////////////////////////////////////////////
		assignedcount=new int[data.constraintN];

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		 levelvdense=new int[data.varN];
		 levelvsparse=new int[data.varN];
		 
		 for(int i=0;i<data.varN;i++)
		 {
			 levelvdense[i]=i;
			 levelvsparse[i]=i;
		 }	
		/////////////////////////////////////////////////////////////////////////////////////////////
	     heap=new int[data.varN];
	     inheap=new int[data.varN];
	     cT=new long[data.constraintN];
	     for(int i=0;i<data.constraintN;i++)
	     {
	    	 cT[i]=0;
	     }
	     vvT=new long[data.varN];
	     for(int i=0;i<data.varN;i++)
	     {
	    	 vvT[i]=1;
	     }
	     
		//
		vtocn=new int[data.varN];
		vtoc=new int[data.varN][];
		
		for(int i=0;i<data.constraintN;i++)
		{
			int x=data.scopes[i][0];
			int y=data.scopes[i][1];
			
			vtocn[x]++;
			vtocn[y]++;
		}
		
		for(int i=0;i<data.varN;i++)
		{
			vtoc[i]=new int[vtocn[i]];
			vtocn[i]=0;
		}
		
		for(int i=0;i<data.constraintN;i++)
		{
			int x=data.scopes[i][0];
			int y=data.scopes[i][1];
		
			vtoc[x][vtocn[x]]=i;
			vtoc[y][vtocn[y]]=i;
			
			vtocn[x]++;
			vtocn[y]++;
		}
		////////////////////////////////////////////////////
		relMap=new boolean[data.varN][data.varN][][];
		for(int i=0;i<data.constraintN;i++)
		{
			int rel[][]=data.relations[i];
			int x=data.scopes[i][0];
			int y=data.scopes[i][1];
			relMap[x][y]=new boolean[data.domn[x]][data.domn[y]];
			relMap[y][x]=new boolean[data.domn[y]][data.domn[x]];
			for(int j=0;j<rel[0].length;j++)
			{
				int a=rel[0][j];
				int b=rel[1][j];
				relMap[x][y][a][b]=true;
				relMap[y][x][b][a]=true;
			}
		}
	}
	
	boolean MAC()
	{
		  heapn=0;
		  backT=2;
		  for(int i=0;i<data.varN;i++)
		  {
			  addheap(i);
		  }
		
		level=0;
		
		int n=0;
		long time=0;
		long time1=System.currentTimeMillis();
		  
		while(level>=0)
		{
			  time=System.currentTimeMillis();
			  if((time-time1)>Model.timeUpBound)
			  {
				  Model.satisfaction="T/O";
				  Model.node=n;
				  return false;
			  }

			if(!AC())
			{
				  
				if(level==0)
				{
					//System.out.println("No");
					Model.satisfaction="no";
		          	Model.node=n;
					return false;
				}
				
				for(int i=0;i<data.varN;i++)
				{
					vcur[i]=vcurlevel[level][i];
				}
				level--;
				
				int v=levelvdense[level];
				
				while(vcur[v]==0)
				{
					if(level==0)
					{
						//System.out.println("No");
						Model.satisfaction="no";
						Model.node=n;
						return false;
					}
					
					for(int i=0;i<data.varN;i++)
					{
						vcur[i]=vcurlevel[level][i];
					}
					level--;
					
					v=levelvdense[level];
				}
				
				  
		          int a=vdense[v][vcur[v]];//???????????????????????vdense[v][vcur[v]];??
                  
                  vdense[v][vcur[v]]=vdense[v][0];
                  vdense[v][0]=a;
		          vcur[v]--;
		          
		          for(int i=0;i<vtocn[v];i++)
		          {
		        	  int c=vtoc[v][i];
		        	  assignedcount[c]--;
		          }
                  
		          addheap(v);
			}
			else
			{
				  n++;

				  double mindom=maxd+1;
				  int minvi=level;
				  int minv=levelvdense[level];
				  
				  for(int i=level;i<data.varN;i++)//data.varN
				  {
					   int v=levelvdense[i];
					   int ddeg=0;
					   
					   for(int j=0;j<vtocn[v];j++) //vtocN[v]
					   {
						   int cc=vtoc[v][j];//vtoc[v][j];
						   
						   /*boolean ff=false;
						   
						   for(int u=0;u<data.scopeN[cc];u++)//data.scopeN[cc]
						   {
							   if(vcur[data.scopes[cc][u]]!=0) //data.scopes[cc][u]
							   {
								   if(ff)
								   {
									   ddeg+=1;
									   break;
								   }
								   ff=true;
							   }
						   }*/
						   if(assignedcount[cc]+1<data.scopeN[cc])
						   {
							   ddeg+=1;							   
						   }
						   
					   }
					   
					   if(vcur[v]>0&&ddeg>0&&(1.0*(vcur[v]+1)/ddeg<mindom||(1.0*(vcur[v]+1)/ddeg==mindom&&v<minv)))
					   {
						   mindom=1.0*(vcur[v]+1)/ddeg;
						   minvi=i;
						   minv=v;
					   }
					   else if(mindom==maxd+1&&v<minv)
					   {
						   minvi=i;
						   minv=v;
					   }
				  }
				  
				  int a=levelvdense[level];
				  levelvdense[level]=levelvdense[minvi];
				  
				  levelvsparse[a]=minvi;
				  levelvsparse[levelvdense[minvi]]=level;
				  
				  levelvdense[minvi]=a;
				  
				  ///////////////////////////////////////////////////////////
		          int v=levelvdense[level];
		          
		          for(int i=0;i<vtocn[v];i++)
		          {
		        	  int c=vtoc[v][i];
		        	  assignedcount[c]++;
		          }
		          
		          level++;
		          if(level==data.varN)
		          {
		        	   //System.out.println("yes");
		        	   Model.satisfaction="yes";
		        	   /*for(int i=0;i<data.varN;i++)
		        	   {
		        		   int vv=levelvdense[i];
		        		   System.out.print("<"+"x"+vv+" "+vdense[vv][0]+">   ");
		        
		        	   }
		        	   
		        	   System.out.println("");
		           	   System.out.println("node: "+n);
		           	   */
		        	   Model.node=n;
		        	   return true;
		          }
		          
		          for(int i=0;i<data.varN;i++)
		          {
		        	  vcurlevel[level][i]=vcur[i];
		          }
		          
		          
                  if(vcur[v]>0)
		          {
       		    	  int val=maxd+1;
       		    	  int valn=0;
       		    	  for(int i=0;i<=vcur[v];i++)
       		    	  {
       		    		  if(vdense[v][i]<val)
       		    		  {
       		    			  valn=i;
       		    			  val=vdense[v][i];
       		    		  }
       		    	  }
       		    	  
       		    	  
    		          a=vdense[v][valn];
                      vdense[v][valn]=vdense[v][0];
                      vdense[v][0]=val;
                      
					  vcur[v]=0;
					  
					  addheap(v);
		          }
			  }
		          
		}
		Model.satisfaction="tailReturn";
		Model.node=n;
		return false;
	}
	
	  boolean AC()
	  {
		  while(heapn!=0)
		  {

			  int x=gethead();
			  
			  for(int i=0;i<vtocn[x];i++)
			  {
				  int c=vtoc[x][i];

				  if(vvT[x]<cT[c])
				  {
					  continue;
				  }
				  
				  for(int j=0;j<scopeN[c];j++)
				  {
					  int y=scopes[c][j];
					  int yy=scopes[c][1-j];
					  
					  if(levelvsparse[y]<level||(y==x&&vvT[yy]<cT[c]))
					  {
						  continue;
					  }
					  
					  if(revise(y,yy))
					  {
						  if(vcur[y]<0)
						  {
							  for(int k=0;k<heapn;k++)
							  {
								  int v=heap[k];
								  inheap[v]=-1;
							  }
							  heapn=0;
							  
							  return false;
						  }
						  
						  if(inheap[y]==-1)
						  {
							  addheap(y);
						  }
						  else
						  {
							  heapup(y);
						  }
						  
					  }
				  }
				  
				  cT[c]=backT;
				  backT++;
			  }
			  
		  }
		  return true;
	  }
	
	boolean revise(int x,int y)
	{
		int n=vcur[x];
		boolean rel[][]=relMap[x][y];
		
		Model.filterSum++;
		
		 for(int i=vcur[x];i>=0;i--)
		 {
			int a=vdense[x][i];
			
			int tt=seekSupport(rel,y,a);
			if(tt==-1)
			{
				vdense[x][i]=vdense[x][vcur[x]];
				vdense[x][vcur[x]]=a;
				
				vcur[x]--;
			}
		 }
		 return n!=vcur[x];
	}
	int seekSupport(boolean rel[][],int y,int a)
	{
		for(int j=0;j<=vcur[y];j++)
		{
			int b=vdense[y][j];
			if(rel[a][b])
			{
				return b;
			}
		}
		return -1;
	}
	
	void addheap(int v)
	{
		vvT[v]=backT;
		backT++;
		
		int p=heapn;
		int q=p;
		
		heapn++;
		
		while(p!=0)
		{
			p=(p-1)/2;
			int vv=heap[p];
			
			if(vcur[vv]<vcur[v])
			{
				break;
			}
			
			heap[q]=vv;
			inheap[vv]=q;

			q=p;
		}
		inheap[v]=q;
		heap[q]=v;
	}
	
	int gethead()
	{
		int a=heap[0];
		heapn--;
		
		heap[0]=heap[heapn];
		inheap[heap[heapn]]=0;
		heapdown(0);
		
		inheap[a]=-1;
		
		return a;
	}
	
	void heapup(int v)
	{
		vvT[v]=backT;
		backT++;
		
		int p=inheap[v];
		  
		int q=p;
		//System.out.println(y+" "+v);
		
		while(p>0)
		{
			p=(p-1)/2;
			int vv=heap[p];
			
			if(vcur[vv]<vcur[v])
			{
				break;
			}
			
			heap[q]=vv;
			inheap[vv]=q;
			q=p;
		}
		

		
		heap[q]=v;
		inheap[v]=q;
		
	}
	
	void heapdown(int p)
	{
		int v= heap[p];
	    int q=p;

		while(true)
		{
			q=p*2+1;
			
			if(q>heapn-1)
			{
				heap[p]=v;
				inheap[v]=p;
				break;
			}
			
			if(q==heapn-1)
			{
				
				int vv=heap[q];
				
				
				if(vcur[vv]>vcur[v])
				{
					heap[p]=v;
					inheap[v]=p;
					break;
				}
				
				heap[p]=vv;
				inheap[vv]=p;
				
				inheap[v]=q;
				heap[q]=v;
				break;
			}
			
			int vvv=heap[q+1];
			int vv=heap[q];
			
			if(vcur[vvv]<vcur[vv])
			{
				q=q+1;
				vv=vvv;
			}
			
			if(vcur[vv]>vcur[v])
			{
				heap[p]=v;
				inheap[v]=p;
				break;
			}
			
			heap[p]=vv;
			inheap[vv]=p;
			
			p=q;
		}
	}
}
