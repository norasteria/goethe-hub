package com.noralearn.goethehub.model;

import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {

  @CreationTimestamp
  private ZonedDateTime createdAt;

  @CreationTimestamp
  private ZonedDateTime updatedAt;
}
