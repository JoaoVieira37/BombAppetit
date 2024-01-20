package sirs.a14.bombappetit.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;
    private String username;
    private String publicKey;

    public AppUser() {
    }

    public AppUser(Integer id, String username, String publicKey) {
        this.id = id;
        this.username = username;
        this.publicKey = publicKey;
    }

    public AppUser(String username, String publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", publicKey=" + publicKey +
                '}';
    }
}
