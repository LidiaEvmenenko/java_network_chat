package SomeOtherHomeWorks.ru.geekbrains.java;

import java.util.Arrays;

public class HomeWork6 {

    public static void main(String[] args) {
        int[] arr = {1,6,3,9,4,7,9,4,2,6};
        System.out.println(checkFor1Or4(arr));
        arr = new int[]{0, 6, 3, 9, 4, 7, 9, 4, 2, 4};
        System.out.println(checkFor1Or4(arr));
        arr = new int[]{1, 6, 3, 9, 8, 7, 9, 5, 2, 5};
        System.out.println(checkFor1Or4(arr));
        arr = new int[]{6, 6, 3, 9, 3, 7, 9, 8, 2, 0};
        System.out.println(checkFor1Or4(arr));

        arr = new int[]{1, 6, 3, 9, 4, 7, 9, 4, 2, 6};
        System.out.println(Arrays.toString(newArrAfterTheLast4(arr)));
        arr = new int[]{1, 6, 3, 9, 4, 7, 9, 4, 2, 4};
        System.out.println(Arrays.toString(newArrAfterTheLast4(arr)));
        arr = new int[]{1, 4, 4, 7, 4, 7, 9, 3, 2, 3};
        System.out.println(Arrays.toString(newArrAfterTheLast4(arr)));
        arr = new int[]{1, 6, 3, 9, 8, 7, 9, 3, 2, 3};
        System.out.println(Arrays.toString(newArrAfterTheLast4(arr)));
    }

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
