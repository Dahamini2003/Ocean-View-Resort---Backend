package com.oceanview.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;   // ✅ correct import for CDI
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import java.time.LocalDate;

@ApplicationScoped
public class JsonbConfigProvider {

  public static class LocalDateAdapter implements JsonbAdapter<LocalDate, String> {
    @Override
    public String adaptToJson(LocalDate obj) {
      return obj.toString();
    }

    @Override
    public LocalDate adaptFromJson(String obj) {
      return LocalDate.parse(obj);
    }
  }

  @Produces
  public Jsonb jsonb() {
    return JsonbBuilder.create(
            new JsonbConfig()
                    .withAdapters(new LocalDateAdapter())
                    .withNullValues(false)
    );
  }
}