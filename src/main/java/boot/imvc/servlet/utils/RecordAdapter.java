package boot.imvc.servlet.utils;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import boot.imvc.servlet.data.bean.Record;

/**
 * Created by EricLee on 2014/9/10.
 */
public class RecordAdapter implements JsonSerializer<Record>, JsonDeserializer<Record> {
  @Override
  public Record deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
    Record r = new Record();
    Map<String, Object> m = context.deserialize(jsonElement, LinkedHashMap.class);
    r.setColumns(m);
    return r;
  }

  @Override
  public JsonElement serialize(Record record, Type type, JsonSerializationContext context) {
    return context.serialize(record.getColumns());
  }
}
