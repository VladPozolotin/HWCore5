package main.java;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    protected static boolean load = false;
    protected static String loadFile;
    protected static int loadFormat;
    protected static boolean logging = false;
    protected static String logName;
    protected static boolean save = false;
    protected static String saveFile;
    protected static int saveFormat;

    public static void main(String[] args) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("shop.xml"));

            NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                    Element element = (Element) currentNode;
                    if (element.getTagName().equals("load")) {
                        if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                            load = true;
                        }
                        loadFile = element.getElementsByTagName("fileName").item(0).getTextContent();
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("json")) {
                            loadFormat = 1;
                        } else if (element.getElementsByTagName("format").item(0).getTextContent().equals("text")) {
                            loadFormat = 2;
                        }
                    }
                    if (element.getTagName().equals("save")) {
                        if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                            save = true;
                        }
                        saveFile = element.getElementsByTagName("fileName").item(0).getTextContent();
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("json")) {
                            saveFormat = 1;
                        } else if (element.getElementsByTagName("format").item(0).getTextContent().equals("text")) {
                            saveFormat = 2;
                        }
                    }
                    if (element.getTagName().equals("log")) {
                        if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                            logging = true;
                        }
                        logName = element.getElementsByTagName("fileName").item(0).getTextContent();
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        String[] products = {"Хлеб", "Молоко", "Печенье"};
        int[] prices = {50, 70, 60};
        int cartSum = 0;
        Basket cart = null;
        if (load) {
            File basket = new File(loadFile);
            switch (loadFormat) {
                case 1 -> cart = Basket.loadFromJSON(basket);
                case 2 -> cart = Basket.loadFromTxtFile(basket);
                default -> {
                }
            }
        }
        if (cart == null) {
            cart = new Basket(products, prices);
        } else {
            for (String product : products) {
                cartSum = cartSum + (cart.getPrice(product) * cart.getCount(product));
            }
        }
        ClientLog log = new ClientLog();
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
                if (logging) {
                    File logFile = new File(logName);
                    log.exportAsCSV(logFile);
                }
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
                        log.log(Integer.parseInt(fields[0]), itemCount);
                        if (save) {
                            File basketNew = new File(saveFile);
                            switch (saveFormat) {
                                case 1 -> cart.saveJSON(basketNew, cart);
                                case 2 -> cart.saveTxt(basketNew);
                                default -> {
                                }
                            }
                        }
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