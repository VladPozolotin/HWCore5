package main.java;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class ClientLog {
    class Event {
        protected int productNum;

        protected int amount;

        public Event(int itemNum, int itemAmount) {
            this.productNum = itemNum;
            this.amount = itemAmount;
        }
    }

    protected ArrayList<Event> inputs;

    public ClientLog() {
        this.inputs = new ArrayList<>();
    }

    public void log(int productNum, int amount) {
        this.inputs.add(new Event(productNum, amount));
    }

    public void exportAsCSV(File textFile) throws IOException {
        ColumnPositionMappingStrategy<Event> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Event.class);
        strategy.setColumnMapping("productNum", "amount");
        try (Writer log = new FileWriter(textFile)) {
            StatefulBeanToCsv<Event> sbc = new StatefulBeanToCsvBuilder<Event>(log)
                    .withMappingStrategy(strategy)
                    .build();
            sbc.write(this.inputs);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            System.out.println(e.getMessage());
        }
    }
}
