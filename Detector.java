import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Detector {
	static int totalResource; //# of total resource
	static int[] U;  //unused resouce list
	//process#i: Ai resourceHolding list
	static HashMap<Integer, int[]> processTable = new HashMap<Integer, int[]>();
	//process#i: Ri requestTaking list
	static HashMap<Integer, int[]> requestTable = new HashMap<Integer, int[]>();
	//each processFinished is false init
	static HashMap<Integer, Boolean> finishedTable = new HashMap<Integer, Boolean>();
	//take all process - process num may not start at 1
	static ArrayList<Integer> processSequence=new ArrayList<Integer>(); // contain all process
	//take all terminated process
	static ArrayList<Integer> processOrder=new ArrayList<Integer>();; // contain all process
	//reader read file
	static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws IOException {
		new Allocation().allocating();
		new Procession().processing();
		new Output().print();
		System.out.println();
	}
	public static class Allocation{
		public void allocating() throws IOException {
			String line = reader.readLine();
			String[] splitor = line.split("\\W+");//unallocated resource vector

			//inital U and Ri
			totalResource=splitor.length;
			U=new int[totalResource];
			for(int i=0; i<totalResource;i++) {
				//check all input is int
				try{
					U[i]=Integer.parseInt(splitor[i]);
				} catch (NumberFormatException e) {
					System.out.println("INPUT ERROR:");
					System.out.println("The first line inputed should be int resource number");
					break;
				}
			}
			
			//start read each processor request
			line = reader.readLine();
			while (!line.equals("")) {
//				//test comma start
//				System.out.println();
//				System.out.println(line);
//				//
				
				splitor = line.split("\\W+");
				int processNum= Integer.parseInt(splitor[1]);
				int resourceNum= Integer.parseInt(splitor[4]);
				
				//add finishedTable&processTable&requestTable if it is not exist
				if(! finishedTable.containsKey(processNum)) {
					processSequence.add(processNum);
					finishedTable.put(processNum, false);
				}
				if(! processTable.containsKey(processNum)) {
					int[] emptyR=new int[totalResource];
					for(int i=0; i<totalResource;i++) {
						emptyR[i]=0;
					}
					processTable.put(processNum, emptyR);
				}
				if(!requestTable.containsKey(processNum)) {
					int[] emptyR=new int[totalResource];
					for(int i=0; i<totalResource;i++) 
						emptyR[i]=0;
					requestTable.put(processNum, emptyR);
				}
				
				//U allocate resource to R
				if (U[resourceNum-1]>0) {
					U[resourceNum-1]--;
					int[] A=processTable.get(processNum);
					A[resourceNum-1]++;
					processTable.replace(processNum, A);
				}else {
					int[] R=requestTable.get(processNum);
					R[resourceNum-1]++;
					requestTable.replace(processNum, R);
				}
//				//test A value
//				System.out.println("allocation of each process");
//				for(int i=0;i<processTable.size();i++) {
//					System.out.print("process"+(i+1)+ ": ");
//					int[] a=processTable.get(i+1);
//					for(int j=0;j<a.length;j++) {
//						System.out.print(a[j]+ " ");
//					}
//					System.out.println();
//				}
//				//
//				//
//				//test R value
//				System.out.println("request of each process");
//				for(int i=0;i<requestTable.size();i++) {
//					System.out.print("request"+(i+1)+ ": ");
//					int[] b=requestTable.get(i+1);
//					for(int j=0;j<b.length;j++) {
//						System.out.print(b[j]+ " ");
//					}
//					System.out.println();
//				}
//				//
//				//test U value
//				System.out.println("value of U");
//				for(int i=0;i<U.length;i++) {
//					System.out.print(U[i]+" ");
//				}
//				System.out.println();
//				//
				line = reader.readLine();
			}
		}
	}
	public static class Procession{
		public void processing(){
			boolean reCheck=false;
			for(int i=0;i<processSequence.size();i++) {
				int proNum=processSequence.get(i);//process number
				boolean finished=finishedTable.get(proNum);
				int[] R= requestTable.get(proNum);
				if(!finished && isSubset(R,U)) {
					processOrder.add(proNum);
					int[] A=processTable.get(proNum);
					U=setResourceBack(A,U);
//					System.out.println("process "+proNum+" is terminated");
					processTable.remove(proNum);
					requestTable.remove(proNum);
					finishedTable.replace(proNum, true);
					reCheck=true;
				}
			}
			if (reCheck)
				processing();
			else{
				System.out.println();
			}
		}
		
		boolean isSubset(int[] r, int[] u) {
			boolean result=true;
			for(int i=0;i<r.length;i++) {
				if(r[i]>U[i]) {
					result=false;
				}
			}
			return result;
		}
		
		int[] setResourceBack(int[] a,int[] u) {
			for (int i=0; i<a.length;i++) {
				u[i]+=a[i];
				a[i]=0;
			}
			return u;
		}
	}
	public static class Output{
		public void print() {
			if(processTable.size()==0) {
				System.out.println("No deadlock, completion order");
				processOrder.forEach(e -> System.out.print(e+ " "));
			}else {
				System.out.println("Deadlock, processes involved are");
				for(int i=0;i<finishedTable.size();i++) {
					int proNum=processSequence.get(i);
					//if false, it is a deadlock
					if (!finishedTable.get(proNum))
						System.out.print(processSequence.get(i)+ " ");
				}
			}
		}
	}
}
