package com.anner.naming.nacos.bean;

import java.io.Serializable;
import java.util.Objects;

public class Pair<FIRST, SECOND> implements Serializable {
     private final FIRST first;
     private final SECOND second;

     public Pair(FIRST first, SECOND second) {
          this.first = first;
          this.second = second;
     }

     public FIRST getFirst() {
          return this.first;
     }

     public SECOND getSecond() {
          return this.second;
     }

     @Override
     public boolean equals(Object o) {
          if (o == this)
               return true;
          if (!(o instanceof Pair)) {
               return false;
          }
          Pair pair = (Pair) o;
          return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
     }

     @Override
     public int hashCode() {
          return Objects.hash(first, second);
     }
}
