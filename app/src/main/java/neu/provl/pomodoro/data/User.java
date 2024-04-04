package neu.provl.pomodoro.data;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
public class User {

    @Setter(value = AccessLevel.NONE)
    private String id;
    private String name;
    private String imageUrl;
    private boolean isOnline;

    private List<User> friends;

    private List<Plant> plants;
}
