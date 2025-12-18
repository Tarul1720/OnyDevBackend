package com.devcom.OnlyDev.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.UUID;


@Data
@Document(collection = "user-interactions")
public class UserInteractions {
    @Id
    private String id;
    private String userId;
    private HashMap<String,Double> tagInteractions=new HashMap<>();
    private HashMap<String,Double> authorInteractions=new HashMap<>();
    private HashMap<String,Double> contentInteractions=new HashMap<>();
    public UserInteractions() {
        this.id = UUID.randomUUID().toString(); // Auto-generate UUID
    }

}
