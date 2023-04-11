package main.java;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] products = {"Хлеб", "Молоко", "Печенье"};
        int[] prices = {50, 70, 60};
        int cartSum = 0;
        File basket = new File("basket.txt");
        Basket cart = Basket.loadFromTxtFile(basket);
        if (cart == null) {
            cart = new Basket(products, prices);
        } else {
            for (String product : products){
                cartSum = cartSum + (cart.getPrice(product)*cart.getCount(product));
            }
        }
        System.out.println("Ассортимент:");
        for (int i = 0; i < products.length; i++) {
            System.out.println((i + 1) + ". " + products[i] + " — " + prices[i] + " руб/шт");
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                System.out.println("Ваша корзина:");
                break;
            }
            String[] fields = input.split(" ");
            try {
                if (fields.length != 2) {
                    System.out.println("Некорректный ввод. Введите номер товара и количество товара через пробел.");
                } else {
                    int itemNum = Integer.parseInt(fields[0]) - 1;
                    int itemCount = Integer.parseInt(fields[1]);
                    if (itemNum < 0 || Integer.parseInt(fields[0]) > products.length) {
                        System.out.println("Такого товара нет в наличии.");
                    } else if (itemCount <= 0) {
                        System.out.println("Некорректное количество товара.");
                    } else {
                        cart.addToCart(itemNum, itemCount);
                        cart.saveTxt(basket);
                        int itemPrice = prices[itemNum];
                        cartSum += (itemPrice * itemCount);
                    }
                }
            } catch (NumberFormatException | IOException exception) {
                System.out.println("Некорректный ввод. Номер товара и количество товара должны вводиться цифрами.");
            }
        }
        if (cartSum == 0) {
            System.out.println("Пусто");
        } else {
            cart.printCart();
            }
        System.out.println("Итого: " + cartSum + " руб.");
    }
}