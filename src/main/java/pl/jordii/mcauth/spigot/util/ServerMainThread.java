package pl.jordii.mcauth.spigot.util;

import pl.jordii.mcauth.spigot.McAuthSpigot;
import org.bukkit.Bukkit;

import java.util.concurrent.CountDownLatch;

public class ServerMainThread {

    public static class WaitForCompletion<T> {

        private CountDownLatch startSignal;

        private CountDownLatch doneSignal;

        private Retrievable<T> retrievable;

        private Object result;

        private WaitForCompletion(Retrievable<T> retrievable) {
            this.retrievable = retrievable;
            this.result = null;
            this.startSignal = new CountDownLatch(1);
            this.doneSignal = new CountDownLatch(1);
        }


        public static <T> T result(Retrievable<T> retrievable) {
            WaitForCompletion<T> task = new WaitForCompletion<T>(retrievable);
            return (T) task.catchResult();
        }


        private Object catchResult() {

            Bukkit.getScheduler().scheduleSyncDelayedTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> {
                try {

                    this.startSignal.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.result = this.retrievable.retrieve();
                this.doneSignal.countDown();
            });


            this.startSignal.countDown();


            // wait for the operation on the main thread to complete
            // and then return the result
            try {
                this.doneSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return this.result;
        }

    }


    public static class RunParallel {


        public static void run(Runnable runnable) {

            // if we are already on the main thread, directly execute the operation instead of creating a new scheduler.
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(McAuthSpigot.getPlugin(McAuthSpigot.class), runnable);
        }

    }

}
