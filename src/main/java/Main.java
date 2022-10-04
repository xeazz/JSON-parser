import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.csv";
        String fileName2 = "data.xml";
        String[] data = {"1,John,Smith,USA,25", "2,Inav,Petrov,RU,23"};
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        //Задание №1
        creatCSV(data);
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        writeString(listToJson(listCSV), "data1.json");

//        //Задание №2
        List<Employee> listXML = parseXML(fileName2);
        writeString(listToJson(listXML), "data2.json");

        //Задание №3
        String json = readString("data.json");
        System.out.println(jsonToList(json));
    }

    // Создаёт файл data.csv с исходными данными записынамит в массив String [][] employee
    public static void creatCSV(String[] data) {
        String[][] employee = {data[0].split(","), data[1].split(",")};
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            for (String[] x : employee) {
                writer.writeNext(x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(list);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName2) {
        List<Employee> list = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName2));
            Node root = doc.getDocumentElement();
            list = read(root);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;
                String[] a = element.getTextContent().trim().replaceAll("\\s+", ",").split(",");
                Employee employee = new Employee(Long.parseLong(a[0]), a[1], a[2], a[3], Integer.parseInt(a[4]));
                list.add(employee);
                read(node_);
            }
        }
        return list;
    }

    public static String readString(String nameFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Employee> jsonToList(String name) {
        JSONParser jsonParser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(name);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (Object x : jsonArray) {
                Employee employee = gson.fromJson(String.valueOf(x), Employee.class);
                list.add(employee);
            }
            return list;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
