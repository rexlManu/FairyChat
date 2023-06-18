package de.rexlmanu.fairychat.plugin.utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ComponentTypeAdapter extends TypeAdapter<Component> {
  @Override
  public void write(JsonWriter out, Component value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }

    out.value(GsonComponentSerializer.gson().serialize(value));
  }

  @Override
  public Component read(JsonReader in) throws IOException {
    return GsonComponentSerializer.gson().deserialize(in.nextString());
  }
}
