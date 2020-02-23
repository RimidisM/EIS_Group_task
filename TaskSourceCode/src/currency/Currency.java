package currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author rimid
 */
public class Currency {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     *
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Daily euro foreign exchange reference rates service");

        System.out.println("");

        boolean exe = true;

        while (exe) {
            if (initiateInformationToConsole() != true) {
                exe = false;
            }
        }
    }

    private static double getCurrencyRatio(String date, LbClient cl, String currencyName) throws IOException {
        cl.resetParameters();
        cl.addParameters("tp", "Eu");
        cl.addParameters("dt", date);
        List<CurencyValueModel> dateResult = cl.getData();
        return getModelByName(dateResult, currencyName).curencyRatio;
    }

    private static CurencyValueModel getModelByName(List<CurencyValueModel> models, String currencyName) {
        CurencyValueModel result = null;
        for (CurencyValueModel model : models) {
            if (currencyName.equals(model.curencyName)) {
                result = model;
            }
        }
        return result;
    }

    private static void printResult(String currencyCode, String inputDate, double dateRatio) {
        System.out.println(currencyCode + " proportion for " + inputDate + " is: " + dateRatio);

    }

    private static boolean initiateInformationToConsole() throws MalformedURLException, IOException {
        String currencyCode;
        String firstInputDate;
        String secondInputDate;
        boolean firstDateValidation;
        boolean secondDateValidation;

        String url = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRates?";
        LbClient cl = new LbClient(url);

        Scanner sc = new Scanner(System.in);
        System.out.println("Please insert currency code (for example USD) or STOP to exit application");
        currencyCode = sc.nextLine().toUpperCase();
        if (currencyCode.equalsIgnoreCase("STOP")) {
            System.out.println("Good bye!!!");
//            sc.close();
            return false;
        } else {
            System.out.println("");
            System.out.println("Please insert first date (date format ISO 8601, example. 2015-01-02)");
            firstInputDate = sc.nextLine();
            if (!isValidFormat(firstInputDate)) {
                System.out.println("Bad date format, please use date format ISO 8601, example. 2015-01-02");
                System.out.println("");
//                initiateInformationToConsole();
            } else {
                System.out.println("");
                System.out.println("Please insert second date (date format ISO 8601, example. 2015-01-02)");
                secondInputDate = sc.nextLine();
                if (!isValidFormat(secondInputDate)) {
                    System.out.println("BAD DATE FORMAT, please use date format ISO 8601, example. 2015-01-02");
                    System.out.println("");
//                    initiateInformationToConsole();
                } else {

                    //Ratio for first date
                    double firstDateRatio = getCurrencyRatio(firstInputDate, cl, currencyCode);
                    //Ratio for second date
                    double secondDateRatio = getCurrencyRatio(secondInputDate, cl, currencyCode);

                    printResult(currencyCode, firstInputDate, firstDateRatio);
                    printResult(currencyCode, secondInputDate, secondDateRatio);

                    double d = secondDateRatio - firstDateRatio;
                    String b = BigDecimal.valueOf(d).setScale(4, BigDecimal.ROUND_HALF_UP).toEngineeringString();
                    System.out.println("");
                    System.out.println("Change for given dates is: " + b);
                    System.out.println("");
                }
            }
        }

        return true;
    }

    public static boolean isValidFormat(String source) throws IOException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String d = sd.format(new Date());
        sd.setLenient(false);
        try {
            Date date = sd.parse(source);
            Date today = sd.parse(d);
            if (date.compareTo(today) > 0) {
                System.out.println("Future date inserted");
                System.out.println("");
//                initiateInformationToConsole();
            }
            return date != null && sd.format(date).equals(source);

        } catch (ParseException e) {
            return false;
        }

    }
}
