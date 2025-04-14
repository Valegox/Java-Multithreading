import java.util.*;
import java.lang.*;

// command-line execution example) java MatmultD 6 < mat500.txt
// 6 means the number of threads to use
// < mat500.txt means the file that contains two matrices is given as standard input
//
// In eclipse, set the argument value and file input by using the menu [Run]->[Run Configurations]->{[Arguments], [Common->Input File]}.

// Original JAVA source code: http://stackoverflow.com/questions/21547462/how-to-multiply-2-dimensional-arrays-matrix-multiplication
public class MatmultD
{
  private static Scanner sc = new Scanner(System.in);
  private static int numThreads=1;
  private static int taskSize=30;

  public static void main(String [] args)
  {
    if (args.length==1) numThreads = Integer.valueOf(args[0]);
        
    int a[][]=readMatrix();
    int b[][]=readMatrix();

    long startTime = System.currentTimeMillis();
    int[][] c=multMatrix(a,b);
    long endTime = System.currentTimeMillis();

    //printMatrix(a);
    //printMatrix(b);    
    //printMatrix(c);
    int sum = printMatrixSum(c);

    //System.out.printf("numThreads: %d\n" , numThreads);
    //System.out.printf("Calculation Time: %d ms\n" , endTime-startTime);

    System.out.printf("[thread_no]:%2d, [Time]:%4d ms, [Matrix sum]: %d\n", numThreads, endTime-startTime, sum);
  }

   public static int[][] readMatrix() {
       int rows = sc.nextInt();
       int cols = sc.nextInt();
       int[][] result = new int[rows][cols];
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
              result[i][j] = sc.nextInt();
           }
       }
       return result;
   }

  public static void printMatrix(int[][] mat) {
  System.out.println("Matrix["+mat.length+"]["+mat[0].length+"]");
    int rows = mat.length;
    int columns = mat[0].length;
    int sum = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        System.out.printf("%4d " , mat[i][j]);
        sum+=mat[i][j];
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("Matrix Sum = " + sum + "\n");
  }

  public static int printMatrixSum(int[][] mat) {
    int rows = mat.length;
    int columns = mat[0].length;
    int sum = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        sum+=mat[i][j];
      }
    }
    return sum;
  }

  public static int[][] multMatrix(int a[][], int b[][]){//a[m][n], b[n][p]
    if(a.length == 0) return new int[0][0];
    if(a[0].length != b.length) return null; //invalid dims

    int m = a.length;
    int p = b[0].length;

    MatmultD_counter counter = new MatmultD_counter(m, p);
    MatmultD_thread[] threads = new MatmultD_thread[numThreads];
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new MatmultD_thread(counter, a, b, numThreads, taskSize, i);
      threads[i].start();
    }

    //System.out.println("Matrix: " + n + " " + m + " " + p);

    for (int i = 0; i < numThreads; i++){
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        System.out.println("[" + threads[i].getName() + "] Interrupted.");
      }
    }
    return counter.getAns();
  }
}

class MatmultD_counter
{
  private int ans[][];

  public MatmultD_counter(int m, int p) {
    this.ans = new int[m][p];
  }

  public void setLine(int i, int[] line) {
    ans[i] = line;
  }

  public int[][] getAns() {
    return ans;
  }
}

class MatmultD_thread extends Thread
{
  private MatmultD_counter counter;
  private int[][] a;
  private int[][] b;
  private int numThreads;
  private int taskSize;
  private int threadIndex;

  public MatmultD_thread(MatmultD_counter counter, int[][] a, int[][] b, int numThreads, int taskSize, int threadIndex) {
    this.counter = counter;
    this.a = a;
    this.b = b;
    this.numThreads = numThreads;
    this.taskSize = taskSize;
    this.threadIndex = threadIndex;
  }

  @Override
  public void run() {

    int n = a[0].length;
    int m = a.length;
    int p = b[0].length;
    
    long startTime = System.currentTimeMillis();
    for (int i = threadIndex * taskSize; i < m; i += numThreads * taskSize) {
      int start = i;
      int end = i + taskSize;
      if (end > m) end = m;
      for (int ii = start; ii < end; ii++) {
        int line[] = new int[p];
        for (int j = 0; j < p; j++) {
          for (int k = 0; k < n; k++) {
            line[j] += a[ii][k] * b[k][j];
          }
        }
        counter.setLine(ii, line);
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println("[" + this.getName() + "] Execution Time: " + (endTime - startTime) + "ms");
  }
}
