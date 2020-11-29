
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  RestTemplate restTemplate;
  StockQuotesService stockQuotesService;
  // Caution: Do not delete or modify the constructor, or else your build will
  // break!



  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

// TODO: CRIO_TASK_MODULE_REFACTOR  
// Now we want to convert our code into a module, so we will not call it from
// main anymore.
// Copy your code from Module#3
// PortfolioManagerApplication#calculateAnnualizedReturn
// into #calculateAnnualizedReturn function here and make sure that it
// follows the method signature.
// Logic to read Json file and convert them into Objects will not be required
// further as our
// clients will take care of it, going forward.
// Test your code using Junits provided.
// Make sure that all of the tests inside PortfolioManagerTest using command
// below -
// ./gradlew test --tests PortfolioManagerTest
// This will guard you against any regressions.
// run ./gradlew build in order to test yout code, and make sure that
// the tests and static code quality pass.

// CHECKSTYLE:OFF

private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {
    
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<AnnualizedReturn>> list = new ArrayList<Future<AnnualizedReturn>>();
    List<AnnualizedReturn> answer =  new ArrayList<>();

    for(PortfolioTrade x : portfolioTrades){
      Callable<AnnualizedReturn> callableTask = () -> {
        List<Candle> l = this.stockQuotesService.getStockQuote(x.getSymbol(), x.getPurchaseDate(), endDate);
        AnnualizedReturn anu = annualizedReturns(endDate, x, l.get(0).getOpen(), l.get(l.size()-1).getClose());
        return anu;
      };
      Future<AnnualizedReturn> future = executor.submit(callableTask);     
      list.add(future);
    }

    for(Future<AnnualizedReturn> fut : list){
      try {
          answer.add(fut.get());
      } catch (Exception e) {
          throw new StockQuoteServiceException("");
      } 
    }
    answer.sort(getComparator());
    return answer;
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo thirdparty APIs to a separate function.
  // It should be split into fto parts.
  // Part#1 - Prepare the Url to call Tiingo based on a template constant,
  // by replacing the placeholders.
  // Constant should look like
  // https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  // Where ? are replaced with something similar to <ticker> and then actual url
  // produced by
  // replacing the placeholders with actual parameters.

  /*==============================================*/

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, JsonMappingException {
    try{
      String json = this.restTemplate.getForObject(buildUri(symbol, from, to), String.class);
      Candle[] list = getObjectMapper().readValue(json, TiingoCandle[].class);
      List<Candle> list2 = new ArrayList<>();
      for (Candle x : list) {
        list2.add(x);
      }
      return list2;
    }
    catch(NullPointerException e){
      return null;
    }
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate=" + startDate
        + "&endDate=" + endDate + "&token=4bfef5asa5d1251778c08616741787e0";
    return uriTemplate;
  }

  public static AnnualizedReturn annualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    Long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    Double annualizedReturns = (Math.pow((1 + totalReturns), (365.0 / daysBetween))) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    List<AnnualizedReturn> list = new ArrayList<>();
    for (PortfolioTrade x : portfolioTrades) {
      try {
        List<Candle> l = this.stockQuotesService.getStockQuote(x.getSymbol(), x.getPurchaseDate(), endDate);
        AnnualizedReturn anu = annualizedReturns(endDate, x, l.get(0).getOpen(), l.get(l.size()-1).getClose());
        list.add(anu);
      } catch (StockQuoteServiceException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    list.sort(getComparator());
    return list;
  }

}
