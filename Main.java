import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Main {
    
    public static Object convertStringToClass(String value, Class<?> clazz) throws Exception {
        if (clazz == String.class) {
            return value;
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(value);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(value);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (clazz == BigDecimal.class) {
            return new BigDecimal(value);
        } else if (clazz == BigInteger.class) {
            return new BigInteger(value);
        } else if (clazz == Date.class) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(value);
        } else {
            try {
                Method valueOfMethod = clazz.getMethod("valueOf", String.class);
                return valueOfMethod.invoke(null, value);
            } catch (NoSuchMethodException e) {
                Constructor<?> constructor = clazz.getConstructor(String.class);
                return constructor.newInstance(value);
            }
        }
    }

    public static void main(String[] args) {
        try {
            String stringValue = "2023-06-22";
            Class<?> clazz = LocalDate.class; // Suppose que nous connaissons la classe cible
            Object obj = convertStringToClass(stringValue, clazz);
            System.out.println(obj); // Affiche Thu Jun 22 00:00:00 UTC 2023

            stringValue = "123";
            clazz = Integer.class;
            obj = convertStringToClass(stringValue, clazz);
            System.out.println(obj); // Affiche 123

            stringValue = "true";
            clazz = Boolean.class;
            obj = convertStringToClass(stringValue, clazz);
            System.out.println(obj); // Affiche true

            stringValue = "123.45";
            clazz = BigDecimal.class;
            obj = convertStringToClass(stringValue, clazz);
            System.out.println(obj); // Affiche 123.45

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
