package SomeOtherHomeWorks.ru.geekbrains.java;

import java.util.Arrays;

public class HomeWork6 {
    public static int[] newArrAfterTheLast4(int[] arr){
        int index = 0;
        for(int i = arr.length-1; i>=0; i--){
            if(arr[i] == 4) {
                index = i + 1;
                break;
            }
        }
        if(index == 0) {
            throw new RuntimeException("В массиве нет 4.");
        }
        int[] a = new int[arr.length - index];
        int j = 0;
        for (int i = index; i < arr.length; i++){
            a[j] = arr[i];
            j++;
        }
        return a;
    }
    public static boolean checkFor1Or4(int[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1 || arr[i] == 4){
                return true;
            }
        }
        return false;
    }

}
