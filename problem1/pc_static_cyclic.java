public class pc_static_cyclic {
    private static int NUM_END = 200000;    // default input
    private static int NUM_THREADS = 1;     // default number of threads
    private static int TASK_SIZE = 10;

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        prime_number_counter counter = new prime_number_counter();
        prime_number_calculator[] calculators = new prime_number_calculator[NUM_THREADS];
        int i;
        long startTime = System.currentTimeMillis();
        for (i = 0; i < NUM_THREADS; i++) {
            calculators[i] = new prime_number_calculator(counter, NUM_END, NUM_THREADS, TASK_SIZE, i);
            calculators[i].start();
        }
        for (i = 0; i < NUM_THREADS; i++) {
            try {
                calculators[i].join();
            } catch (InterruptedException e) {
                System.out.println("[" + calculators[i].getName() + "] Interrupted.");
            }
        }
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        float performance = (float)100/timeDiff;
        System.out.println("Program Performance: " + performance);
        System.out.println("1..." + (NUM_END-1) + " prime# counter=" + counter.getCounter());
    }
}

class prime_number_counter {
    private static int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}

class prime_number_calculator extends Thread {

    private prime_number_counter counter;
    private int numEnd;
    private int numThreads;
    private int taskSize;
    private int threadIndex;

    prime_number_calculator(prime_number_counter counter, int numEnd, int numThreads, int taskSize, int threadIndex) {
        super();
        this.counter = counter;
        this.numEnd = numEnd;
        this.numThreads = numThreads;
        this.taskSize = taskSize;
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        int i, j;
        long startTime = System.currentTimeMillis();
        for (i = threadIndex * taskSize; i < numEnd; i += numThreads * taskSize) {
            int start = i;
            int end = i + taskSize;
            if (end > numEnd) end = numEnd;
            for (j = start; j < end; j++) {
                if (isPrime(j)) this.counter.increment();
            }
        }
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[" + this.getName() + "] Execution Time: " + timeDiff + "ms");
        //System.out.println("Runned for " + start + " to " + (end - 1));
    }

    private static boolean isPrime(int x) {
        int i;
        if (x <= 1) return false;
        for (i = 2; i < x; i++) {
            if (x % i == 0) return false;
        }
        return true;
    }
}
