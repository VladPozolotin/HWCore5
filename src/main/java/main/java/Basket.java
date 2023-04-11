package main.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Basket {
    protected List<String> products;
    protected List<Integer> prices = new ArrayList<>();
    protected HashMap<String, Integer> basket = new HashMap<>();

    public Basket(String[] products, int[] prices) {
        this.products = Arrays.asList(products);
        this.prices = Arrays.stream(prices).boxed().collect(Collectors.toList());
        for (String product : this.products) {
            basket.put(product, 0);
        }
    }

    public void addToCart(int productNum, int amount) {
        Integer cart = this.basket.get(this.products.get(productNum));
        cart = cart + amount;
        this.basket.put(this.products.get(productNum), cart);
    }

    public void printCart() {
        for (int i = 0; i < this.products.size(); i++) {
            if (this.basket.get(this.products.get(i)) > 0) {
                System.out.println(this.products.get(i) + " " + this.basket.get(this.products.get(i)) + " шт. " + this.prices.get(i) + " руб/шт " + "сумма: " + (this.prices.get(i) * this.basket.get(this.products.get(i))) + " руб");
            }
        }
    }

    public void saveTxt(File textFile) throws IOException {
        try (PrintWriter out = new PrintWriter(textFile)) {
            for (String product : this.products) {
                out.print(product + " ");
            }
            out.print("\n");
            for (Integer price : this.prices) {
                out.print(price + " ");
            }
            out.print("\n");
            for (String product : this.products) {
                out.print(this.basket.get(product) + " ");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void saveJSON(File JSONfile, Basket basket) throws IOException {
        GsonBuilder build = new GsonBuilder();
        Gson gson = build.create();
        try (FileWriter file = new FileWriter(JSONfile)) {
            gson.toJson(basket, file);
            file.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int getPrice(String product) {
        return this.prices.get(this.products.indexOf(product));
    }

    public int getCount(String product) {
        return this.basket.get(product);
    }

    public static Basket loadFromTxtFile(File textFile) {
        try {
            FileReader in = new FileReader(textFile);
            BufferedReader reader = new BufferedReader(in);
            String lineProd = reader.readLine();
            String[] products = lineProd.split(" ");
            String linePrice = reader.readLine();
            String[] pricesStr = linePrice.split(" ");
            int[] prices = Arrays.stream(pricesStr)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            String lineCount = reader.readLine();
            String[] countStr = lineCount.split(" ");
            int[] count = Arrays.stream(countStr)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            Basket basket = new Basket(products, prices);
            for (int i = 0; i < count.length; i++) {
                basket.addToCart(i, count[i]);
            }
            return basket;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public static Basket loadFromJSON(File JSONfile) {
        Gson gson = new Gson();
        try {
            FileReader in = new FileReader(JSONfile);
            BufferedReader reader = new BufferedReader(in);
            Basket basket = gson.fromJson(reader, Basket.class);
            return basket;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
