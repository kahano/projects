import java.util.Arrays;
import java.lang.*;


class Seq_matrix{


    static int runs = 7;
    static int n;
    static int seed;
    static Matrix_multiply.Mode mode;


    public static void multiply_not_transposed(double[][] a_matrix, double[][] b_matrix, double[][] c_matrix){
        for(int i = 0; i < a_matrix.length;i++)
            for(int j= 0; j < b_matrix.length;j++)
                for(int v = 0; v < a_matrix.length;v++)
                    c_matrix[i][j] += a_matrix[i][v] * b_matrix[v][j];



    }

    public static double[][] multi_transpose(double[][] m){
        double[][] transpos = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++)
                transpos[i][j] = m[j][i];


        return transpos;
    }


    public static void multiply_a_transposed(double[][] a_matrix, double[][] b_matrix, double[][] c_matrix){

        double[][] a_transpos = multi_transpose(a_matrix);

        for(int i = 0; i < a_matrix.length;i++)
            for(int j= 0; j < b_matrix.length;j++)
                for(int v = 0; v < a_matrix.length;v++)
                    c_matrix[i][j] += a_transpos[v][i] * b_matrix[v][j];
    }

    public static void multiply_b_transposed(double[][] a_matrix, double[][] b_matrix, double[][] c_matrix){

        double[][] b_transpos = multi_transpose(b_matrix);

        for(int i = 0; i < a_matrix.length;i++)
            for(int j= 0; j < b_matrix.length;j++)
                for(int v = 0; v < a_matrix.length;v++)
                    c_matrix[i][j] += a_matrix[i][v] * b_transpos[j][v];
    }

    public static double[][] choose_mode(double[][] a, double[][] b, Matrix_multiply.Mode mode ){

        double[][] c = new double[a.length][a[0].length];

        switch(mode){

            case SEQ_NOT_TRANSPOSED:
                multiply_not_transposed(a,b,c);
                break;

            case SEQ_A_TRANSPOSED:
                multiply_a_transposed(a,b,c);
                break;

            case SEQ_B_TRANSPOSED:
                multiply_b_transposed(a,b,c);
                break;


            default:
                Parallell_matrix.compute(a,b,c);
        }
        return c;
    }

    static double runtime( Matrix_multiply.Mode m, double[][] a, double[][] b) {
        double[] exec_time = new double[runs];
        double[][] res = null;

        for (int i = 0; i < 1; i++) {
            long start = System.nanoTime();
            res = choose_mode(a,b,m);
            long end = System.nanoTime();
            double tid = (end - start) / 1000000.0;
            exec_time[i] = tid;

        }

        Matrix_multiply.saveResult(seed,m,res);


        double median = exec_time[exec_time.length/2];
        return median;
    }



    public static void matrix_print(double[][] ans){
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
                mode = Matrix_multiply.Mode.SEQ_NOT_TRANSPOSED;
                break;
            case 2:
                mode = Matrix_multiply.Mode.SEQ_A_TRANSPOSED;
                break;
            case 3:
                mode = Matrix_multiply.Mode.SEQ_B_TRANSPOSED;
                break;


            default:
                System.out.println("\nwrong mode input");
        }


        System.out.println("Median_sequential : " + runtime(mode,a,b));




    }


}
