public class pc_dynamic {
    private static int NUM_END = 200000;    // default input
    private static int NUM_THREADS = 1;     // default number of threads
    private static int TASK_SIZE = 10;

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        prime_number_counter counter = new prime_number_counter(NUM_END, TASK_SIZE);
        prime_number_calculator[] calculators = new prime_number_calculator[NUM_THREADS];
        int i;
        long startTime = System.currentTimeMillis();
        for (i = 0; i < NUM_THREADS; i++) {
            calculators[i] = new prime_number_calculator(counter);
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
    private int remaining = 0;
    private int taskSize = 0;

    prime_number_counter(int numEnd, int taskSize) {
        this.remaining = numEnd;
        this.taskSize = taskSize;
    }

    public synchronized void increment() {
        counter++;
    }

    public synchronized int takeTask() {
        if (remaining <= 0) return -1;
        try {
            return remaining;
        } finally {
            remaining -= taskSize;
            if (remaining < 0) remaining = 0;
        }
    }

    public int getCounter() {
        return counter;
    }

    public int getTaskSize() {
        return taskSize;
    }
}

class prime_number_calculator extends Thread {

    private prime_number_counter counter;


    prime_number_calculator(prime_number_counter counter) {
        super();
        this.counter = counter;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (true) {
            int taskEnd = this.counter.takeTask();
            if (taskEnd <= 0) break;
            int taskStart = taskEnd - this.counter.getTaskSize();
            if (taskStart < 0) taskStart = 0;
            for (int i = taskStart; i < taskEnd; i++) {
                if (isPrime(i)) this.counter.increment();
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
