
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {
  private String apiKey = "GOUOS7TU4LTOR0IT";
  private RestTemplate restTemplate;

  public AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Inplement the StockQuoteService interface as per the contracts.
  // The implementation of this functions will be doing following tasks
  // 1. Build the appropriate url to communicate with thirdparty.
  // The url should consider startDate and endDate if it is supported by the
  // provider.
  // 2. Perform thirdparty communication with the Url prepared in step#1
  // 3. Map the response and convert the same to List<Candle>
  // 4. If the provider does not support startDate and endDate, then the
  // implementation
  // should also filter the dates based on startDate and endDate.
  // Make sure that result contains the records for for startDate and endDate
  // after filtering.
  // 5. return a sorted List<Candle> sorted ascending based on Candle#getDate
  // Call alphavantage service to fetch daily adjusted data for last 20 years.
  // Refer to
  // documentation here - https://www.alphavantage.co/documentation/
  // Make sure you use {RestTemplate#getForObject(URI, String)} else the test will
  // fail.
  // Run the tests using command below and make sure it passes
  // ./gradlew test --tests AlphavantageServiceTest
  // CHECKSTYLE:OFF
  // CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call alphavantage service. Method
  // should
  // be using configurations provided in the {@link @application.properties}.
  // Use thie method in #getStockQuote.

  /*==================================================================================*/

  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  Update the method signature to match the signature change in the interface.
  //  Start throwing new StockQuoteServiceException when you get some invalid response from
  //  Alphavangate, or you encounter a runtime exception during Json parsing.
  //  Make sure that the exception propagates all the way from PortfolioManager,
  //  so that the external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
                  throws JsonProcessingException, StockQuoteServiceException {
    String uri = buildUri(symbol);
    String responses = restTemplate.getForObject(uri, String.class);
    System.out.println(responses);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    AlphavantageDailyResponse dailyResponses = mapper
                              .readValue(responses, AlphavantageDailyResponse.class);
    try{
      List<Candle> stocks = dailyResponses.getCandles().entrySet().stream().filter(entry -> {
        LocalDate date = entry.getKey();
        return from.compareTo(date) * date.compareTo(to) >= 0;
      }).map(entry -> {
        entry.getValue().setDate(entry.getKey());
        return entry;
      }).map(entry -> entry.getValue()).collect(Collectors.toList());
        stocks.sort(Comparator.comparing(Candle::getDate));
        return stocks;
      }catch (Exception e){
        throw new StockQuoteServiceException("Limit reached");
      }
  }

  protected String buildUri(String symbol) {
    return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&outputsize=full&symbol=" + symbol + "&apikey="
        + this.apiKey;
  }

}
