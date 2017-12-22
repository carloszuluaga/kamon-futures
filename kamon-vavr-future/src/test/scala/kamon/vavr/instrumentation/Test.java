package kamon.vavr.instrumentation;

import java.util.concurrent.Executors;


class MyRunnableTask implements Runnable {
    public void run() {
        new Exception().printStackTrace();
        System.out.println("Soy MyRunnableTask");
    }
}

public class Test {

    public static void test(){
        Runnable r0 = () -> {
            new Exception().printStackTrace();
            System.out.println();
        };

        Runnable r1 = () -> {
            new Exception().printStackTrace();
            System.out.println();
        };

        Runnable r2 = new MyRunnableTask();

        showIdentity(r1);
        showIdentity(r2);

        Executors.newSingleThreadExecutor().submit(r1);
        Executors.newSingleThreadExecutor().submit(r2);

    }

    private static void showIdentity(Runnable runnable) {
        //runnable.run();
        Class<? extends Runnable> clazz = runnable.getClass();
        System.out.printf("class name          : %s%n", clazz.getName());
        System.out.printf("class hashcode      : %s%n", clazz.hashCode());
        System.out.printf("canonical name      : %s%n", clazz.getCanonicalName());
        System.out.printf("enclosing class     : %s%n", clazz.getEnclosingClass());

        Class[] interfaces = clazz.getInterfaces();
        System.out.println("Interfaces:");

        for(int i=0; i<interfaces.length; i++)
            System.out.printf("Interfase     : %s", interfaces[i]);

        System.out.println();
    }
}
