import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
//import java.lang.Runtime;

//import java.io.FileWriter;

public class Model 
{
	public static long filterSum=0,updateSum=0;
	public static int node=0;
	public static long timeUpBound=600000;
	public static String satisfaction="--";
	
       Relation rs[]=null;
       int rnum=0;
       
       Domain   ds[]=null;
       int dnum=0;
       
       Variable vs[]=null;
       int vnum=0;
       
       Constraint cs[]=null;
       int cnum=0;
       
       int maxd=0;
       
       int maxr=0;
       
       boolean short_state=false;
       
       Relation rcur[]=null;
       int rnumcur=0;
       //Nodebox inrcur=new Nodebox();
      
       int RN=0;
       
       public Model(String name)
       {   	 
    	   try
     	  {
     		  File f = new File(name); 
             
     		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
     		  DocumentBuilder builder = factory.newDocumentBuilder(); 
     		  Document doc = builder.parse(f); 
     		  
     		  
     		  NodeList cl=doc.getElementsByTagName("constraint");  
     		  cnum=cl.getLength();     
     		  cs=new Constraint[cnum];   
     		  
     		  ////////////////////////////////////////////////////////////
     		  NodeList dl= doc.getElementsByTagName("domain");
     		  dnum=dl.getLength();
     		  ds=new Domain[dnum];

     		  
     		  for(int i=0;i<dnum;i++)
     		  {
         		  String d=dl.item(i).getTextContent();
         		  int num=Integer.parseInt(dl.item(i).getAttributes().item(1).getNodeValue());

         		  String ss=dl.item(i).getAttributes().item(0).getNodeValue();
         		  
         		  if(num>maxd)
         		  {
         			  maxd=num;
         		  }
         		  
         		  ds[i]=new Domain(num,d,i,ss);
         		 // System.out.println(num);
     		  }

     		  ///////////////////////////////////////////////////////////

     		  NodeList vl= doc.getElementsByTagName("variable");
     		  vnum=vl.getLength();
     		  vs=new Variable[vnum];
  
              for(int i=0;i<vnum;i++)
              {
            	  String ss=vl.item(i).getAttributes().item(0).getNodeValue();
            	  String name1=vl.item(i).getAttributes().item(1).getNodeValue();
            	  int d=0;
            	             	  
            	  for(;d<dnum;d++)
            	  {
            		  if(ds[d].name.equals(ss))
            		  {
            			  break;
            		  }
            	  }
            	  
            	  vs[i]=new Variable(ds[d],i,cnum,name1);
              }   		  
     		  

              ////////////////////////////////////////////////// 
     		  
     		  NodeList rl = doc.getElementsByTagName("relation");
     		  rnum=rl.getLength();
     		  rs=new Relation[rnum];
     		  
     		  for(int i=0;i<rnum;i++)
     		  {
         		  String text=rl.item(i).getTextContent();
         		  String intype=rl.item(i).getAttributes().item(3).getNodeValue();
         		  int inrnum=Integer.parseInt(rl.item(i).getAttributes().item(2).getNodeValue());
         		  int invnum=Integer.parseInt(rl.item(i).getAttributes().item(0).getNodeValue());
         		  
         		  String name1=rl.item(i).getAttributes().item(1).getNodeValue();
  
         		  rs[i]=new Relation(inrnum,invnum,text,intype,i);
         		  rs[i].name=name1;
         		  //System.out.println(text);
     		  }

     		  //System.out.println(text);

   
     		  ////////////////////////////////////////////////////////////    		  
     		  rcur=new Relation[cnum];
     		  
     		  for(int i=0;i<cnum;i++)
     		  {
     			  int invnum=Integer.parseInt(cl.item(i).getAttributes().item(0).getNodeValue());
     			  
     			  String rname=cl.item(i).getAttributes().item(2).getNodeValue();
     			  int inr=0;
     			  
     			  for(;inr<rnum;inr++)
     			  {
     				  if(rs[inr].name.equals(rname))
     				  {
     					  break;
     				  }
     			  }
     			  
     			  
     			  String s[]=cl.item(i).getAttributes().item(3).getNodeValue().split(" ");
     			  Variable invs[]=new Variable[invnum];
     			  
     			  for(int j=0;j<invnum;j++)
     			  {
     				  int v=0;
     				  
     				  for(;v<vnum;v++)
     				  {
     					  if(s[j].equals(vs[v].name))
     					  {
     						  break;
     					  }
     				  }
     				  
     				  invs[j]=vs[v];
     			  }
     			  
     			  
     			  cs[i]=new Constraint(invnum,rs[inr],invs,i,this);
     			  
         		  if(cs[i].rnum>maxr)
         		  {
         			  maxr=cs[i].rnum;
         		  }
     			  
     			  //System.out.println(invs[2]);
     		  }

     		  /////////////////////////////////////////////////////////////
     		  rs=new Relation[rnumcur];
     		  for(int i=0;i<rnumcur;i++)
     		  {
     			  rs[i]=rcur[i];
     		  }
     		  rnum=rnumcur;
     		  
     	  }
     	  catch(Exception e)
     	  {
     		  e.printStackTrace();
     		  System.out.println("aaa"+ e.getMessage());
     	  }
       }
       
       public static void main(String[] args)
       {
    	   try {
    		   File file = new File("f:/benchmarkBinary");
          	   File[] ff = file.listFiles();
          	   for (int ii=0;ii<ff.length;ii++)
          	   {
          		   if(ff[ii].isDirectory())
          		   {
          			   String path = ff[ii].getPath();
          			   //System.out.println(path);
          			   File ffile = new File(path);
          			   String ssname[]=ffile.list();
          			   WritableWorkbook book = Workbook.createWorkbook(new File("G:/OneDrive/CPresult/"+ff[ii].getName()+".xls"));
          			   WritableSheet sheet = book.createSheet(ff[ii].getName(), ii);
          			   
          			   for (int i = 0; i < ssname.length; i++)
          			   {
          				   System.out.println(ssname[i]);
          				   String name=path+"\\"+ssname[i];
          				   Label label = new Label(0, i, ssname[i]);
          				   sheet.addCell(label);
              			   
              			   
              			   
              			   /*{
              			   Model mm=new Model(name);
              	      	   DATA data=new DATA(mm);
              	      	   node=0;
              	           STR3new str=new STR3new(data);
              	      	   long time1=System.currentTimeMillis();
              	      	   str.MAC();
              	      	   long time2=System.currentTimeMillis();
              	      	   long result=time2-time1;
              	      	   str=null;
              	      	   data=null;
              	      	   mm=null;
              	      	   System.out.println("node: "+node);
              	      	   System.out.println("runTime: "+result);
              	      	   jxl.write.Number jNode = new jxl.write.Number(5, i, node);
              	      	   sheet.addCell(jNode);
              	      	   jxl.write.Number jResult = new jxl.write.Number(11, i, result);
              	      	   sheet.addCell(jResult);
              			   }
          				   
              			   {
              			   Model mm=new Model(name);
              	      	   DATA data=new DATA(mm);
              	      	   node=0;
              	           STR2new str=new STR2new(data);
              	      	   long time1=System.currentTimeMillis();
              	      	   str.MAC();
              	      	   long time2=System.currentTimeMillis();
              	      	   long result=time2-time1;
              	      	   str=null;
              	      	   data=null;
              	      	   mm=null;
              	      	   System.out.println("node: "+node);
              	      	   System.out.println("runTime: "+result);
              	      	   jxl.write.Number jNode = new jxl.write.Number(3, i, node);
              	      	   sheet.addCell(jNode);
              	      	   jxl.write.Number jResult = new jxl.write.Number(9, i, result);
              	      	   sheet.addCell(jResult);
              			   }*/
              			   
              			   
              			   //////////////////////////////////////////////////////
              			   //calculate parameter
          				   
              			   /*{
              			   Model mm=new Model(name);
              	      	   DATA data=new DATA(mm);
              	      	   node=0;
              	           para_adaptSTR str=new para_adaptSTR(data);
              	      	   long time1=System.currentTimeMillis();
              	      	   str.MAC();
              	      	   long time2=System.currentTimeMillis();
              	      	   long result=time2-time1;
              	      	   str=null;
              	      	   data=null;
              	      	   mm=null;
              	      	   
              	      	   double proportion=(double)para_adaptSTR.stra1/((double)(para_adaptSTR.stra1+para_adaptSTR.stra2));
              	      	   double avgP=(double)para_adaptSTR.avgTuple/(double)para_adaptSTR.avgIniTuple;
              	      	   
              	      	   System.out.println("node: "+node);
              	      	   System.out.println("runTime: "+result);
              	      	   System.out.println(para_adaptSTR.stra1+"+"+para_adaptSTR.stra2+"="+para_adaptSTR.count);
              	      	   System.out.println("proportion: "+proportion);
              	      	   System.out.println("avgDiffer: "+para_adaptSTR.avgDiffer);
              	      	   System.out.println("avgTuple: "+para_adaptSTR.avgTuple+" avgIniTuple: "+para_adaptSTR.avgIniTuple);
              	      	   System.out.println("avgP: "+avgP);
              	      	   
              	      	   jxl.write.Number jNode = new jxl.write.Number(2, i, node);
              	      	   sheet.addCell(jNode);
              	      	   jxl.write.Number jResult = new jxl.write.Number(3, i, result);
              	      	   sheet.addCell(jResult);
              	      	   jxl.write.Number jProportion = new jxl.write.Number(5, i, proportion);
              	      	   sheet.addCell(jProportion);
              	      	   jxl.write.Number javgDiffer = new jxl.write.Number(6, i, para_adaptSTR.avgDiffer);
              	      	   sheet.addCell(javgDiffer);
              	      	   jxl.write.Number jstra1 = new jxl.write.Number(7, i, para_adaptSTR.stra1);
              	      	   sheet.addCell(jstra1);
              	      	   jxl.write.Number jstra2 = new jxl.write.Number(8, i, para_adaptSTR.stra2);
              	      	   sheet.addCell(jstra2);
              	      	   jxl.write.Number jcount = new jxl.write.Number(9, i, para_adaptSTR.count);
              	      	   sheet.addCell(jcount);
              	      	   jxl.write.Number javgP = new jxl.write.Number(11, i, avgP);
              	      	   sheet.addCell(javgP);
              	      	   jxl.write.Number javgTuple = new jxl.write.Number(12, i, para_adaptSTR.avgTuple);
              	      	   sheet.addCell(javgTuple);
              	      	   jxl.write.Number javgIniTuple = new jxl.write.Number(13, i, para_adaptSTR.avgIniTuple);
              	      	   sheet.addCell(javgIniTuple);
              			   }
              			   
              			   {
              			   Model mm=new Model(name);
              	      	   DATA data=new DATA(mm);
              	      	   node=0;
              	           para_STR2new str=new para_STR2new(data);
              	      	   long time1=System.currentTimeMillis();
              	      	   str.MAC();
              	      	   long time2=System.currentTimeMillis();
              	      	   long result=time2-time1;
              	      	   str=null;
              	      	   data=null;
              	      	   mm=null;
              	      	   
              	      	   System.out.println("node: "+node);
              	      	   System.out.println("runTime: "+result);
              	      	   System.out.println("count: "+para_STR2new.count);
              	      	   System.out.println("avgTuple: "+para_STR2new.avgTuple);
              	      	   System.out.println("avgSval: "+para_STR2new.avgSval);
              	      	   System.out.println("avgSsup: "+para_STR2new.avgSsup);
              	      	   
              	      	   jxl.write.Number jNode = new jxl.write.Number(16, i, node);
              	      	   sheet.addCell(jNode);
              	      	   jxl.write.Number jResult = new jxl.write.Number(17, i, result);
              	      	   sheet.addCell(jResult);
              	      	   jxl.write.Number javgTuple = new jxl.write.Number(19, i, para_STR2new.avgTuple);
              	      	   sheet.addCell(javgTuple);
              	      	   jxl.write.Number javgSval = new jxl.write.Number(20, i, para_STR2new.avgSval);
              	      	   sheet.addCell(javgSval);
              	      	   jxl.write.Number javgSsup = new jxl.write.Number(21, i, para_STR2new.avgSsup);
              	      	   sheet.addCell(javgSsup);
              	      	   
              	      	   jxl.write.Number jcount = new jxl.write.Number(23, i, para_STR2new.count);
              	      	   sheet.addCell(jcount);
              			   }
              	      	   
         				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	   STR2new str=new STR2new(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime: "+result);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(3, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(6, i, result);
                  	      	   sheet.addCell(jResult);
          				   }*/
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	      	satisfaction="--";
                  	      	   AC3_forScp str=new AC3_forScp(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(36, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(3, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(14, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(25, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(47, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	      	   AC3_vtoc str=new AC3_vtoc(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(37, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(4, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(15, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(26, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(48, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	      	   AC3_ctr str=new AC3_ctr(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(38, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(5, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(16, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(27, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(49, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	           AC3_divScp_R str=new AC3_divScp_R(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(39, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(6, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(17, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(28, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(50, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	           AC3_forScp_domv str=new AC3_forScp_domv(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(41, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(8, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(19, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(30, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(52, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	           AC3_vtoc_domv str=new AC3_vtoc_domv(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(42, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(9, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(20, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(31, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(53, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	           AC3_ctr_domv str=new AC3_ctr_domv(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(43, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(10, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(21, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(32, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(54, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   {
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	        satisfaction="--";
                  	           AC3_divScp_R_domv str=new AC3_divScp_R_domv(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("satisfaction: "+satisfaction);
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime:                "+result);
                  	      	   System.out.println("fiter: "+filterSum);
                  	      	   float perRevision=(float)filterSum/(float)node;
                  	      	   System.out.println("perRevision: "+perRevision);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(44, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(11, i, result);
                  	      	   sheet.addCell(jResult);
                  	      	   jxl.write.Number jFilterSum = new jxl.write.Number(22, i, filterSum);
                  	      	   sheet.addCell(jFilterSum);
                  	      	   jxl.write.Number jPerRevision = new jxl.write.Number(33, i, perRevision);
                  	      	   sheet.addCell(jPerRevision);
                  	      	   
                  	      	   Label jSatisfaction = new Label(55, i, satisfaction);
                  	      	   sheet.addCell(jSatisfaction);
          				   }
          				   
          				   
          				   /*{
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	filterSum=0; updateSum=0;
                  	      	   RPC_stamp_vtoc str=new RPC_stamp_vtoc(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime: "+result);
                  	      	System.out.println("fiter: "+filterSum);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(4, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(7, i, result);
                  	      	   sheet.addCell(jResult);
          				   }*/
          				   
          				   
          				   /*{
                  			   Model mm=new Model(name);
                  	      	   DATA data=new DATA(mm);
                  	      	   node=0;
                  	      	   filterSum=0; updateSum=0;
                  	      	FEDRGv2_avgArity fe=new FEDRGv2_avgArity(data,mm);
                  	      	   //AC3_variable_stamp_vtoc3 str=new AC3_variable_stamp_vtoc3(data);
                  	      	   long time1=System.currentTimeMillis();
                  	      	   //str.MAC();
                  	      	   long time2=System.currentTimeMillis();
                  	      	   long result=time2-time1;
                  	      	   //str=null;
                  	      	   data=null;
                  	      	   mm=null;
                  	      	   System.out.println("node: "+node);
                  	      	   System.out.println("runTime: "+result);
                  	      	   long total=filterSum+updateSum;
                  	      	   System.out.println("fiter: "+filterSum+"  update: "+updateSum+"  total: "+total);
                  	      	   jxl.write.Number jNode = new jxl.write.Number(4, i, node);
                  	      	   sheet.addCell(jNode);
                  	      	   jxl.write.Number jResult = new jxl.write.Number(7, i, result);
                  	      	   sheet.addCell(jResult);
          				   }*/
          			   }
          			   book.write();
          			   book.close();
          		   }
          	   }
    	   } catch(Exception e){
   			System.out.println(e);
   		}
    	   
       }
       
       
       
	   Model()
	   {
		   
	   }	   
}
