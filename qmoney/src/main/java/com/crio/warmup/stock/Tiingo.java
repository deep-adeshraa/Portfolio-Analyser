package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.Candle;

import java.time.LocalDate;

public class Tiingo implements Candle {
  private LocalDate date;
  private Double close;
  private Double high;
  private Double low;
  private Double open;
  private long volume;
  private float adjClose;
  private float adjHigh;
  private float adjLow;
  private float adjOpen;
  private long adjVolume;
  private float divCash;
  private float splitFactor;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Double getClose() {
    return close;
  }

  public void setClose(Double close) {
    this.close = close;
  }

  public Double getHigh() {
    return high;
  }

  public void setHigh(Double high) {
    this.high = high;
  }

  public Double getLow() {
    return low;
  }

  public void setLow(Double low) {
    this.low = low;
  }

  public Double getOpen() {
    return open;
  }

  public void setOpen(Double open) {
    this.open = open;
  }

  public long getVolume() {
    return volume;
  }

  public void setVolume(long volume) {
    this.volume = volume;
  }

  public float getAdjClose() {
    return adjClose;
  }

  public void setAdjClose(float adjClose) {
    this.adjClose = adjClose;
  }

  public float getAdjHigh() {
    return adjHigh;
  }

  public void setAdjHigh(float adjHigh) {
    this.adjHigh = adjHigh;
  }

  public float getAdjLow() {
    return adjLow;
  }

  public void setAdjLow(float adjLow) {
    this.adjLow = adjLow;
  }

  public float getAdjOpen() {
    return adjOpen;
  }

  public void setAdjOpen(float adjOpen) {
    this.adjOpen = adjOpen;
  }

  public long getAdjVolume() {
    return adjVolume;
  }

  public void setAdjVolume(long adjVolume) {
    this.adjVolume = adjVolume;
  }

  public float getDivCash() {
    return divCash;
  }

  public void setDivCash(float divCash) {
    this.divCash = divCash;
  }

  public float getSplitFactor() {
    return splitFactor;
  }

  public void setSplitFactor(float splitFactor) {
    this.splitFactor = splitFactor;
  }  
}