import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class SquareSumImp implements SquareSum {
    long finalSum;
    List<Callable<Long>> tasks = new ArrayList<>();
    List<Future<Long>> futureList = new ArrayList<>();
    int startPosition = 0;
    final Phaser phaser = new Phaser();


    public static void main(String[] args) {

        int[] testArray = {10, 10, 12, 22, 11, 21};
        System.out.println(new SquareSumImp().getSquareSum(testArray, 2));
    }

    @Override
    public long getSquareSum(int[] values, int num) {
        final int numberOfThreads = num > values.length ? values.length : num;
        final int BLOCKS = values.length / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            int[] a1 = new int[BLOCKS];
            System.arraycopy(values, startPosition, a1, 0, BLOCKS);
            phaser.register();
            tasks.add(new Task(a1, numberOfThreads));
            startPosition += BLOCKS;
        }
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
            futureList = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future<Long> longFuture : futureList) {
            try {
                finalSum += longFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return finalSum;
    }


    private class Task implements Callable<Long> {
        int[] values;
        int numberOfThreads;

        public Task(int[] values, int numberOfThreads) {
            this.values = values;
            this.numberOfThreads = numberOfThreads;
        }

        @Override
        public Long call() throws Exception {
            //phaser.register();
            int taskResult = 0;
            for (int value : values) {
                taskResult += value * value;
            }
            phaser.arrive();
            return (long) taskResult;
        }
    }
}
