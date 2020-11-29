package com.crio.warmup.stock;

public class Trade {
  private String symbol;
  private int quantity;
  private String purchaseDate;
  private String tradeType;

  /* ================ Setters ================= */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setPurchaseDate(String purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  public void setTradeType(String tradeType) {
    this.tradeType = tradeType;
  }

  /* ================ Getters ================= */
  public String getSymbol() {
    return symbol;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getPurchaseDate() {
    return purchaseDate;
  }

  public String getTradeType() {
    return tradeType;
  }

}