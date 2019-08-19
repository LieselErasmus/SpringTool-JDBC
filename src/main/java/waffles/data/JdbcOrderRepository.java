// tag::core[]
package waffles.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import waffles.Waffle;
import waffles.Order;

@Repository
public class JdbcOrderRepository implements OrderRepository {

  private SimpleJdbcInsert orderInserter;
  private SimpleJdbcInsert orderWaffleInserter;
  private ObjectMapper objectMapper;

  @Autowired
  public JdbcOrderRepository(JdbcTemplate jdbc) {
    this.orderInserter = new SimpleJdbcInsert(jdbc)
        .withTableName("Waffle_Order")
        .usingGeneratedKeyColumns("id");

    this.orderWaffleInserter = new SimpleJdbcInsert(jdbc)
        .withTableName("Waffle_Order_Waffles");

    this.objectMapper = new ObjectMapper();
  }
// end::core[]

// tag::save[]
  @Override
  public Order save(Order order) {
    order.setPlacedAt(new Date());
    long orderId = saveOrderDetails(order);
    order.setId(orderId);
    List<Waffle> waffles = order.getWaffles();
    for (Waffle waffle : waffles) {
      saveWaffleToOrder(waffle, orderId);
    }

    return order;
  }

  private long saveOrderDetails(Order order) {
    @SuppressWarnings("unchecked")
    Map<String, Object> values =
        objectMapper.convertValue(order, Map.class);
    values.put("placedAt", order.getPlacedAt());

    long orderId =
        orderInserter
            .executeAndReturnKey(values)
            .longValue();
    return orderId;
  }

  private void saveWaffleToOrder(Waffle waffle, long orderId) {
    Map<String, Object> values = new HashMap<>();
    values.put("waffleOrder", orderId);
    values.put("waffle", waffle.getId());
    orderWaffleInserter.execute(values);
  }
// end::save[]

/*
// tag::core[]

...

// end::core[]
 */

// tag::core[]
}
// end::core[]
