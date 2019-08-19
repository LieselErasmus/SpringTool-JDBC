package waffles.data;

import waffles.Order;

public interface OrderRepository {

  Order save(Order order);
  
}
