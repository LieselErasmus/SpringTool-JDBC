package waffles.data;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import waffles.Ingredient;
import waffles.Waffle;

@Repository
public class JdbcWaffleRepository implements WaffleRepository {

  private JdbcTemplate jdbc;

  public JdbcWaffleRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Waffle save(Waffle waffle) {
    long waffleId = saveWaffleInfo(waffle);
    waffle.setId(waffleId);
    for (Ingredient ingredient : waffle.getIngredients()) {
      saveIngredientToWaffle(ingredient, waffleId);
    }

    return waffle;
  }

  private long saveWaffleInfo(Waffle waffle) {
    waffle.setCreatedAt(new Date());
    PreparedStatementCreator psc =
        new PreparedStatementCreatorFactory(
            "insert into Waffle (name, createdAt) values (?, ?)",
            Types.VARCHAR, Types.TIMESTAMP
        ).newPreparedStatementCreator(
            Arrays.asList(
                waffle.getName(),
                new Timestamp(waffle.getCreatedAt().getTime())));

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(psc, keyHolder);

    return keyHolder.getKey().longValue();
  }

  private void saveIngredientToWaffle(
          Ingredient ingredient, long waffleId) {
    jdbc.update(
        "insert into Waffle_Ingredients (waffle, ingredient) " +
        "values (?, ?)",
        waffleId, ingredient.getId());
  }

}
