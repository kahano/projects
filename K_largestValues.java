
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;
import java.util.List;

/*

 jeg bruker parallell programmering som benytter 4 tråder
til å finne største "K" som er oppgitt av brukern som blir sortert synkende. Jeg skal sammenligne en algoritme
som bruker parallitet med en algoritme som bruker sekvensiell kjøring, og finne ut hvilke "n" størrselse er verdt med å
bruke parallitet fremfor sekvensiell kjøring slik at programmet skal kjøre raskere. Hvis ratio av (seq median/parallel median) er > 1 så betyr parallitet er mer effektivt i dette tilfellet.
Jeg tester for n = 1000, 10000, 100000, 1000000 ,10000000, 100000000.


*/



class K_largestValues{

    static int cores = Runtime.getRuntime().availableProcessors();
    static CyclicBarrier cb = new CyclicBarrier(cores + 1);



    /** This sorts a [v..h] in ascending order with the insertion algorithm */
    private static void insertSort (int [] a, int v, int h) {


        for (int k = v; k < h; k++) {
         // invariant: a [v..k] is now sorted ascending (smallest first)
            int t = a[k + 1];
            int i = k;
            while (i >= v && a[i] < t) { // descending order
                a[i + 1] = a[i];
                i--;
            }
            a[i + 1] = t;
             // than for k
        } // end insertSort


    }



    private static void swap(int[]array,int a, int b){
        int temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }



    private static void K_largest_sortering(int[] arr,int v, int k,int h){


        for (int i = k+1; i < h; i++) {
            if(arr[i] > arr[k]){
                swap(arr, k,i);
                insertSort(arr,v,k);
            }
        }


    }



    public static void sort_a2(int[] arr, int k){ // sequential method

        K_largest_sortering(arr,0,k-1,arr.length);

        int[] ny_arr = new int[k];

        for(int i = 0; i < k; i++){
            ny_arr[i] = arr[i];
        }
        //System.out.println(Arrays.toString(ny_arr));


    }




    private static void sort_a2_parall(int[] a, int v, int k,int h){ // hjelping parallell method

        K_largest_sortering(a,v,v+k,h);



    }



    static class MyRun implements Runnable{
        //
        private int index;
        private int start;
        private int end;
        private int k;
        private int[] arr;
        private int[][] lst;
        private int[] sort;


        public MyRun(
                    int index,
                    int v,
                    int h,
                    int k,
                    int[][]lst,
                    int[] arr

                    ){

            this.index = index;
            this.start = v;
            this.end = h;
            this.k = k;
            this.lst = lst;
            this.arr = arr;
            this.sort = new int[k];

        }

        public void run(){


            sort_a2_parall(this.arr,this.start,this.k,this.end);

            for (int j = 0; j < this.sort.length ; j++) {
                this.sort[j] = this.arr[this.start+j];
            }



            //System.out.println("thread: " + Arrays.toString( this.sort));


            this.lst[index] = this.sort;
            //System.out.println("thread: " + Arrays.toString(this.lst[index]));




           try{
               cb.await();
           }catch(Exception e){
               e.printStackTrace();
           }



        }


    }


    // here I am using some hjelping methods  to sort the results by using heap

    private static void Bubbledown(int[] array,int i , int size){
        int storst = i;
        int venstre = 2*i + 1;
        int hoyre = 2*i + 2;

        if(venstre < size && array[storst] > array[venstre] ){
            storst = venstre;
        }
        if(hoyre < size && array[storst] > array[hoyre] ){
            storst = hoyre;

        }
        if(storst != i){
           swap(array,storst,i);
           Bubbledown(array,storst,size);
        }

    }

    private static void BuildMaxHeap(int[] array,int size){
        for(int i = array.length/2-1; i >= 0;i--){
            Bubbledown(array,i,array.length);
        }

    }

    public static void Heapsort(int[] array){
        BuildMaxHeap(array,array.length);
        for(int i = array.length-1; i >= 0;i--){
            swap(array,0,i);

            Bubbledown(array,0,i);
        }

    }

    public static void sort_a2Parall(int[] a, int k){


        int segment_size = a.length/cores;


        int[][] que = new int[cores][k];


        List<Integer> k_biggst = new ArrayList<>() ;




        for (int i = 0; i < cores; i++ ) {

            int start = i*segment_size;

            new Thread(new MyRun(i,start,(i+1)*segment_size,k,que,a)).start();


        }


        try{
            cb.await();
        }catch(Exception e){
            e.printStackTrace();
        }


        for(int i = 0; i < que.length;i++){
            for(int j = 0 ; j < que[i].length;j++){
                k_biggst.add(que[i][j]);
            }

        }

        int[] arr  = new int[k_biggst.size()];
        for(int i = 0; i < arr.length;i++){
            arr[i] = k_biggst.get(i);
        }


        Heapsort(arr);


        int[] result = new int[k];
        for(int j = 0; j < result.length;j++ ){
            result[j] = arr[j];
        }

        System.out.println("biggest K: " + Arrays.toString(result));



    }


    public static void main(String[] args){

        int n;
        int k;
        int runs = 7;

        try {


             n = Integer.parseInt(args[0]);
             System.out.println("\n");
             k = Integer.parseInt(args[1]);




        } catch(ArrayIndexOutOfBoundsException e) {
             System.out.println("\nwrong with the array length. \n");
             return;
        } catch(NumberFormatException e) {
             System.out.println("\n send an integer <n>: .");
             return;
        }


        int[] arr = new int[n];
        Random r = new Random(7363);
        for(int i = 0; i < n;i++){
            arr[i] = r.nextInt(n);

        }

        double[] runtime_parall = new double[runs];
        for (int i = 0; i < runs; i++) {


            long start = System.nanoTime();
            sort_a2Parall(arr,k);
            long end = System.nanoTime();

            runtime_parall[i] = (end - start) / 1000000.0;


        }

        double median_parall = runtime_parall[runs / 2];

        System.out.println("\nMedian parall kjoringstid for A2 : " + median_parall);
        System.out.println("\n");

        double[] runtime_seq = new double[runs];
        for (int i = 0; i < runs; i++) {

            long start = System.nanoTime();
            sort_a2(arr,k);
            long end = System.nanoTime();
            runtime_seq[i] = (end - start) / 1000000.0;

        }




        double median_seq = runtime_seq[runs / 2];
        System.out.println("\n");
        System.out.println("\nMedian sequential kjoringstid for A2 : " + median_seq);

        System.out.println("\n");
        double speedup = median_seq/median_parall;
        System.out.println("speed up : " + speedup);
        System.out.println("\n");

    }





}
