import java.util.concurrent.CyclicBarrier;
import java.util.Arrays;

class Parallell_matrix{



    static int runs = 7;
    static int n;
    static int seed;
    static int cores = Runtime.getRuntime().availableProcessors();
    static CyclicBarrier cb = new CyclicBarrier(cores + 1);
    static Matrix_multiply.Mode mode;




    static class MyRun implements Runnable{
        private double[][] a;
        private  double[][] b;
        private  double[][] c;
        private  int start;
        private int end;

        public MyRun (
                    double[][] a,
                    double[][] b,
                    double[][] c,
                    int s,
                    int e
                    ){

            this.a = a;
            this.b = b;
            this.c = c;
            start = s;
            end = e;


        }


        public void matrix_print(double[][] ans){
            for(int i = 0; i < ans.length;i++){
                double res ;
                for(int j = 0; j < ans[i].length;j++){
                    res = Math.round(ans[i][j]*100);
                    res = res/100;
                    System.out.print(" " +res+ " ");
                }
                System.out.println(" ");
            }
        }

        public double[][] multi_transpose(double[][] m){
            double[][] transpos = new double[m.length][m[0].length];
            for (int i = 0; i < m.length; i++)
                for (int j = 0; j < m[i].length; j++)
                    transpos[i][j] = m[j][i];


            return transpos;
        }
        @Override
        public void run(){

            switch(mode){

                case PARA_NOT_TRANSPOSED:

                    for(int i = start; i < end;i++)
                        for(int j= 0; j < b.length;j++)
                            for(int v = 0; v < a.length;v++)
                                c[i][j] += a[i][v] * b[v][j];
                    break;

                case PARA_A_TRANSPOSED:
                    double[][] a_transpos = multi_transpose(a);
                    for(int i = start; i < end;i++)
                        for(int j= 0; j < b.length;j++)
                            for(int v = 0; v < a.length;v++)
                                c[i][j] += a_transpos[v][i] * b[v][j];

                    break;

                case PARA_B_TRANSPOSED:
                    double[][] b_transpos = multi_transpose(b);
                    for(int i = start; i < end;i++)
                        for(int j= 0; j < b.length;j++)
                            for(int v = 0; v < a.length;v++)
                                c[i][j] += a[i][v] * b_transpos[j][v];

                    break;
            }

            // matrix_print(c);
            // System.out.println("\n");


            try{

                cb.await();
            }catch(Exception e){
                e.printStackTrace();
            }


        }
    }

    public static void compute(double[][] a, double[][] b, double[][] c){


       int tot_cores = 0;


       int segment_length = a.length / cores;



       Thread[] threads = new Thread[cores];
       int start = 0;
       for (int i = 0; i < cores; i++, start+= segment_length) {

           threads[i] = new Thread(new MyRun(a,b,c,start,start+segment_length));

       }

       for(int i = 0; i < cores;i++){
           threads[i].start();
       }

       try{

           cb.await();
       }catch(Exception e){
           e.printStackTrace();
       }


   }

   static double runtime( Matrix_multiply.Mode m, double[][] a, double[][] b) {
       double[] exec_time = new double[runs];
       double[][] res = null;

       for (int i = 0; i < runs; i++) {
           long start = System.nanoTime();
           res = Seq_matrix.choose_mode(a,b,mode);
           long end = System.nanoTime();
           double tid = (end - start) / 1000000.0;
           exec_time[i] = tid;

       }
       Matrix_multiply.saveResult(seed,m,res);

       //System.out.println("times: " + Arrays.toString(exec_time));
       System.out.println("\n");
       double median = exec_time[exec_time.length/2];
       return median;
   }




   public static void main(String[] args){


       int mode_nr ;

       try {
            seed = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);
            mode_nr = Integer.parseInt(args[2]);
            System.out.println("\n");


        } catch(ArrayIndexOutOfBoundsException e) {
             System.out.println("\nwrong with the array length. \n");
             return;
        } catch(NumberFormatException e) {
             System.out.println("\n send an integer <n>: ");
             return;
        }

        double[][] a = Matrix_multiply.generateMatrixA(seed, n);
        double[][] b = Matrix_multiply.generateMatrixB(seed,n);

        switch (mode_nr) {
           case 1:
               mode = Matrix_multiply.Mode.PARA_NOT_TRANSPOSED;
               break;
           case 2:
               mode = Matrix_multiply.Mode.PARA_A_TRANSPOSED;
               break;
           case 3:
               mode = Matrix_multiply.Mode.PARA_B_TRANSPOSED;
               break;


           default:
               System.out.println("\nwrong mode input");
       }


       System.out.println("Median_Parallell : " + runtime(mode,a,b));




   }








}
