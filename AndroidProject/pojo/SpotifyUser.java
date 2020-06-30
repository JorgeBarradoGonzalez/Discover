package jb.dam2.discover.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpotifyUser {
    private String birthdate;
    private String country;
    private String display_name;
    private String email;
    private String id;

    public SpotifyUser() {
        this.birthdate = "";
        this.country = "";
        this.display_name = "";
        this.email = "";
        this.id = "";
    }
}
