package neu.provl.pomodoro.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
public class Song {

    @Setter(value = AccessLevel.NONE)
    private String id;

    private String name;
    private String author;

    private String thumbnailUrl;

    public String getPlaybackURL() {
        return "";
    }

}
