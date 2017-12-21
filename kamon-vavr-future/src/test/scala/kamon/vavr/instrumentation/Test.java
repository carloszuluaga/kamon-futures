package kamon.vavr.instrumentation;

import java.util.concurrent.Executors;

public class Test {

    public static void test(){
        Runnable r = () -> {
            new Exception().printStackTrace();
            System.out.println();
        };

        showIdentity(r);
        Executors.newSingleThreadExecutor().submit(r);
    }

    private static void showIdentity(Runnable runnable) {
        //runnable.run();
        Class<? extends Runnable> clazz = runnable.getClass();
        System.out.printf("class name     : %s%n", clazz.getName());
        System.out.printf("class hashcode : %s%n", clazz.hashCode());
        System.out.printf("canonical name : %s%n", clazz.getCanonicalName());
        System.out.printf("enclosing class: %s%n", clazz.getEnclosingClass());
        System.out.println();
    }
}
