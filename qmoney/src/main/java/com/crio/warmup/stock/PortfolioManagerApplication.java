
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_REST_API
  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.

  public static String callApi(String token, String ticker, String startDate, String endDate) {
    return "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?startDate=" + startDate + "&endDate=" + endDate
        + "&token=" + token;
  }

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    String token = "f2a92f696bc7506ba5c6ec4893d9036c2f550ba1";
    File f = resolveFileFromResources(args[0]);
    Trade[] t = getObjectMapper().readValue(f, Trade[].class);
    TreeMap<Double, String> map = new TreeMap<>();
    List<String> list = new ArrayList<String>();
    for (Trade x : t) {
      String json = restTemplate.getForObject(callApi(token, 
          x.getSymbol(), x.getPurchaseDate(), args[1]),
          String.class);
      Tiingo[] tingo = getObjectMapper().readValue(json, Tiingo[].class);
      map.put(tingo[tingo.length - 1].getClose(), x.getSymbol());
    }
    for (Map.Entry<Double, String> entry : map.entrySet()) {
      list.add(entry.getValue());
    }
    return list;
  }

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    Trade[] t = getObjectMapper().readValue(f, Trade[].class);
    List<String> list = new ArrayList<String>();
    for (Trade x : t) {
      list.add(x.getSymbol());
    }
    return list;
  }

  private static File resolveFileFromResources(String filename) 
      throws URISyntaxException {
    return Paths.get(Thread.currentThread()
      .getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "trades.json";
    String toStringOfObjectMapper = "ObjectMapper";
    String functionNameFromTestFileInStackTrace = 
        "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "185";
    return Arrays.asList(new String[] { valueOfArgument0, 
        resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Copy the relevant code from #mainReadQuotes to parse the Json into
  // PortfolioTrade list and
  // Get the latest quotes from TIingo.
  // Now That you have the list of PortfolioTrade And their data,
  // With this data, Calculate annualized returns for the stocks provided in the
  // Json
  // Below are the values to be considered for calculations.
  // buy_price = open_price on purchase_date and sell_value = close_price on
  // end_date
  // startDate and endDate are already calculated in module2
  // using the function you just wrote #calculateAnnualizedReturns
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.
  // use gralde command like below to test your code
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // where trades.json is your json file

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    String token = "4bfefbfb973110a9e2c1251778c08616741787e0";
    File f = resolveFileFromResources(args[0]);
    PortfolioTrade[] t = getObjectMapper().readValue(f, PortfolioTrade[].class);

    TreeMap<Double, AnnualizedReturn> map = new TreeMap<>(Collections.reverseOrder());

    List<AnnualizedReturn> list = new ArrayList<>();

    for (PortfolioTrade x : t) {
      String json = restTemplate.getForObject(callApi(token, x.getSymbol(), 
          "" + x.getPurchaseDate(), args[1]),
          String.class);

      Tiingo[] tingo = getObjectMapper().readValue(json, Tiingo[].class);

      AnnualizedReturn n = calculateAnnualizedReturns(LocalDate.parse(args[1]), x, 
          tingo[0].getOpen(),
          tingo[tingo.length - 1].getClose());
      map.put(n.getAnnualizedReturn(), n);
    }

    for (Map.Entry<Double, AnnualizedReturn> entry : map.entrySet()) {
      list.add(entry.getValue());
    }
    return list;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // annualized returns should be calculated in two steps -
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  // Store the same as totalReturns
  // 2. calculate extrapolated annualized returns by scaling the same in years
  // span. The formula is
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // Store the same as annualized_returns
  // return the populated list of AnnualizedReturn for all stocks,
  // Test the same using below specified command. The build should be successful
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    Long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    Double annualizedReturns = (Math.pow((1 + totalReturns), (365.0 / daysBetween))) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory,
  // Create PortfolioManager using PortfoliomanagerFactory,
  // Refer to the code from previous modules to get the List<PortfolioTrades> and
  // endDate, and
  // call the newly implemented method in PortfolioManager to calculate the
  // annualized returns.
  // Test the same using the same commands as you used in module 3
  // use gralde command like below to test your code
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // where trades.json is your json file
  // Confirm that you are getting same results as in Module3.

  public static String readFileAsString(String name) 
      throws URISyntaxException, FileNotFoundException {
    File f = resolveFileFromResources(name);
    Scanner scanner = new Scanner(f, StandardCharsets.UTF_8.name());
    String content = scanner.useDelimiter("\\A").next();
    scanner.close();
    return content;
  }

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) 
      throws Exception {
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    PortfolioManager portfolioManager = PortfolioManagerFactory
        .getPortfolioManager(restTemplate);
        
    try {
      PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate); 
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}
