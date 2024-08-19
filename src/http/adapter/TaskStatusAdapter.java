package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Status;

import java.io.IOException;

public class TaskStatusAdapter extends TypeAdapter<Status> {

    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        if (status == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(status.toString());
        }
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        String str = jsonReader.nextString();
        if (str.equals("null")) {
            return null;
        }
        return Status.valueOf(str);
    }
}
