/*
 * Created by MVG
 * (C) 2019 Moscow, Russia
 * */
public class MainClass {
    /*1) Создают одномерный длинный массив*/
    private static final int SIZE = 10000000; // 10 Mb для весомости))
    private static final int SPLIT_SIZE = SIZE / 2;

    public static void main(String[] args) {
        MainClass o = new MainClass();
        o.runThroughModeCalculation();
        o.splitModeCalculation();
    }

    private static void fillArray(float a[]) {
        for (int i = 0; i < a.length; i++)
            a[i] = 1;
    }

    private static void doCalculationWithArray(float[] array, int startPosition) {
        for (int j = 0; j < array.length; j++, startPosition++) {
            // эта формула более правильная, т.к. дает те же значения для второй части массива в случае разбивки на 2
            array[j] = (float) (array[j] * Math.sin(0.2f + startPosition / 5) * Math.cos(0.2f + startPosition / 5) * Math.cos(0.4f + startPosition / 2));
            // данная формула дасть другие значения для второй части массива
            //array[j] = (float)(array[j] * Math.sin(0.2f + j/ 5) * Math.cos(0.2f + j / 5) * Math.cos(0.4f + j / 2));
        }
    }

    static void runThroughModeCalculation() {
        float[] array = new float[SIZE];
        fillArray(array);

        long threadTimer = System.currentTimeMillis();
        doCalculationWithArray(array, 0); // обсчитываем весь массив с нулевой позиции
        System.out.println("Время подсчета данных в массиве в один поток: " + (System.currentTimeMillis() - threadTimer));

        return;
    }

    static void splitModeCalculation() {
        float[] array = new float[SIZE];
        fillArray(array);
        float[] a1 = new float[SPLIT_SIZE];
        float[] a2 = new float[SPLIT_SIZE];

        long threadTimer = System.currentTimeMillis();

        System.arraycopy(array, 0, a1, 0, SPLIT_SIZE);
        System.arraycopy(array, SPLIT_SIZE, a2, 0, SPLIT_SIZE);

        Thread thread1 = new Thread(() -> {
            doCalculationWithArray(a1, 0);
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            doCalculationWithArray(a2, SPLIT_SIZE);
        });
        thread2.start();

        /* Дожидаемся окончания обработки данных в массиве */
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // склеиваем массив обратно
        System.arraycopy(a1, 0, array, 0, SPLIT_SIZE);
        System.arraycopy(a2, 0, array, SPLIT_SIZE, SPLIT_SIZE);
        System.out.println("Время выполнения метода в 2 потока: " + (System.currentTimeMillis() - threadTimer));
    }

}
